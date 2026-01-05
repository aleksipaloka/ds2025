package gr.hua.dit.ds2025.web.ui;

import gr.hua.dit.ds2025.core.service.TripBusinessLogicService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class HomepageController {

    private final TripBusinessLogicService tripBusinessLogicService;

    public HomepageController(final TripBusinessLogicService tripBusinessLogicService) {
        if (tripBusinessLogicService == null) throw new NullPointerException();
        this.tripBusinessLogicService = tripBusinessLogicService;
    }

    @GetMapping("/")
    public String showHomepage(Authentication authentication, Model model) {

        final boolean loggedIn = AuthUtils.isAuthenticated(authentication);
        model.addAttribute("loggedIn", loggedIn);

        final boolean isAdmin = authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        model.addAttribute("isAdmin", isAdmin);

        final LocalDateTime now = LocalDateTime.now();

        model.addAttribute("availableTrips", tripBusinessLogicService.getAvailableTripsForHomepage(now));

        if (loggedIn) {
            model.addAttribute("upcomingTrips", tripBusinessLogicService.getUpcomingTripsForCurrentUser(now));
        } else {
            model.addAttribute("upcomingTrips", List.of());
        }

        return "homepage";
    }
}
