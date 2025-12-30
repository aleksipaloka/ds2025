package gr.hua.dit.ds2025.web.ui;

import gr.hua.dit.ds2025.core.security.CurrentUser;
import gr.hua.dit.ds2025.core.security.CurrentUserProvider;
import gr.hua.dit.ds2025.core.service.ReviewBusinessLogicService;
import gr.hua.dit.ds2025.core.service.TripBusinessLogicService;
import gr.hua.dit.ds2025.core.service.model.CreateReviewRequest;
import gr.hua.dit.ds2025.core.service.model.TripView;
import gr.hua.dit.ds2025.web.ui.model.CreateReviewForm;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class ReviewController {

    private final ReviewBusinessLogicService reviewBusinessLogicService;
    private final TripBusinessLogicService tripBusinessLogicService;
    private final CurrentUserProvider currentUserProvider;

    public ReviewController(
            final ReviewBusinessLogicService reviewBusinessLogicService,
            final TripBusinessLogicService tripBusinessLogicService,
            final CurrentUserProvider currentUserProvider
    ) {
        if (reviewBusinessLogicService == null) throw new NullPointerException();
        if (tripBusinessLogicService == null) throw new NullPointerException();
        if (currentUserProvider == null) throw new NullPointerException();
        this.reviewBusinessLogicService = reviewBusinessLogicService;
        this.tripBusinessLogicService = tripBusinessLogicService;
        this.currentUserProvider = currentUserProvider;
    }

    private void validateReviewAllowed(CurrentUser currentUser, long revieweeId, Long tripId) {
        if (currentUser.id() == revieweeId) {
            throw new IllegalArgumentException("You cannot review yourself.");
        }

        if (tripId == null) return;

        final TripView trip = tripBusinessLogicService.getTrip(tripId).orElseThrow(
                () -> new IllegalArgumentException("Trip not found")
        );

        final LocalDateTime now = LocalDateTime.now();
        if (trip.departureTime() == null || !now.isAfter(trip.departureTime())) {
            throw new IllegalStateException("Reviews are available only after the trip starts.");
        }

        final boolean reviewerIsDriver = trip.driver() != null && trip.driver().id() != null
                && trip.driver().id().equals(currentUser.id());
        final boolean reviewerIsPassenger = trip.passengers() != null
                && trip.passengers().stream().anyMatch(p -> p.id() != null && p.id().equals(currentUser.id()));

        if (!reviewerIsDriver && !reviewerIsPassenger) {
            throw new SecurityException("not authorized");
        }

        final boolean revieweeIsDriver = trip.driver() != null && trip.driver().id() != null
                && trip.driver().id().equals(revieweeId);
        final boolean revieweeIsPassenger = trip.passengers() != null
                && trip.passengers().stream().anyMatch(p -> p.id() != null && p.id().equals(revieweeId));

        if (!revieweeIsDriver && !revieweeIsPassenger) {
            throw new IllegalArgumentException("Reviewee is not part of this trip.");
        }
    }

    @GetMapping("/reviews/new")
    public String showForm(
            @RequestParam("revieweeId") long revieweeId,
            @RequestParam(name = "tripId", required = false) Long tripId,
            Authentication authentication,
            Model model,
            RedirectAttributes ra
    ) {
        model.addAttribute("loggedIn", AuthUtils.isAuthenticated(authentication));
        final CurrentUser currentUser = currentUserProvider.requireCurrentUser();

        try {
            validateReviewAllowed(currentUser, revieweeId, tripId);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return (tripId != null) ? "redirect:/trips/" + tripId : "redirect:/profile";
        }

        model.addAttribute("createReviewForm", new CreateReviewForm(5, "", revieweeId, tripId));
        return "create-review";
    }

    @PostMapping("/reviews/new")
    public String submit(
            @Valid @ModelAttribute("createReviewForm") CreateReviewForm form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes ra
    ) {
        model.addAttribute("loggedIn", AuthUtils.isAuthenticated(authentication));
        final CurrentUser currentUser = currentUserProvider.requireCurrentUser();

        if (bindingResult.hasErrors()) {
            return "create-review";
        }

        try {
            validateReviewAllowed(currentUser, form.revieweeId(), form.tripId());

            final CreateReviewRequest req = new CreateReviewRequest(
                    currentUser.id(),
                    form.revieweeId(),
                    form.rating(),
                    form.comments()
            );

            reviewBusinessLogicService.createReview(req, true);

            ra.addFlashAttribute("message", "Review submitted.");
            return (form.tripId() != null)
                    ? "redirect:/trips/" + form.tripId()
                    : "redirect:/profile";

        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage() != null ? ex.getMessage() : "Could not submit review.");
            return "create-review";
        }
    }
}
