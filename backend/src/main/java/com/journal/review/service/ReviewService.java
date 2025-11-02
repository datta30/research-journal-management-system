package com.journal.review.service;

import com.journal.common.exception.BusinessRuleException;
import com.journal.common.exception.ResourceNotFoundException;
import com.journal.notification.model.NotificationType;
import com.journal.notification.service.NotificationService;
import com.journal.paper.model.Paper;
import com.journal.paper.repository.PaperRepository;
import com.journal.review.dto.ReviewSubmissionRequest;
import com.journal.review.dto.ReviewSummary;
import com.journal.review.model.Review;
import com.journal.review.model.ReviewStatus;
import com.journal.review.repository.ReviewRepository;
import com.journal.user.dto.UserSummary;
import com.journal.user.model.UserAccount;
import com.journal.user.service.UserService;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PaperRepository paperRepository;
    private final NotificationService notificationService;
    private final UserService userService;

    public ReviewService(ReviewRepository reviewRepository,
            PaperRepository paperRepository,
            NotificationService notificationService,
            UserService userService) {
        this.reviewRepository = reviewRepository;
        this.paperRepository = paperRepository;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    public List<ReviewSummary> assignmentsForReviewer(UserAccount reviewer) {
        return reviewRepository.findByReviewer(reviewer).stream()
                .sorted(Comparator.comparing(Review::getDueAt))
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewSummary submitReview(Long reviewId, ReviewSubmissionRequest request, UserAccount reviewer) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review assignment not found"));
        if (!review.getReviewer().getId().equals(reviewer.getId())) {
            throw new BusinessRuleException("You are not assigned to this review");
        }
        if (review.getStatus() == ReviewStatus.SUBMITTED) {
            throw new BusinessRuleException("Review already submitted");
        }
        review.setRecommendation(request.recommendation());
        review.setComments(request.comments());
        review.setPlagiarismObservations(request.plagiarismObservations());
        review.setSubmittedAt(OffsetDateTime.now());
        review.setStatus(ReviewStatus.SUBMITTED);
        Review saved = reviewRepository.save(review);

        Paper paper = paperRepository.findById(review.getPaper().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found"));
        if (paper.getEditor() != null) {
            notificationService.notify(paper.getEditor(), NotificationType.COMMENT,
                    "Review submitted for '%s'".formatted(paper.getTitle()),
                    "/editor/papers/%d".formatted(paper.getId()));
        }
        notificationService.notify(paper.getAuthor(), NotificationType.COMMENT,
                "A reviewer added comments to '%s'".formatted(paper.getTitle()),
                "/author/papers/%d".formatted(paper.getId()));

        return toSummary(saved);
    }

    public List<ReviewSummary> reviewsForPaper(Long paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found"));
        return reviewRepository.findByPaper(paper).stream()
                .sorted(Comparator.comparing(Review::getSubmittedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    private ReviewSummary toSummary(Review review) {
        UserSummary reviewerSummary = userService.toSummary(review.getReviewer());
        return new ReviewSummary(
                review.getId(),
                review.getPaper().getId(),
                review.getPaperVersion().getId(),
                review.getStatus(),
                review.getRecommendation(),
                review.getComments(),
                review.getSubmittedAt(),
                review.getDueAt(),
                reviewerSummary);
    }
}
