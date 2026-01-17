package gr.hua.dit.ds2025.core.service.impl;

import gr.hua.dit.ds2025.core.model.Review;
import gr.hua.dit.ds2025.core.model.User;
import gr.hua.dit.ds2025.core.repositories.ReviewRepository;
import gr.hua.dit.ds2025.core.repositories.UserRepository;
import gr.hua.dit.ds2025.core.security.CurrentUser;
import gr.hua.dit.ds2025.core.security.CurrentUserProvider;
import gr.hua.dit.ds2025.core.service.ReviewBusinessLogicService;

import gr.hua.dit.ds2025.core.service.mapper.ReviewMapper;

import gr.hua.dit.ds2025.core.service.model.CreateReviewRequest;
import gr.hua.dit.ds2025.core.service.model.ReviewView;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewBusinessLogicServiceImpl implements ReviewBusinessLogicService {

    private final ReviewMapper reviewMapper;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    public ReviewBusinessLogicServiceImpl(final ReviewMapper reviewMapper,
                                          final ReviewRepository reviewRepository,
                                          final UserRepository userRepository,
                                          final CurrentUserProvider currentUserProvider) {
        if (reviewMapper == null) throw new NullPointerException();
        if (reviewRepository == null) throw new NullPointerException();
        if (userRepository == null) throw new NullPointerException();
        if (currentUserProvider == null) throw new NullPointerException();

        this.reviewMapper = reviewMapper;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public List<ReviewView> getReviews() {
        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
        final List<Review> reviewList;

        reviewList = this.reviewRepository.findAll();
        return reviewList.stream()
                .map(this.reviewMapper::convertReviewToReviewView)
                .toList();
    }

    @Override
    public List<ReviewView> getReviewsToUser() {
        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
        final List<Review> reviewList;

        reviewList = this.reviewRepository.findAllByRevieweeId(currentUser.id());
        return reviewList.stream()
                .map(this.reviewMapper::convertReviewToReviewView)
                .toList();
    }

    @Override
    public List<ReviewView> getReviewsFromUser() {
        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
        final List<Review> reviewList;

        reviewList = this.reviewRepository.findAllByReviewerId(currentUser.id());
        return reviewList.stream()
                .map(this.reviewMapper::convertReviewToReviewView)
                .toList();
    }

    @Transactional
    @Override
    public void createReview(@Valid final CreateReviewRequest createReviewRequest, final boolean notify) {
        if (createReviewRequest == null) throw new NullPointerException();

        final long reviewerId = createReviewRequest.reviewerId();
        final long revieweeId = createReviewRequest.revieweeId();
        final int rating = createReviewRequest.rating();
        final String comments = createReviewRequest.comments();

        if (reviewerId == revieweeId) {
            throw new IllegalArgumentException("You cannot review yourself.");
        }

        final User reviewer = this.userRepository.findById(reviewerId)
                .orElseThrow(() -> new IllegalArgumentException("reviewer not found"));
        final User reviewee = this.userRepository.findById(revieweeId)
                .orElseThrow(() -> new IllegalArgumentException("reviewee not found"));

        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
        if (currentUser.id() != reviewerId) {
            throw new SecurityException("Authenticated reviewer does not match the review's reviewerId");
        }

        Review review = new Review();
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setComments(comments);
        review.setRating(rating);
        review = this.reviewRepository.save(review);

        this.reviewMapper.convertReviewToReviewView(review);
    }
}
