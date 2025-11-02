package com.journal.paper.controller;

import com.journal.paper.dto.PaperSummary;
import com.journal.paper.model.PaperStatus;
import com.journal.paper.repository.PaperRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/papers")
public class PublicPaperController {

    private final PaperRepository paperRepository;

    public PublicPaperController(PaperRepository paperRepository) {
        this.paperRepository = paperRepository;
    }

    @GetMapping("/published")
    public List<PaperSummary> published() {
        return paperRepository.findByStatus(PaperStatus.PUBLISHED).stream()
                .map(paper -> new PaperSummary(
                        paper.getId(),
                        paper.getTitle(),
                        paper.getStatus(),
                        paper.getCurrentVersion(),
                        paper.getSubmittedAt(),
                        paper.getUpdatedAt(),
                        paper.getEditor() != null ? paper.getEditor().getFullName() : null))
                .collect(Collectors.toList());
    }
}
