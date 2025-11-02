package com.journal.paper.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PaperSubmissionRequest(
        @NotBlank @Size(max = 300) String title,
        @NotBlank String abstractText,
        String keywords,
        @NotBlank String content,
        String changeLog,
        String filePath
) {
}
