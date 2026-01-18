package gr.hua.dit.ds2025.config;

import gr.hua.dit.ds2025.core.email.NotificationService;
import gr.hua.dit.ds2025.core.repositories.TripRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class TripReminderScheduler {

    private final NotificationService notificationService;
    private final TripRepository tripRepository;

    public TripReminderScheduler(NotificationService notificationService, TripRepository tripRepository) {
        this.notificationService = notificationService;
        this.tripRepository = tripRepository;
    }

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void sendDueReminders() {

        LocalDateTime base = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        LocalDateTime from = base.plusMinutes(9);
        LocalDateTime to = base.plusMinutes(10);

        List<Long> tripIds = tripRepository.findTripIdsDepartingBetween(from, to);

        System.out.println("[SCHEDULER] checking trips from " + from + " to " + to);
        System.out.println("[SCHEDULER] found tripIds=" + tripIds);


        for (Long tripId : tripIds) {
            try {
                notificationService.sendTripReminderIfDue(tripId, null);
            } catch (Exception e) {
                System.err.println("[REMINDER][FAILED] tripId=" + tripId + " reason=" + e.getMessage());
            }
        }
    }
}

