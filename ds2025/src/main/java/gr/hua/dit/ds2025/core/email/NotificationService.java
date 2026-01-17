package gr.hua.dit.ds2025.core.email;

import gr.hua.dit.ds2025.core.service.model.TripView;

public interface NotificationService {

    TripReminderResult sendTripReminderIfDue(final long tripId, final String customMessage);
}
