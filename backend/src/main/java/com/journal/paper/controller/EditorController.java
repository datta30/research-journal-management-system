package com.journal.paper.controller;

import com.journal.paper.dto.PaperAssignmentRequest;
import com.journal.paper.dto.PaperDecisionRequest;
import com.journal.paper.dto.PaperDetailResponse;
import com.journal.paper.dto.PaperSummary;
import com.journal.paper.service.PaperService;
import com.journal.review.dto.ReviewSummary;
import com.journal.review.service.ReviewService;
import com.journal.user.dto.UserSummary;
import com.journal.user.model.RoleType;
import com.journal.user.model.UserAccount;
import com.journal.user.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/editor")
@PreAuthorize("hasRole('EDITOR')")
public class EditorController {

    private final PaperService paperService;
    private final ReviewService reviewService;
    private final UserService userService;

    public EditorController(PaperService paperService, ReviewService reviewService, UserService userService) {
        this.paperService = paperService;
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @GetMapping("/papers")
    public List<PaperSummary> queue(@AuthenticationPrincipal UserAccount editor) {
        return paperService.queueForEditor(editor);
    }

    @GetMapping("/papers/{paperId}")
    public PaperDetailResponse detail(@PathVariable Long paperId, @AuthenticationPrincipal UserAccount editor) {
        return paperService.getPaper(paperId, editor);
    }

    @GetMapping("/papers/{paperId}/reviews")
    public List<ReviewSummary> reviews(@PathVariable Long paperId, @AuthenticationPrincipal UserAccount editor) {
        paperService.getPaper(paperId, editor);
        return reviewService.reviewsForPaper(paperId);
    }

    @GetMapping("/reviewers")
    public List<UserSummary> reviewers() {
        return userService.listByRole(RoleType.REVIEWER);
    }

    @PostMapping("/papers/{paperId}/assign")
    public PaperDetailResponse assign(
            @PathVariable Long paperId,
            @AuthenticationPrincipal UserAccount editor,
            @Valid @RequestBody PaperAssignmentRequest request) {
        return paperService.assignReviewers(paperId, request, editor);
    }

    @PostMapping("/papers/{paperId}/decision")
    public PaperDetailResponse decide(
            @PathVariable Long paperId,
            @AuthenticationPrincipal UserAccount editor,
            @Valid @RequestBody PaperDecisionRequest request) {
        return paperService.recordDecision(paperId, request, editor);
    }
}
