package com.journal.review.dto;

import com.journal.review.model.ReviewRecommendation;
import com.journal.review.model.ReviewStatus;
import com.journal.user.dto.UserSummary;
import java.time.OffsetDateTime;

public record ReviewSummary(
        Long id,
        Long paperId,
        Long paperVersionId,
        ReviewStatus status,
        ReviewRecommendation recommendation,
        String comments,
        OffsetDateTime submittedAt,
        OffsetDateTime dueAt,
        UserSummary reviewer
) {
}
