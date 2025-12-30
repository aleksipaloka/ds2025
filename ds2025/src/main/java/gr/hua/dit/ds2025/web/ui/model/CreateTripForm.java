package gr.hua.dit.ds2025.web.ui.model;

import jakarta.validation.constraints.AssertTrue;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateTripForm(
        @NotBlank String startingPoint,
        @NotBlank String destination,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime,
        @Min(1) int availableSeats
) {
        @AssertTrue(message = "Departure time must be in the future.")
        public boolean isDepartureTimeInFuture() {
            return departureTime != null && departureTime.isAfter(LocalDateTime.now());
        }
}
