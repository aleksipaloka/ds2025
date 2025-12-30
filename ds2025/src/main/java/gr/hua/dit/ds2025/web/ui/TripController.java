package gr.hua.dit.ds2025.web.ui;

import gr.hua.dit.ds2025.core.security.CurrentUser;
import gr.hua.dit.ds2025.core.security.CurrentUserProvider;
import gr.hua.dit.ds2025.core.service.TripBusinessLogicService;
import gr.hua.dit.ds2025.core.service.model.CreateTripRequest;
import gr.hua.dit.ds2025.web.ui.model.CreateTripForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class TripController {

    private final TripBusinessLogicService tripBusinessLogicService;
    private final CurrentUserProvider currentUserProvider;

    public TripController(
            final TripBusinessLogicService tripBusinessLogicService,
            final CurrentUserProvider currentUserProvider
    ) {
        if (tripBusinessLogicService == null) throw new NullPointerException();
        if (currentUserProvider == null) throw new NullPointerException();
        this.tripBusinessLogicService = tripBusinessLogicService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/trips/new")
    public String showForm(Model model) {
        if (!model.containsAttribute("createTripForm")) {
            model.addAttribute("createTripForm",
                    new CreateTripForm("", "", LocalDateTime.now().plusHours(1), 1)
            );
        }
        return "trip";
    }

    @PostMapping("/trips/new")
    public String submit(
            @Valid @ModelAttribute("createTripForm") CreateTripForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra
    ) {
        if (bindingResult.hasErrors()) {
            return "trip";
        }

        try {
            final CurrentUser currentUser = currentUserProvider.requireCurrentUser();

            final CreateTripRequest req = new CreateTripRequest(
                    currentUser.id(),
                    form.availableSeats(),
                    form.destination(),
                    form.startingPoint(),
                    form.departureTime()
            );

            tripBusinessLogicService.createTrip(req, true);
            ra.addFlashAttribute("message", "Trip created successfully.");
            return "redirect:/";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage() != null ? ex.getMessage() : "Could not create trip.");
            return "trip";
        }
    }

    @PostMapping("/trips/{id}/book")
    public String bookTrip(@PathVariable long id, RedirectAttributes ra) {
        try {
            tripBusinessLogicService.bookTrip(id);
            ra.addFlashAttribute("message", "Trip booked successfully.");
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not book trip.");
        }
        return "redirect:/";
    }

    @PostMapping("/trips/{id}/cancel")
    public String cancelTrip(@PathVariable long id, RedirectAttributes ra) {
        try {
            tripBusinessLogicService.cancelTrip(id);
            ra.addFlashAttribute("message", "Trip cancelled.");
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        } catch (SecurityException ex) {
            ra.addFlashAttribute("error", "Not allowed.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Could not cancel trip.");
        }
        return "redirect:/";
    }
}
