package gr.hua.dit.ds2025.core.repositories;

import gr.hua.dit.ds2025.core.model.Review;
import gr.hua.dit.ds2025.core.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByReviewerId(long reviewerId);

    List<Review> findAllByRevieweeId(long revieweeId);

}
