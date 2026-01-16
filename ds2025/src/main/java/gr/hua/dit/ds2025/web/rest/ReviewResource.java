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
        final List<ReviewView> reviewViewList = this.reviewDataService.getAllReviews();
        return reviewViewList;
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("/{id}")
    public ReviewView byId(@PathVariable("id") long id) {
        return reviewDataService.getReviewById(id);
    }

}
