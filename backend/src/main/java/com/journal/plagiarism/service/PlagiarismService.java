package com.journal.plagiarism.service;

import com.journal.paper.model.Paper;
import com.journal.paper.repository.PaperRepository;
import com.journal.paper.repository.PaperVersionRepository;
import com.journal.plagiarism.dto.PlagiarismInsight;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PlagiarismService {

    private final PaperRepository paperRepository;
    private final PaperVersionRepository paperVersionRepository;

    public PlagiarismService(PaperRepository paperRepository, PaperVersionRepository paperVersionRepository) {
        this.paperRepository = paperRepository;
        this.paperVersionRepository = paperVersionRepository;
    }

    public PlagiarismInsight evaluateSimilarity(String content, Long excludePaperId) {
        if (content == null || content.isBlank()) {
            return new PlagiarismInsight(0d, List.of());
        }
        Set<String> sourceTokens = tokenize(content);
        List<PlagiarismInsight.SimilarPaper> matches = new ArrayList<>();
        for (Paper paper : paperRepository.findAll()) {
            if (excludePaperId != null && excludePaperId.equals(paper.getId())) {
                continue;
            }
            paperVersionRepository.findFirstByPaperOrderByVersionNumberDesc(paper).ifPresent(latest -> {
                Set<String> targetTokens = tokenize(latest.getContent());
                double score = jaccardSimilarity(sourceTokens, targetTokens);
                if (score >= 0.4d) {
                    matches.add(new PlagiarismInsight.SimilarPaper(paper.getId(), paper.getTitle(), round(score)));
                }
            });
        }
        matches.sort(Comparator.comparing(PlagiarismInsight.SimilarPaper::score).reversed());
        double maxScore = matches.stream().map(PlagiarismInsight.SimilarPaper::score).max(Double::compareTo).orElse(0d);
        return new PlagiarismInsight(round(maxScore), matches);
    }

    private double jaccardSimilarity(Set<String> first, Set<String> second) {
        if (first.isEmpty() || second.isEmpty()) {
            return 0d;
        }
        Set<String> intersection = new HashSet<>(first);
        intersection.retainAll(second);
        Set<String> union = new HashSet<>(first);
        union.addAll(second);
        return (double) intersection.size() / (double) union.size();
    }

    private Set<String> tokenize(String content) {
        if (content == null) {
            return Set.of();
        }
        return List.of(content.toLowerCase(Locale.ENGLISH).split("\\W+")).stream()
                .filter(token -> token.length() > 2)
                .collect(Collectors.toSet());
    }

    private double round(double value) {
        return Math.round(value * 100d) / 100d;
    }
}
