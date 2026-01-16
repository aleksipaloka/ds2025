package gr.hua.dit.ds2025.web.rest;

import gr.hua.dit.ds2025.core.service.ReviewBusinessLogicService;
import gr.hua.dit.ds2025.core.service.ReviewDataService;
import gr.hua.dit.ds2025.core.service.model.CreateReviewRequest;
import gr.hua.dit.ds2025.core.service.model.ReviewView;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/review", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReviewResource {

    private final ReviewDataService reviewDataService;

    public ReviewResource(ReviewDataService reviewDataService) {
        this.reviewDataService = reviewDataService;
    }


    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("")
    public List<ReviewView> reviews() {
        return this.reviewDataService.getAllReviews();
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("/{id}")
    public ReviewView byId(@PathVariable("id") long id) {
        return reviewDataService.getReviewById(id);
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("/user/{id}")
    public List<ReviewView> byUserId(@PathVariable("id") long id) {
        return reviewDataService.getAllReviewsFromToUser(id);
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("/user/from/{id}")
    public List<ReviewView> fromUser(@PathVariable("id") long id) {
        return reviewDataService.getAllReviewsFromUser(id);
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("/user/to/{id}")
    public List<ReviewView> toUser(@PathVariable("id") long id) {
        return reviewDataService.getAllReviewsToUser(id);
    }

    @PreAuthorize("hasAuthority('INTEGRATION_WRITE')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ReviewView create(@RequestBody @Valid CreateReviewRequest req) {
        return reviewDataService.createReview(req, false);
    }
}
