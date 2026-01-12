package gr.hua.dit.ds2025.web.ui.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.annotation.Order;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice(basePackages = "gr.hua.dit.ds2025.web.ui")
@Order(2)
public class GlobalErrorHandlerControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalErrorHandlerControllerAdvice.class);

    @ExceptionHandler(Exception.class)
    public String handleAnyError(final Exception exception,
                                 final HttpServletRequest httpServletRequest,
                                 final HttpServletResponse httpServletResponse,
                                 final Model model) {
        LOGGER.warn("Handling exception {} {}", exception.getClass(), exception.getMessage());
        model.addAttribute("message", exception.getMessage());
        model.addAttribute("path", httpServletRequest.getRequestURI());
        switch (exception) {
            case NoResourceFoundException noResourceFoundException -> {
                httpServletResponse.setStatus(404);
                return "error/404";
            }
            case SecurityException securityException -> httpServletResponse.setStatus(401);
            case ResponseStatusException responseStatusException -> {
                if (responseStatusException.getStatusCode().value() == 404) {
                    httpServletResponse.setStatus(404);
                    return "error/404";
                }
            }
            default -> {
            }
        }
        return "error/error";
    }
}
