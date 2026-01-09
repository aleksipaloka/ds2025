package gr.hua.dit.ds2025.web.ui;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(
            final Authentication authentication,
            final HttpServletRequest request,
            final Model model
    ) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/";
        }

        if (request.getParameter("error") != null) {
            String msg = "Invalid username or password.";

            HttpSession session = request.getSession(false);
            if (session != null) {
                Object exObj = session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
                if (exObj instanceof DisabledException) {
                    msg = "Your account has been deactivated.";
                }
                session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            }

            model.addAttribute("error", msg);
        }
        if (request.getParameter("logout") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(final Authentication authentication) {
        if (AuthUtils.isAnonymous(authentication)) {
            return "redirect:/login";
        }
        return "logout";
    }
}