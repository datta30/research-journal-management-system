package com.journal.paper.dto;

import com.journal.paper.model.DecisionOutcome;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaperDecisionRequest(
        @NotNull DecisionOutcome outcome,
        @NotBlank String notes
) {
}
