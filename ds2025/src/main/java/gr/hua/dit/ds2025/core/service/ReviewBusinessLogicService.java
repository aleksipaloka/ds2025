package gr.hua.dit.ds2025.core.service;

import gr.hua.dit.ds2025.core.service.model.CreateReviewRequest;
import gr.hua.dit.ds2025.core.service.model.ReviewView;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface ReviewBusinessLogicService {

    List<ReviewView> getReviews();

    List<ReviewView> getReviewsToUser();

    List<ReviewView> getReviewsFromUser();

    ReviewView createReview(final CreateReviewRequest createReviewRequest, final boolean notify);

}
