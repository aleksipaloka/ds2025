package gr.hua.dit.ds2025.core.service.model;

import gr.hua.dit.ds2025.core.model.User;

import java.time.LocalDateTime;
import java.util.List;

public record TripView(
        long id,
        int availableSeats,
        String startingPoint,
        String destination,
        LocalDateTime departureTime,
        List<UserView> passengers,
        UserView driver
) {}
