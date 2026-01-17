package gr.hua.dit.ds2025.core.service.impl;

import gr.hua.dit.ds2025.core.model.Review;
import gr.hua.dit.ds2025.core.model.User;
import gr.hua.dit.ds2025.core.repositories.ReviewRepository;
import gr.hua.dit.ds2025.core.repositories.UserRepository;
import gr.hua.dit.ds2025.core.service.ReviewDataService;
import gr.hua.dit.ds2025.core.service.mapper.ReviewMapper;
import gr.hua.dit.ds2025.core.service.model.CreateReviewRequest;
import gr.hua.dit.ds2025.core.service.model.ReviewView;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReviewDataServiceImpl implements ReviewDataService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;

    public ReviewDataServiceImpl(final ReviewRepository reviewRepository,
                                 final ReviewMapper reviewMapper, UserRepository userRepository) {
        this.userRepository = userRepository;
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

    @Override
    public List<ReviewView> getAllReviewsFromToUser(long id) {
        final List<Review> reviewList = this.reviewRepository.findAll();
        final List<ReviewView> reviewViewList = reviewList
                .stream()
                .filter(review -> review.getReviewer().getId().equals(id) || review.getReviewee().getId().equals(id))
                .map(this.reviewMapper::convertReviewToReviewView)
                .toList();
        return reviewViewList;
    }

    @Override
    public List<ReviewView> getAllReviewsToUser(long id) {
        final List<Review> reviewList = this.reviewRepository.findAllByRevieweeId(id);
        final List<ReviewView> reviewViewList = reviewList
                .stream()
                .map(this.reviewMapper::convertReviewToReviewView)
                .toList();
        return reviewViewList;
    }

    @Override
    public List<ReviewView> getAllReviewsFromUser(long id) {
        final List<Review> reviewList = this.reviewRepository.findAllByReviewerId(id);
        final List<ReviewView> reviewViewList = reviewList
                .stream()
                .map(this.reviewMapper::convertReviewToReviewView)
                .toList();
        return reviewViewList;
    }

    @Override
    public ReviewView createReview(CreateReviewRequest createReviewRequest, boolean notify) {
        Review review = new Review();

        review.setReviewer(userRepository.findById(createReviewRequest.reviewerId())
                .orElseThrow(() -> new IllegalArgumentException("reviewer not found")));
        review.setReviewee(userRepository.findById(createReviewRequest.revieweeId())
                .orElseThrow(() -> new IllegalArgumentException("reviewer not found")));
        review.setRating(createReviewRequest.rating());
        review.setComments(createReviewRequest.comments());
        review = this.reviewRepository.save(review);
        return this.reviewMapper.convertReviewToReviewView(review);
    }


}