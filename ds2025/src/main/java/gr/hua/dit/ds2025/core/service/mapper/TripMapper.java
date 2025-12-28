package gr.hua.dit.ds2025.core.service.mapper;

import gr.hua.dit.ds2025.core.model.Trip;
import gr.hua.dit.ds2025.core.model.User;
import gr.hua.dit.ds2025.core.service.model.TripView;
import gr.hua.dit.ds2025.core.service.model.UserSummaryView;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TripMapper {

    private final UserSummaryMapper userSummaryMapper;

    public TripMapper(UserSummaryMapper userSummaryMapper) {
        if (userSummaryMapper == null) throw new NullPointerException("UserSummaryMapper is null");
        this.userSummaryMapper = userSummaryMapper;
    }

    public TripView convertTripToTripView(final Trip trip) {
        if (trip == null) return null;

        return new TripView(
                trip.getId(),
                trip.getAvailableSeats(),
                trip.getStartingPoint(),
                trip.getDestination(),
                trip.getDepartureTime(),
                convertUsersToSummary(trip.getPassengers()),
                userSummaryMapper.convertUserToUserSummaryView(trip.getDriver())

        );
    }

    public List<TripView> convertTripToTripView(final List<Trip> trips) {
        if (trips == null) return Collections.emptyList();

        return trips.stream()
                .map(this::convertTripToTripView)
                .collect(Collectors.toList());
    }

    private List<UserSummaryView> convertUsersToSummary(List<User> users) {
        if (users == null) return Collections.emptyList();
        return users.stream()
                .map(userSummaryMapper::convertUserToUserSummaryView)
                .collect(Collectors.toList());
    }
}
