package com.journal.paper.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record PaperVersionResponse(
        Long id,
        Integer versionNumber,
        String changeLog,
        String filePath,
        Double plagiarismScore,
        String plagiarismSummary,
        OffsetDateTime submittedAt,
        List<PlagiarismMatch> matches
) {
    public record PlagiarismMatch(Long paperId, String paperTitle, Double score) {}
}
