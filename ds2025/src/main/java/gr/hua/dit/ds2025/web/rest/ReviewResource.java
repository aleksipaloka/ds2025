package gr.hua.dit.ds2025.web.rest;

import gr.hua.dit.ds2025.core.repositories.ReviewRepository;
import gr.hua.dit.ds2025.core.service.ReviewBusinessLogicService;
import gr.hua.dit.ds2025.core.service.ReviewDataService;
import gr.hua.dit.ds2025.core.service.mapper.ReviewMapper;
import gr.hua.dit.ds2025.core.service.model.ReviewView;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/review", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReviewResource {

    private final ReviewDataService reviewDataService;
    private final ReviewRepository reviewRepository;
    private final ReviewBusinessLogicService reviewBusinessLogicService;
    private final ReviewMapper reviewMapper;

    public ReviewResource(ReviewDataService reviewDataService, ReviewRepository reviewRepository, ReviewBusinessLogicService reviewBusinessLogicService, ReviewMapper reviewMapper) {
        this.reviewDataService = reviewDataService;
        this.reviewRepository = reviewRepository;
        this.reviewBusinessLogicService = reviewBusinessLogicService;
        this.reviewMapper = reviewMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<ReviewView> viewAllReviews() {
        return reviewDataService.getAllReviews();
    }


    @GetMapping("/reviewsFromMe")
    public ResponseEntity<?> viewReviewsFromMe(@AuthenticationPrincipal Authentication auth) {
        try {
            final List<ReviewView> reviewViewList = this.reviewDataService.getAllReviews();
            if (reviewViewList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            final List<ReviewView> reviewsFromMe = reviewBusinessLogicService.getReviewsFromUser();

            if (reviewsFromMe.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(reviewsFromMe);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/reviewsToMe")
    public ResponseEntity<?> viewReviewsToMe(@AuthenticationPrincipal Authentication auth) {
        try {
            final List<ReviewView> reviewViewList = this.reviewDataService.getAllReviews();
            if (reviewViewList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            final List<ReviewView> reviewsToMe = reviewBusinessLogicService.getReviewsToUser();

            if (reviewsToMe.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(reviewsToMe);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }



}
