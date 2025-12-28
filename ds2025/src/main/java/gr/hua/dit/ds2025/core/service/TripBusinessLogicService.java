package gr.hua.dit.ds2025.core.service;

import gr.hua.dit.ds2025.core.service.model.CreateTripRequest;
import gr.hua.dit.ds2025.core.service.model.TripView;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface TripBusinessLogicService {

    Optional<TripView> getTrip(final Long id);

    List<TripView> getTrips();

    List<TripView> getTripsAsDriver();

    List<TripView> getTripsAsPassenger();

    TripView createTrip(final CreateTripRequest createTripRequest, final boolean notify);

    default TripView createTrip(final CreateTripRequest createTripRequest) {
        return this.createTrip(createTripRequest, true);
    }
}
