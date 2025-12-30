package gr.hua.dit.ds2025.web.ui;

import gr.hua.dit.ds2025.core.security.CurrentUser;
import gr.hua.dit.ds2025.core.security.CurrentUserProvider;
import gr.hua.dit.ds2025.core.service.TripBusinessLogicService;
import gr.hua.dit.ds2025.core.service.model.TripView;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;

@Controller
public class TripDetailsController {

    private final TripBusinessLogicService tripBusinessLogicService;
    private final CurrentUserProvider currentUserProvider;

    public TripDetailsController(
            final TripBusinessLogicService tripBusinessLogicService,
            final CurrentUserProvider currentUserProvider
    ) {
        if (tripBusinessLogicService == null) throw new NullPointerException();
        if (currentUserProvider == null) throw new NullPointerException();
        this.tripBusinessLogicService = tripBusinessLogicService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/trips/{id}")
    public String tripDetails(
            @PathVariable("id") long id,
            Authentication authentication,
            Model model
    ) {
        final boolean loggedIn = AuthUtils.isAuthenticated(authentication);
        model.addAttribute("loggedIn", loggedIn);

        final String displayName = authentication != null ? authentication.getName() : "Guest";
        model.addAttribute("displayName", displayName);

        Long currentUserId = null;
        if (loggedIn) {
            try {
                final CurrentUser cu = currentUserProvider.requireCurrentUser();
                currentUserId = cu.id();
            } catch (Exception ignored) {
                currentUserId = null;
            }
        }
        model.addAttribute("currentUserId", currentUserId);

        final TripView trip = tripBusinessLogicService.getTrip(id).orElse(null);
        if (trip == null) return "redirect:/profile";

        model.addAttribute("trip", trip);

        // ✅ Reviews allowed only after departure time
        final LocalDateTime now = LocalDateTime.now();
        final boolean reviewEnabled = trip.departureTime() != null && now.isAfter(trip.departureTime());
        model.addAttribute("reviewEnabled", reviewEnabled);

        return "trip-details";
    }
}
