package gr.hua.dit.ds2025.web.ui;

import gr.hua.dit.ds2025.core.model.User;
import gr.hua.dit.ds2025.core.repositories.UserRepository;
import gr.hua.dit.ds2025.core.service.ReviewBusinessLogicService;
import gr.hua.dit.ds2025.core.service.model.ReviewView;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ReviewBusinessLogicService reviewBusinessLogicService;
    private final UserRepository userRepository;

    public AdminController(
            final ReviewBusinessLogicService reviewBusinessLogicService,
            final UserRepository userRepository
    ) {
        if (reviewBusinessLogicService == null) throw new NullPointerException();
        if (userRepository == null) throw new NullPointerException();
        this.reviewBusinessLogicService = reviewBusinessLogicService;
        this.userRepository = userRepository;
    }

    // ✅ Admin: list all reviews
    @GetMapping("/reviews")
    public String reviews(Authentication authentication, Model model) {
        model.addAttribute("loggedIn", AuthUtils.isAuthenticated(authentication));
        model.addAttribute("displayName", authentication != null ? authentication.getName() : "Admin");

        final List<ReviewView> reviews = reviewBusinessLogicService.getReviews();
        model.addAttribute("reviews", reviews);

        return "admin-reviews";
    }

    // ✅ Admin: view a user's profile (basic)
    @GetMapping("/users/{id}")
    public String userProfile(
            @PathVariable("id") long id,
            Authentication authentication,
            Model model
    ) {
        model.addAttribute("loggedIn", AuthUtils.isAuthenticated(authentication));
        model.addAttribute("displayName", authentication != null ? authentication.getName() : "Admin");

        final User user = userRepository.findById(id).orElse(null);
        if (user == null) return "redirect:/admin/reviews";

        model.addAttribute("user", user);
        return "admin-user";
    }

    // ✅ Admin: delete/deactivate user (hard delete)
    @PostMapping("/users/{id}/delete")
    public String deleteUser(
            @PathVariable("id") long id,
            RedirectAttributes ra
    ) {
        try {
            if (!userRepository.existsById(id)) {
                ra.addFlashAttribute("error", "User not found.");
                return "redirect:/admin/reviews";
            }

            userRepository.deleteById(id);
            ra.addFlashAttribute("message", "User deleted.");
            return "redirect:/admin/reviews";

        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage() != null ? ex.getMessage() : "Could not delete user.");
            return "redirect:/admin/reviews";
        }
    }
}
