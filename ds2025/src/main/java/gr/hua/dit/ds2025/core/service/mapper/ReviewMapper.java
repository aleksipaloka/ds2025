package gr.hua.dit.ds2025.core.service.mapper;

import gr.hua.dit.ds2025.core.model.Review;
import gr.hua.dit.ds2025.core.service.model.ReviewView;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewMapper {

    private final UserSummaryMapper userSummaryMapper;

    public ReviewMapper(final UserSummaryMapper userSummaryMapper) {
        if (userSummaryMapper == null) {
            throw new NullPointerException("UserSummaryMapper is null");
        }
        this.userSummaryMapper = userSummaryMapper;
    }

    public ReviewView convertReviewToReviewView(final Review review) {
        if (review == null) {
            return null;
        }

        return new ReviewView(
                review.getId(),
                userSummaryMapper.convertUserToUserSummaryView(review.getReviewer()),
                userSummaryMapper.convertUserToUserSummaryView(review.getReviewee()),
                review.getRating(),
                review.getComments()
        );
    }

    public List<ReviewView> convertReviewToReviewView(final List<Review> reviews) {
        if (reviews == null) {
            return Collections.emptyList();
        }

        return reviews.stream()
                .map(this::convertReviewToReviewView)
                .collect(Collectors.toList());
    }
}
