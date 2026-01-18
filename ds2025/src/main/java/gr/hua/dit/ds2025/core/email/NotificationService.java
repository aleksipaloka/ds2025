package gr.hua.dit.ds2025.core.email;

public interface NotificationService {

    TripReminderResult sendTripReminderIfDue(final long tripId, final String customMessage);
}
