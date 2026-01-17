package gr.hua.dit.ds2025.web.rest.model;

import java.util.List;

public record TripReminderResponse(
        long tripId,
        boolean sent,
        String reason,
        List<String> recipients
) {}
