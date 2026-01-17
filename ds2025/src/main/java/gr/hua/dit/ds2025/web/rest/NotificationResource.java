package gr.hua.dit.ds2025.web.rest;


import gr.hua.dit.ds2025.core.email.NotificationService;
import gr.hua.dit.ds2025.web.rest.model.TripReminderRequest;
import gr.hua.dit.ds2025.web.rest.model.TripReminderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationResource {

    private final NotificationService notificationService;

    public NotificationResource(final NotificationService notificationService) {
        if (notificationService == null) throw new NullPointerException();
        this.notificationService = notificationService;
    }

    @Operation(
            summary = "Send trip reminder email (driver + passengers)",
            description = "Sends emails only if now is within 10 minutes before departure.",
            security = @SecurityRequirement(name = "bearerAuth")
    )

    @PreAuthorize("hasRole('INTEGRATION_WRITE')")
    @PostMapping("/trips/{tripId}/reminder")
    public TripReminderResponse sendTripReminder(
            @PathVariable long tripId,
            @RequestBody(required = false) @Valid TripReminderRequest body
    ) {
        final String message = (body != null) ? body.message() : null;

        final var result = notificationService.sendTripReminderIfDue(tripId, message);

        return new TripReminderResponse(
                tripId,
                result.sent(),
                result.reason(),
                result.recipients()
        );
    }
}