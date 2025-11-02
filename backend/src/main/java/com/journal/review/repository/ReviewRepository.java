package com.journal.review.repository;

import com.journal.paper.model.Paper;
import com.journal.review.model.Review;
import com.journal.review.model.ReviewStatus;
import com.journal.user.model.UserAccount;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewer(UserAccount reviewer);
    List<Review> findByReviewerAndStatus(UserAccount reviewer, ReviewStatus status);
    List<Review> findByPaper(Paper paper);
}
