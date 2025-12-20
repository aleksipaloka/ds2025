package gr.hua.dit.ds2025.core.repositories;

import gr.hua.dit.ds2025.core.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.*;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
