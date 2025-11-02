package com.journal.review.dto;

import com.journal.review.model.ReviewRecommendation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewSubmissionRequest(
        @NotNull ReviewRecommendation recommendation,
        @NotBlank String comments,
        String plagiarismObservations
) {
}
