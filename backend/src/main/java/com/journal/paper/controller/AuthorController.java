package com.journal.paper.controller;

import com.journal.paper.dto.PaperDetailResponse;
import com.journal.paper.dto.PaperRevisionRequest;
import com.journal.paper.dto.PaperSubmissionRequest;
import com.journal.paper.dto.PaperSummary;
import com.journal.paper.service.PaperService;
import com.journal.user.model.UserAccount;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/author")
@PreAuthorize("hasRole('AUTHOR')")
public class AuthorController {

    private final PaperService paperService;

    public AuthorController(PaperService paperService) {
        this.paperService = paperService;
    }

    @GetMapping("/papers")
    public List<PaperSummary> myPapers(@AuthenticationPrincipal UserAccount author) {
        return paperService.listForAuthor(author);
    }

    @PostMapping("/papers")
    public PaperDetailResponse submitPaper(
            @AuthenticationPrincipal UserAccount author,
            @Valid @RequestBody PaperSubmissionRequest request) {
        return paperService.submitPaper(author, request);
    }

    @GetMapping("/papers/{paperId}")
    public PaperDetailResponse viewPaper(@PathVariable Long paperId, @AuthenticationPrincipal UserAccount author) {
        return paperService.getPaper(paperId, author);
    }

    @PostMapping("/papers/{paperId}/revisions")
    public PaperDetailResponse submitRevision(
            @PathVariable Long paperId,
            @AuthenticationPrincipal UserAccount author,
            @Valid @RequestBody PaperRevisionRequest request) {
        return paperService.submitRevision(paperId, request, author);
    }
}
