package gr.hua.dit.ds2025.web.ui;

import gr.hua.dit.ds2025.core.model.Role;
import gr.hua.dit.ds2025.core.service.UserBusinessLogicService;
import gr.hua.dit.ds2025.core.service.model.CreateUserRequest;
import gr.hua.dit.ds2025.core.service.model.CreateUserResult;
import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class RegistrationController {

    private final UserBusinessLogicService userBusinessLogicService;

    public RegistrationController(final UserBusinessLogicService userBusinessLogicService) {
        if (userBusinessLogicService == null) throw new NullPointerException();
        this.userBusinessLogicService = userBusinessLogicService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(
            final Authentication authentication,
            final Model model
    ) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/";
        }
        final CreateUserRequest createUserRequest = new CreateUserRequest( "", "", "", "", "");
        model.addAttribute("createUserRequest", createUserRequest);
        return "register";
    }

    @PostMapping("/register")
    public String handleFormSubmission(
            final Authentication authentication,
            @Valid @ModelAttribute("createUserRequest") final CreateUserRequest createUserRequest,
            final BindingResult bindingResult,
            final Model model
    ) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/";
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }
        final CreateUserResult createUserResult = this.userBusinessLogicService.createUser(createUserRequest);
        if (createUserResult.created()) {
            return "redirect:/login";
        }
        model.addAttribute("createUserRequest", createUserRequest);
        model.addAttribute("errormessage", createUserResult.reason());
        return "register";
    }
}
