package com.journal.paper.model;

import com.journal.common.model.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "paper_versions")
public class PaperVersion extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id", nullable = false)
    private Paper paper;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "change_log", columnDefinition = "TEXT")
    private String changeLog;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "plagiarism_score")
    private Double plagiarismScore;

    @Column(name = "plagiarism_summary", columnDefinition = "TEXT")
    private String plagiarismSummary;

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;
}
