package gr.hua.dit.ds2025.core.service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateTripRequest (
    @NotNull @Positive Long driverId,
    @NotNull @NotBlank int availableSeats,
    @NotNull @NotBlank @Size(max = 1000) String startingPoint,
    @NotNull @NotBlank @Size(max = 1000) String destination,
    @NotNull @NotBlank LocalDateTime departureTime
){}
