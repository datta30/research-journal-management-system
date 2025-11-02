package com.journal.plagiarism.dto;

import java.util.List;

public record PlagiarismInsight(Double score, List<SimilarPaper> matches) {
    public record SimilarPaper(Long paperId, String title, Double score) {}
}
