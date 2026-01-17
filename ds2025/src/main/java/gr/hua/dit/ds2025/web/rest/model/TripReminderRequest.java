package gr.hua.dit.ds2025.web.rest.model;

import jakarta.validation.constraints.NotBlank;

public record TripReminderRequest(
        @NotBlank String message // optional custom message
) {}
