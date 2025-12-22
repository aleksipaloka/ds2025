package gr.hua.dit.ds2025.core.service.mapper;


import gr.hua.dit.ds2025.core.model.Review;
import gr.hua.dit.ds2025.core.service.model.ReviewView;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewMapper {

    private final UserMapper userMapper;

    public ReviewMapper(final UserMapper userMapper) {
        if (userMapper == null) throw new NullPointerException();
        this.userMapper = userMapper;
    }

    public ReviewView convertReviewToReviewView(final Review review){

        if (review == null){
            return null;
        }

        return new ReviewView(
                review.getId(),
                this.userMapper.convertUserToUserView(review.getReviewer()),
                this.userMapper.convertUserToUserView(review.getReviewee()),
                review.getRating(),
                review.getComments()
        );
    }

    public List<ReviewView> convertReviewToReviewView(final List<Review> reviews) {
        if (reviews == null) return Collections.emptyList();
        return reviews.stream()
                .map(this::convertReviewToReviewView) // καλεί την 1-1 μέθοδο
                .collect(Collectors.toList());
    }
}
