package gr.hua.dit.ds2025.core.email;

import java.util.List;

public record TripReminderResult(
        boolean sent,
        String reason,
        List<String> recipients
) {}