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
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewBusinessLogicServiceImpl implements ReviewBusinessLogicService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewBusinessLogicServiceImpl.class);

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
    public Optional<ReviewView> getReview(final Long id) {
        if (id == null) throw new NullPointerException();
        if (id <= 0) throw new IllegalArgumentException();

        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();

        // --------------------------------------------------

        final Review review;
        try {
            review = this.reviewRepository.getReferenceById(id);
        } catch (EntityNotFoundException ignored) {
            return Optional.empty();
        }

        final long reviewUserId;
        reviewUserId = review.getReviewer().getId();

        if (currentUser.id() != reviewUserId) {
            return Optional.empty(); // this Review does not exist for this user.
        }

        // --------------------------------------------------

        final ReviewView reviewView = this.reviewMapper.convertReviewToReviewView(review);

        // --------------------------------------------------

        return Optional.of(reviewView);
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
    public ReviewView createReview(@Valid final CreateReviewRequest createReviewRequest, final boolean notify) {
        if (createReviewRequest == null) throw new NullPointerException();

        // Unpack.
        // --------------------------------------------------

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

        // Security
        // --------------------------------------------------

        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
        if (currentUser.id() != reviewerId) {
            throw new SecurityException("Authenticated reviewer does not match the review's reviewerId");
        }

        // --------------------------------------------------

        Review review = new Review();
        // review.setId(); // auto-generated
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setComments(comments);
        review.setRating(rating);
        review = this.reviewRepository.save(review);

        // --------------------------------------------------

        return this.reviewMapper.convertReviewToReviewView(review);
    }
}
