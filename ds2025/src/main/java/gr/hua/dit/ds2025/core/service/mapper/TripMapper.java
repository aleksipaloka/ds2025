package gr.hua.dit.ds2025.core.service.mapper;

import gr.hua.dit.ds2025.core.model.Trip;
import gr.hua.dit.ds2025.core.service.model.TripView;
import gr.hua.dit.ds2025.core.service.model.UserView;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TripMapper {
    private final UserMapper userMapper;

    public TripMapper(final UserMapper personMapper) {
        if (personMapper == null) throw new NullPointerException();
        this.userMapper = personMapper;
    }

    public TripView convertTripToTripView(final Trip trip) {
        if (trip == null) {
            return null;
        }
        return new TripView(
                trip.getId(),
                trip.getAvailableSeats(),
                trip.getStartingPoint(),
                trip.getDestination(),
                trip.getDepartureTime(),
                this.userMapper.convertUserToUserView(trip.getPassengers()),
                this.userMapper.convertUserToUserView(trip.getDriver())
        );
    }

    public List<TripView> convertTripToTripView(final List<Trip> trips) {
        if (trips == null) {
            return Collections.emptyList();
        }

        return trips.stream()
                .map(this::convertTripToTripView) // καλεί τη 1-1
                .collect(Collectors.toList());
    }
}
