package com.journal.paper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.journal.common.exception.BusinessRuleException;
import com.journal.common.exception.ResourceNotFoundException;
import com.journal.notification.model.NotificationType;
import com.journal.notification.service.NotificationService;
import com.journal.paper.dto.PaperAssignmentRequest;
import com.journal.paper.dto.PaperDecisionRequest;
import com.journal.paper.dto.PaperDetailResponse;
import com.journal.paper.dto.PaperRevisionRequest;
import com.journal.paper.dto.PaperSubmissionRequest;
import com.journal.paper.dto.PaperSummary;
import com.journal.paper.dto.PaperVersionResponse;
import com.journal.paper.model.DecisionOutcome;
import com.journal.paper.model.Paper;
import com.journal.paper.model.PaperStatus;
import com.journal.paper.model.PaperVersion;
import com.journal.paper.repository.PaperRepository;
import com.journal.paper.repository.PaperVersionRepository;
import com.journal.plagiarism.dto.PlagiarismInsight;
import com.journal.plagiarism.service.PlagiarismService;
import com.journal.review.model.Review;
import com.journal.review.model.ReviewStatus;
import com.journal.review.repository.ReviewRepository;
import com.journal.user.dto.UserSummary;
import com.journal.user.model.RoleType;
import com.journal.user.model.UserAccount;
import com.journal.user.service.UserService;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PaperService {

    private final PaperRepository paperRepository;
    private final PaperVersionRepository paperVersionRepository;
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final PlagiarismService plagiarismService;
    private final ObjectMapper objectMapper;

    private static final TypeReference<List<PaperVersionResponse.PlagiarismMatch>> MATCH_TYPE =
            new TypeReference<>() {};

    public PaperService(PaperRepository paperRepository,
            PaperVersionRepository paperVersionRepository,
            ReviewRepository reviewRepository,
            UserService userService,
            NotificationService notificationService,
            PlagiarismService plagiarismService,
            ObjectMapper objectMapper) {
        this.paperRepository = paperRepository;
        this.paperVersionRepository = paperVersionRepository;
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.notificationService = notificationService;
        this.plagiarismService = plagiarismService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PaperDetailResponse submitPaper(UserAccount author, PaperSubmissionRequest request) {
        Paper paper = Paper.builder()
                .title(request.title())
                .abstractText(request.abstractText())
                .keywords(request.keywords())
                .status(PaperStatus.SUBMITTED)
                .author(author)
                .submittedAt(OffsetDateTime.now())
                .currentVersion(1)
                .build();

        PaperVersion version = PaperVersion.builder()
                .versionNumber(1)
                .content(request.content())
                .changeLog(request.changeLog())
                .filePath(request.filePath())
                .submittedAt(OffsetDateTime.now())
                .build();

        paper.addVersion(version);
        evaluateAndAttachPlagiarism(paper, version);
        Paper saved = paperRepository.save(paper);
        return toDetailResponse(saved);
    }

    public List<PaperSummary> listForAuthor(UserAccount author) {
        return paperRepository.findByAuthor(author).stream()
                .sorted(Comparator.comparing(Paper::getCreatedAt).reversed())
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    public PaperDetailResponse getPaper(Long paperId, UserAccount requester) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found"));
        if (!canViewPaper(paper, requester)) {
            throw new BusinessRuleException("You are not allowed to view this paper");
        }
        return toDetailResponse(paper);
    }

    @Transactional
    public PaperDetailResponse submitRevision(Long paperId, PaperRevisionRequest request, UserAccount author) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found"));
        if (!paper.getAuthor().getId().equals(author.getId())) {
            throw new BusinessRuleException("Only the author can submit revisions");
        }
        if (paper.getStatus() != PaperStatus.REVISIONS_REQUESTED && paper.getStatus() != PaperStatus.IN_REVIEW) {
            throw new BusinessRuleException("Paper is not awaiting revision");
        }
        int nextVersion = paper.getCurrentVersion() == null ? 1 : paper.getCurrentVersion() + 1;
        PaperVersion version = PaperVersion.builder()
                .paper(paper)
                .versionNumber(nextVersion)
                .content(request.content())
                .changeLog(request.changeLog())
                .filePath(request.filePath())
                .submittedAt(OffsetDateTime.now())
                .build();
        paper.addVersion(version);
        paper.setCurrentVersion(nextVersion);
        paper.setStatus(PaperStatus.IN_REVIEW);
        evaluateAndAttachPlagiarism(paper, version);
        Paper saved = paperRepository.save(paper);
        if (paper.getEditor() != null) {
            notificationService.notify(paper.getEditor(), NotificationType.REVISION_REQUEST,
                    "Revision submitted for paper '%s'".formatted(paper.getTitle()),
                    "/editor/papers/%d".formatted(paper.getId()));
        }
        return toDetailResponse(saved);
    }

    @Transactional
    public PaperDetailResponse assignReviewers(Long paperId, PaperAssignmentRequest request, UserAccount editor) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found"));
        if (editor.getRole() != RoleType.EDITOR) {
            throw new BusinessRuleException("Only editors can assign reviewers");
        }
        paper.setEditor(editor);
        paper.setStatus(PaperStatus.IN_REVIEW);

        List<Review> reviews = new ArrayList<>();
        for (Long reviewerId : request.reviewerIds()) {
            UserAccount reviewer = userService.getById(reviewerId);
            if (reviewer.getRole() != RoleType.REVIEWER) {
                throw new BusinessRuleException("User %s is not a reviewer".formatted(reviewer.getEmail()));
            }
            Review review = Review.builder()
                    .paper(paper)
                    .paperVersion(latestVersion(paper))
                    .reviewer(reviewer)
                    .status(ReviewStatus.PENDING)
                    .dueAt(request.dueAt())
                    .build();
            reviews.add(review);
            notificationService.notify(reviewer, NotificationType.ASSIGNMENT,
                    "You have been assigned to review paper '%s'".formatted(paper.getTitle()),
                    "/reviewer/assignments/%d".formatted(paper.getId()));
        }
        reviewRepository.saveAll(reviews);
        Paper saved = paperRepository.save(paper);
        return toDetailResponse(saved);
    }

    @Transactional
    public PaperDetailResponse recordDecision(Long paperId, PaperDecisionRequest request, UserAccount editor) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found"));
        if (editor.getRole() != RoleType.EDITOR) {
            throw new BusinessRuleException("Only editors can record decisions");
        }
        DecisionOutcome outcome = request.outcome();
        switch (outcome) {
            case ACCEPT -> {
                paper.setStatus(PaperStatus.PUBLISHED);
                paper.setPublishedAt(OffsetDateTime.now());
            }
            case REJECT -> paper.setStatus(PaperStatus.REJECTED);
            case REQUEST_REVISION -> paper.setStatus(PaperStatus.REVISIONS_REQUESTED);
            default -> throw new BusinessRuleException("Unsupported decision");
        }
        paper.setDecisionNotes(request.notes());
        paper.setEditor(editor);
        Paper saved = paperRepository.save(paper);
        notificationService.notify(paper.getAuthor(), NotificationType.DECISION,
                "Decision recorded for paper '%s': %s".formatted(paper.getTitle(), outcome.name()),
                "/author/papers/%d".formatted(paper.getId()));
        return toDetailResponse(saved);
    }

    public List<PaperSummary> queueForEditor(UserAccount editor) {
        return paperRepository.findByStatuses(List.of(PaperStatus.SUBMITTED, PaperStatus.IN_REVIEW, PaperStatus.REVISIONS_REQUESTED)).stream()
                .filter(paper -> paper.getEditor() == null || paper.getEditor().getId().equals(editor.getId()))
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    private boolean canViewPaper(Paper paper, UserAccount requester) {
        if (requester.getRole() == RoleType.EDITOR || requester.getRole() == RoleType.ADMIN) {
            return true;
        }
        if (paper.getAuthor().getId().equals(requester.getId())) {
            return true;
        }
        return reviewRepository.findByPaper(paper).stream()
                .anyMatch(review -> review.getReviewer().getId().equals(requester.getId()));
    }

    private PaperVersion latestVersion(Paper paper) {
        return paperVersionRepository.findFirstByPaperOrderByVersionNumberDesc(paper)
                .orElseThrow(() -> new BusinessRuleException("Paper has no versions"));
    }

    private void evaluateAndAttachPlagiarism(Paper paper, PaperVersion version) {
        PlagiarismInsight insight = plagiarismService.evaluateSimilarity(version.getContent(), paper.getId());
        version.setPlagiarismScore(insight.score());
        try {
            version.setPlagiarismSummary(objectMapper.writeValueAsString(insight.matches()));
        } catch (JsonProcessingException e) {
            throw new BusinessRuleException("Unable to serialize plagiarism insights");
        }
    }

    private PaperDetailResponse toDetailResponse(Paper paper) {
        Paper managed = paperRepository.findById(paper.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found"));
        List<PaperVersionResponse> versions = managed.getVersions().stream()
                .sorted(Comparator.comparing(PaperVersion::getVersionNumber).reversed())
                .map(this::toVersionResponse)
                .collect(Collectors.toList());
        UserSummary authorSummary = userService.toSummary(managed.getAuthor());
        UserSummary editorSummary = managed.getEditor() != null ? userService.toSummary(managed.getEditor()) : null;
        return new PaperDetailResponse(
                managed.getId(),
                managed.getTitle(),
                managed.getAbstractText(),
                managed.getKeywords(),
                managed.getStatus(),
                managed.getCurrentVersion(),
                managed.getSubmittedAt(),
                managed.getUpdatedAt(),
                managed.getPublishedAt(),
                managed.getDecisionNotes(),
                authorSummary,
                editorSummary,
                versions);
    }

    private PaperVersionResponse toVersionResponse(PaperVersion version) {
        List<PaperVersionResponse.PlagiarismMatch> matches = List.of();
        if (version.getPlagiarismSummary() != null) {
            try {
                matches = objectMapper.readValue(version.getPlagiarismSummary(), MATCH_TYPE);
            } catch (JsonProcessingException ignored) {
            }
        }
        return new PaperVersionResponse(
                version.getId(),
                version.getVersionNumber(),
                version.getChangeLog(),
                version.getFilePath(),
                version.getPlagiarismScore(),
                version.getPlagiarismSummary(),
                version.getSubmittedAt(),
                matches);
    }

    private PaperSummary toSummary(Paper paper) {
        String editorName = paper.getEditor() != null ? paper.getEditor().getFullName() : null;
        return new PaperSummary(
                paper.getId(),
                paper.getTitle(),
                paper.getStatus(),
                paper.getCurrentVersion(),
                paper.getSubmittedAt(),
                paper.getUpdatedAt(),
                editorName);
    }
}
