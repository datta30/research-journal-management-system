package com.journal.paper.repository;

import com.journal.paper.model.Paper;
import com.journal.paper.model.PaperVersion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperVersionRepository extends JpaRepository<PaperVersion, Long> {
    List<PaperVersion> findByPaperOrderByVersionNumberDesc(Paper paper);
    Optional<PaperVersion> findFirstByPaperOrderByVersionNumberDesc(Paper paper);
}
