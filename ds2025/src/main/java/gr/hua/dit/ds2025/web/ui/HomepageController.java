package gr.hua.dit.ds2025.web.ui;

import java.util.List;

import gr.hua.dit.ds2025.core.service.impl.TripBusinessLogicServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {

    private final TripBusinessLogicServiceImpl tripBusinessLogicService;

    public HomepageController(final TripBusinessLogicServiceImpl tripBusinessLogicService) {
        if (tripBusinessLogicService == null) throw new NullPointerException();
        this.tripBusinessLogicService = tripBusinessLogicService;
    }

    @GetMapping("/test")
    public String test() {
        return "homepage";
    }

    @GetMapping("/")
    public String showHomepage(Authentication authentication, Model model) {

        boolean loggedIn = AuthUtils.isAuthenticated(authentication);
        model.addAttribute("loggedIn", loggedIn);

        // Πρέπει να είναι PUBLIC method (να μη ζητάει authentication)
        model.addAttribute("availableTrips", tripBusinessLogicService.getPublicAvailableTrips());

        // Upcoming μόνο αν logged in
        if (loggedIn) {
            model.addAttribute("upcomingTrips", tripBusinessLogicService.getPublicAvailableTrips());
        } else {
            model.addAttribute("upcomingTrips", java.util.List.of());
        }

        return "homepage";
    }
}
