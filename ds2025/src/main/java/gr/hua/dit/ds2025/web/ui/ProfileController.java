package gr.hua.dit.ds2025.web.ui;

import gr.hua.dit.ds2025.core.service.ReviewBusinessLogicService;
import gr.hua.dit.ds2025.core.service.TripBusinessLogicService;
import gr.hua.dit.ds2025.core.service.model.ReviewView;
import gr.hua.dit.ds2025.core.service.model.TripView;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class ProfileController {

    private final TripBusinessLogicService tripBusinessLogicService;
    private final ReviewBusinessLogicService reviewBusinessLogicService;

    public ProfileController(
            final TripBusinessLogicService tripBusinessLogicService,
            final ReviewBusinessLogicService reviewBusinessLogicService
    ) {
        if (tripBusinessLogicService == null) throw new NullPointerException();
        if (reviewBusinessLogicService == null) throw new NullPointerException();
        this.tripBusinessLogicService = tripBusinessLogicService;
        this.reviewBusinessLogicService = reviewBusinessLogicService;
    }

    public record PastTripItem(TripView trip, String roleLabel) {}

    @GetMapping("/profile")
    public String profile(
            final Authentication authentication,
            final Model model,
            @RequestParam(name = "reviews", defaultValue = "to") final String reviewsFilter
    ) {

        final String displayName = authentication != null ? authentication.getName() : "Profile";
        model.addAttribute("displayName", displayName);

        final List<PastTripItem> pastTrips = new ArrayList<>();

        final List<TripView> asDriver = tripBusinessLogicService.getTripsAsDriver();
        for (TripView t : asDriver) pastTrips.add(new PastTripItem(t, "Driver"));

        final List<TripView> asPassenger = tripBusinessLogicService.getTripsAsPassenger();
        for (TripView t : asPassenger) pastTrips.add(new PastTripItem(t, "Passenger"));

        // Sort by departureTime desc (αν είναι null, πάει κάτω)
        pastTrips.sort(Comparator.comparing(
                (PastTripItem pt) -> pt.trip().departureTime(),
                Comparator.nullsLast(Comparator.naturalOrder())
        ).reversed());

        model.addAttribute("pastTrips", pastTrips);


        final boolean showToMe = !"from".equalsIgnoreCase(reviewsFilter);
        final List<ReviewView> reviews = showToMe
                ? reviewBusinessLogicService.getReviewsToUser()
                : reviewBusinessLogicService.getReviewsFromUser();

        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewsFilter", showToMe ? "to" : "from");

        return "profile";
    }
}
