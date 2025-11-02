package com.journal.paper.dto;

import com.journal.paper.model.PaperStatus;
import com.journal.user.dto.UserSummary;
import java.time.OffsetDateTime;
import java.util.List;

public record PaperDetailResponse(
        Long id,
        String title,
        String abstractText,
        String keywords,
        PaperStatus status,
        Integer currentVersion,
        OffsetDateTime submittedAt,
        OffsetDateTime updatedAt,
        OffsetDateTime publishedAt,
        String decisionNotes,
        UserSummary author,
        UserSummary editor,
        List<PaperVersionResponse> versions
) {
}
