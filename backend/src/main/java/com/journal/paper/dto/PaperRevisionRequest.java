package com.journal.paper.dto;

import jakarta.validation.constraints.NotBlank;

public record PaperRevisionRequest(
        @NotBlank String content,
        String changeLog,
        String filePath
) {
}
