package gr.hua.dit.ds2025.web.ui;

import gr.hua.dit.ds2025.core.repositories.UserRepository;
import gr.hua.dit.ds2025.core.service.ReviewBusinessLogicService;
import gr.hua.dit.ds2025.core.service.TripBusinessLogicService;
import gr.hua.dit.ds2025.core.service.model.ReviewView;
import gr.hua.dit.ds2025.core.service.model.TripView;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ReviewBusinessLogicService reviewBusinessLogicService;
    private final TripBusinessLogicService tripBusinessLogicService;
    private final UserRepository userRepository;

    public AdminController(
            final ReviewBusinessLogicService reviewBusinessLogicService,
            final TripBusinessLogicService tripBusinessLogicService,
            final UserRepository userRepository
    ) {
        if (reviewBusinessLogicService == null) throw new NullPointerException();
        if (tripBusinessLogicService == null) throw new NullPointerException();
        if (userRepository == null) throw new NullPointerException();
        this.reviewBusinessLogicService = reviewBusinessLogicService;
        this.tripBusinessLogicService = tripBusinessLogicService;
        this.userRepository = userRepository;
    }

    // ίδια λογική με ProfileController
    public record PastTripItem(TripView trip, String roleLabel) {}

    @GetMapping("/reviews")
    public String adminReviews(Authentication authentication, Model model) {
        model.addAttribute("loggedIn", AuthUtils.isAuthenticated(authentication));
        model.addAttribute("displayName", authentication != null ? authentication.getName() : "Admin");

        final List<ReviewView> reviews = reviewBusinessLogicService.getReviews();
        model.addAttribute("reviews", reviews);

        return "admin-reviews";
    }

    /**
     * ✅ Admin view ενός χρήστη με το ΙΔΙΟ profile.html
     * URL: /admin/users/{id}
     */
    @GetMapping("/users/{id}")
    public String viewUserProfileAsAdmin(
            @PathVariable("id") long id,
            Authentication authentication,
            Model model,
            RedirectAttributes ra,
            @RequestParam(name = "reviews", defaultValue = "to") final String reviewsFilter
    ) {
        model.addAttribute("loggedIn", AuthUtils.isAuthenticated(authentication));

        final var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            ra.addFlashAttribute("error", "User not found.");
            return "redirect:/admin/reviews";
        }

        // displayName στο profile
        final String displayName = (user.getName() != null ? user.getName() : "")
                + " "
                + (user.getLastName() != null ? user.getLastName() : "");
        model.addAttribute("displayName", displayName.isBlank() ? user.getUsername() : displayName.trim());

        // Past trips: επειδή τα υπάρχοντα service methods είναι “current user only”,
        // εδώ παίρνουμε όλα και φιλτράρουμε με βάση το id.
        final List<PastTripItem> pastTrips = new ArrayList<>();

        // ως driver
        for (TripView t : tripBusinessLogicService.getTrips()) {
            if (t.driver() != null && t.driver().id() == id) {
                pastTrips.add(new PastTripItem(t, "Driver"));
            }
        }

        // ως passenger
        for (TripView t : tripBusinessLogicService.getTrips()) {
            if (t.passengers() != null && t.passengers().stream().anyMatch(p -> p.id() == id)) {
                pastTrips.add(new PastTripItem(t, "Passenger"));
            }
        }

        pastTrips.sort(Comparator.comparing(
                (PastTripItem pt) -> pt.trip().departureTime(),
                Comparator.nullsLast(Comparator.naturalOrder())
        ).reversed());

        model.addAttribute("pastTrips", pastTrips);

        // Reviews (όλα) φιλτραρισμένα για τον user
        final List<ReviewView> reviewsToUser = reviewBusinessLogicService.getReviews().stream()
                .filter(r -> r.reviewee() != null && r.reviewee().id() == id)
                .toList();

        final List<ReviewView> reviewsFromUser = reviewBusinessLogicService.getReviews().stream()
                .filter(r -> r.reviewer() != null && r.reviewer().id() == id)
                .toList();

        // Για το existing UI filter (to/from)
        model.addAttribute("reviewsToUser", reviewsToUser);
        model.addAttribute("reviewsFromUser", reviewsFromUser);

        // ✅ Εδώ είναι η διόρθωση: διάλεξε based on reviewsFilter
        final boolean showTo = !"from".equalsIgnoreCase(reviewsFilter);
        model.addAttribute("reviews", showTo ? reviewsToUser : reviewsFromUser);
        model.addAttribute("reviewsFilter", showTo ? "to" : "from");

        // Για να εμφανίζεις Admin badge/κουμπιά μέσα στο profile αν θες
        model.addAttribute("isAdmin", true);
        model.addAttribute("adminViewingUserId", id);

        return "profile";
    }

    @PostMapping("/users/{id}/delete")
    public String deactivateUser(@PathVariable("id") long id, RedirectAttributes ra) {
        try {
            final var user = userRepository.findById(id).orElse(null);
            if (user == null) {
                ra.addFlashAttribute("error", "User not found.");
                return "redirect:/admin/reviews";
            }

            user.setEnabled(false);
            userRepository.save(user);

            ra.addFlashAttribute("message", "User deactivated (soft delete).");
            return "redirect:/admin/reviews";
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage() != null ? ex.getMessage() : "Could not deactivate user.");
            return "redirect:/admin/reviews";
        }
    }
}
