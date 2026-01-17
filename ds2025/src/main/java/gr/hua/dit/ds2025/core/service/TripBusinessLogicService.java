package gr.hua.dit.ds2025.core.service;

import gr.hua.dit.ds2025.core.service.model.CreateTripRequest;
import gr.hua.dit.ds2025.core.service.model.TripView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TripBusinessLogicService {

    Optional<TripView> getTrip(final Long id);

    List<TripView> getTrips();

    List<TripView> getTripsAsDriver();

    List<TripView> getTripsAsPassenger();

    void createTrip(final CreateTripRequest createTripRequest, final boolean notify);

    void cancelTrip(final long tripId);

    void bookTrip(final long tripId);

    List<TripView> getAvailableTripsForHomepage(LocalDateTime now);

    List<TripView> getUpcomingTripsForCurrentUser(LocalDateTime now);

}
