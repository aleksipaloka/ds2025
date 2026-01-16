package gr.hua.dit.ds2025.core.service.impl;

import gr.hua.dit.ds2025.core.model.Review;
import gr.hua.dit.ds2025.core.repositories.ReviewRepository;
import gr.hua.dit.ds2025.core.service.ReviewDataService;
import gr.hua.dit.ds2025.core.service.mapper.ReviewMapper;
import gr.hua.dit.ds2025.core.service.model.ReviewView;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewDataServiceImpl implements ReviewDataService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    public ReviewDataServiceImpl(final ReviewRepository reviewRepository,
                                 final ReviewMapper reviewMapper) {
        if (reviewRepository == null) throw new NullPointerException();
        if (reviewMapper == null) throw new NullPointerException();
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public List<ReviewView> getAllReviews() {
        final List<Review> reviewList = this.reviewRepository.findAll();
        final List<ReviewView> reviewViewList = reviewList
                .stream()
                .map(this.reviewMapper::convertReviewToReviewView)
                .toList();
        return reviewViewList;
    }

    @Override
    public ReviewView getReviewById(long reviewId) {
        final Review review;
        review = this.reviewRepository.getReferenceById(reviewId);

        return this.reviewMapper.convertReviewToReviewView(review);
    }
}