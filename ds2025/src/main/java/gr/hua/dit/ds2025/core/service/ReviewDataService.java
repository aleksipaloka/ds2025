package gr.hua.dit.ds2025.core.service;

import gr.hua.dit.ds2025.core.service.model.ReviewView;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ReviewDataService {

    List<ReviewView> getAllReviews();

    ReviewView getReviewById(long reviewId);
}
