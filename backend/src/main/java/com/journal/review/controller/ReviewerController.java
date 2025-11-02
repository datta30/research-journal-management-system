package com.journal.review.controller;

import com.journal.review.dto.ReviewSubmissionRequest;
import com.journal.review.dto.ReviewSummary;
import com.journal.review.service.ReviewService;
import com.journal.user.model.UserAccount;
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
@RequestMapping("/api/reviewer")
@PreAuthorize("hasRole('REVIEWER')")
public class ReviewerController {

    private final ReviewService reviewService;

    public ReviewerController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/assignments")
    public List<ReviewSummary> assignments(@AuthenticationPrincipal UserAccount reviewer) {
        return reviewService.assignmentsForReviewer(reviewer);
    }

    @PostMapping("/assignments/{reviewId}/submit")
    public ReviewSummary submit(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserAccount reviewer,
            @Valid @RequestBody ReviewSubmissionRequest request) {
        return reviewService.submitReview(reviewId, request, reviewer);
    }
}
