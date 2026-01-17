package gr.hua.dit.ds2025.web.ui;

import gr.hua.dit.ds2025.core.security.CurrentUser;
import gr.hua.dit.ds2025.core.security.CurrentUserProvider;
import gr.hua.dit.ds2025.core.service.TripBusinessLogicService;
import gr.hua.dit.ds2025.core.service.model.TripView;
import gr.hua.dit.ds2025.core.weather.WeatherService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;

//TODO MERGE TRIP CONTROLLER & TRIP-DETAILS CONTROLLER?
@Controller
public class TripDetailsController {

    private final TripBusinessLogicService tripBusinessLogicService;
    private final CurrentUserProvider currentUserProvider;
    private final WeatherService weatherService;

    public TripDetailsController(
            final TripBusinessLogicService tripBusinessLogicService,
            final CurrentUserProvider currentUserProvider,
            final WeatherService weatherService
    ) {
        if (tripBusinessLogicService == null) throw new NullPointerException();
        if (currentUserProvider == null) throw new NullPointerException();
        if (weatherService == null) throw new NullPointerException();
        this.tripBusinessLogicService = tripBusinessLogicService;
        this.currentUserProvider = currentUserProvider;
        this.weatherService = weatherService;
    }

    @GetMapping("/trips/{id}")
    public String tripDetails(
            @PathVariable("id") long id,
            Authentication authentication,
            HttpServletRequest request,
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
        if (trip == null) {
            return "redirect:/profile";
        }
        model.addAttribute("trip", trip);

        final LocalDateTime now = LocalDateTime.now();

        final String backUrl =
                (trip.departureTime() != null && trip.departureTime().isBefore(now))
                        ? "/profile"
                        : "/";
        model.addAttribute("backUrl", backUrl);

        try {
            final String place = trip.startingPoint();
            if (place != null && !place.isBlank()) {
                model.addAttribute("weather", weatherService.getCurrentWeatherByPlace(place));
            }
        } catch (Exception e) {
            model.addAttribute("weatherError", "Could not load weather info.");
        }

        final boolean reviewEnabled =
                trip.departureTime() != null && now.isAfter(trip.departureTime());
        model.addAttribute("reviewEnabled", reviewEnabled);

        return "trip-details";
    }
}
