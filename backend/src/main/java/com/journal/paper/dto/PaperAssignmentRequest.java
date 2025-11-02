package com.journal.paper.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;

public record PaperAssignmentRequest(
        @NotEmpty List<Long> reviewerIds,
        @NotNull @Future OffsetDateTime dueAt
) {
}
