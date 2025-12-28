package gr.hua.dit.ds2025.web.rest;

import gr.hua.dit.ds2025.core.service.ReviewDataService;
import gr.hua.dit.ds2025.core.service.model.ReviewView;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/review", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReviewResource {

    private final ReviewDataService reviewDataService;

    public ReviewResource(final ReviewDataService reviewDataService) {
        if (reviewDataService == null) throw new NullPointerException();
        this.reviewDataService = reviewDataService;
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("")
    public List<ReviewView> reviews() {
        final List<ReviewView> reviewViewList = this.reviewDataService.getAllReviews();
        return reviewViewList;
    }

}
