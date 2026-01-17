package gr.hua.dit.ds2025.core.email;

import gr.hua.dit.ds2025.core.repositories.UserRepository;
import gr.hua.dit.ds2025.core.service.TripBusinessLogicService;
import gr.hua.dit.ds2025.core.service.UserBusinessLogicService;
import gr.hua.dit.ds2025.core.service.model.TripView;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class NotificationServiceImpl implements NotificationService{

    private final TripBusinessLogicService tripService;
    private final UserBusinessLogicService userService;
    private final EmailProviderClient emailClient;
    private final UserRepository userRepository;

    public NotificationServiceImpl(final TripBusinessLogicService tripService,
                               final UserBusinessLogicService userService,
                               final EmailProviderClient emailClient, UserRepository userRepository) {
        this.userRepository = userRepository;
        if (tripService == null) throw new NullPointerException();
        if (userService == null) throw new NullPointerException();
        if (emailClient == null) throw new NullPointerException();
        this.tripService = tripService;
        this.userService = userService;
        this.emailClient = emailClient;
    }

    public TripReminderResult sendTripReminderIfDue(final long tripId, final String customMessage) {
        final TripView trip = tripService.getTrip(tripId)
                .orElse(null);

        if (trip == null) {
            return new TripReminderResult(false, "Trip not found", List.of());
        }
        if (trip.departureTime() == null) {
            return new TripReminderResult(false, "Trip has no departureTime", List.of());
        }

        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime from = trip.departureTime().minusMinutes(10);
        final LocalDateTime to = trip.departureTime();

        if (now.isBefore(from)) {
            return new TripReminderResult(false, "Too early (not within 10 minutes window)", List.of());
        }
        if (!now.isBefore(to)) {
            return new TripReminderResult(false, "Trip already started/past", List.of());
        }

        // Collect user IDs (driver + passengers)
        final Set<Long> userIds = new LinkedHashSet<>();
        if (trip.driver() != null && trip.driver().id() != null) userIds.add(trip.driver().id());
        if (trip.passengers() != null) {
            for (var p : trip.passengers()) {
                if (p != null && p.id() != null) userIds.add(p.id());
            }
        }

        // Convert IDs -> emails via UserBusinessLogicService (service only)
        final Set<String> emails = new LinkedHashSet<>();
        for (Long uid : userIds) {
            userRepository.findById(uid).ifPresent(u -> {
                if (u.getEmail() != null && !u.getEmail().isBlank()) {
                    emails.add(u.getEmail().trim().toLowerCase());
                }
            });
        }

        if (emails.isEmpty()) {
            return new TripReminderResult(false, "No recipient emails found", List.of());
        }

        final String subject = "GreenRide: Trip starts in 10 minutes";
        final String html = buildHtml(trip, customMessage);

        for (String email : emails) {
            emailClient.sendEmail(email, subject, html);
        }

        return new TripReminderResult(true, "Sent", emails.stream().toList());
    }
    private String buildHtml(final TripView trip, final String customMessage) {
        String msg = (customMessage == null || customMessage.isBlank())
                ? "Reminder: Your trip starts in ~10 minutes."
                : customMessage;

        return """
                <div style="font-family: Arial, sans-serif;">
                  <h2 style="color:#3c7f4b;">GreenRide</h2>
                  <p><strong>%s</strong></p>
                  <p><strong>Route:</strong> %s - %s</p>
                  <p><strong>Departure:</strong> %s</p>
                </div>
                """.formatted(
                escape(msg),
                escape(safe(trip.startingPoint())),
                escape(safe(trip.destination())),
                trip.departureTime()
        );
    }

    private static String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    // very small HTML escape for demo
    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}