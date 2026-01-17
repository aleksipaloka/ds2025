package gr.hua.dit.ds2025.core.service;

import gr.hua.dit.ds2025.core.service.model.CreateReviewRequest;
import gr.hua.dit.ds2025.core.service.model.ReviewView;

import java.util.List;

public interface ReviewBusinessLogicService {

    List<ReviewView> getReviews();

    List<ReviewView> getReviewsToUser();

    List<ReviewView> getReviewsFromUser();

    void createReview(final CreateReviewRequest createReviewRequest, final boolean notify);

}
