package gr.hua.dit.ds2025.core.service;

import gr.hua.dit.ds2025.core.service.model.CreateReviewRequest;
import gr.hua.dit.ds2025.core.service.model.CreateTripRequest;
import gr.hua.dit.ds2025.core.service.model.TripView;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TripDataService {

    List<TripView> getAllTrips();

    TripView getTrip(long id);

    List<TripView> getTripsAsDriver(long id);

    List<TripView> getTripsAsPassenger(long id);

    List<TripView> getAvailableTrips();

    List<TripView> getUpcomingTripsForUser(long id);

    TripView createTrip(CreateTripRequest req);
}