package com.journal.paper.repository;

import com.journal.paper.model.Paper;
import com.journal.paper.model.PaperStatus;
import com.journal.user.model.UserAccount;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaperRepository extends JpaRepository<Paper, Long> {
    List<Paper> findByAuthor(UserAccount author);
    List<Paper> findByEditor(UserAccount editor);
    List<Paper> findByStatus(PaperStatus status);

    @Query("select p from Paper p where p.status in :statuses order by p.createdAt desc")
    List<Paper> findByStatuses(List<PaperStatus> statuses);
}
