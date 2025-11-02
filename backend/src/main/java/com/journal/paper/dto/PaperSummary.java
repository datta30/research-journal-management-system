package com.journal.paper.dto;

import com.journal.paper.model.PaperStatus;
import java.time.OffsetDateTime;

public record PaperSummary(
        Long id,
        String title,
        PaperStatus status,
        Integer currentVersion,
        OffsetDateTime submittedAt,
        OffsetDateTime updatedAt,
        String editorName
) {
}
