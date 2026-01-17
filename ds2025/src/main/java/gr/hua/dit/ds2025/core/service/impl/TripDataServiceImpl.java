package gr.hua.dit.ds2025.core.service.impl;

import gr.hua.dit.ds2025.core.model.Trip;
import gr.hua.dit.ds2025.core.repositories.TripRepository;
import gr.hua.dit.ds2025.core.repositories.UserRepository;
import gr.hua.dit.ds2025.core.service.TripDataService;
import gr.hua.dit.ds2025.core.service.mapper.TripMapper;
import gr.hua.dit.ds2025.core.service.model.CreateReviewRequest;
import gr.hua.dit.ds2025.core.service.model.CreateTripRequest;
import gr.hua.dit.ds2025.core.service.model.TripView;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TripDataServiceImpl implements TripDataService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final UserRepository userRepository;

    public TripDataServiceImpl(final TripRepository tripRepository,
                               final TripMapper tripMapper, UserRepository userRepository) {
        this.userRepository = userRepository;
        if (tripRepository == null) throw new NullPointerException();
        if (tripMapper == null) throw new NullPointerException();
        this.tripRepository = tripRepository;
        this.tripMapper = tripMapper;
    }

    @Override
    public List<TripView> getAllTrips() {
        final List<Trip> tripList = this.tripRepository.findAll();
        final List<TripView> tripViewList = tripList
                .stream()
                .map(this.tripMapper::convertTripToTripView)
                .toList();
        return tripViewList;
    }

    @Override
    public TripView getTrip(long id) {
        final Trip trip = this.tripRepository.getReferenceById(id);
        return this.tripMapper.convertTripToTripView(trip);
    }

    @Override
    public List<TripView> getTripsAsDriver(long id) {
        final List<Trip> tripList = this.tripRepository.findAllByDriverId(id);
        return tripList
                .stream()
                .map(this.tripMapper::convertTripToTripView)
                .toList();
    }

    @Override
    public List<TripView> getTripsAsPassenger(long id) {
        final List<Trip> tripList = this.tripRepository.findAllByPassengersId(id);
        return tripList
                .stream()
                .map(this.tripMapper::convertTripToTripView)
                .toList();
    }

    @Override
    public List<TripView> getAvailableTrips() {
        final List<Trip> tripList = this.tripRepository.findAllByDepartureTimeAfterAndAvailableSeatsGreaterThan(LocalDateTime.now(), 0);
        return tripList
                .stream()
                .map(this.tripMapper::convertTripToTripView)
                .toList();
    }

    @Override
    public List<TripView> getUpcomingTripsForUser(long id) {
        final List<TripView> asDriver = tripRepository.findAllByDriverIdAndDepartureTimeAfter(id, LocalDateTime.now())
                .stream().map(tripMapper::convertTripToTripView).toList();

        final List<TripView> asPassenger = tripRepository.findAllByPassengersIdAndDepartureTimeAfter(id, LocalDateTime.now())
                .stream().map(tripMapper::convertTripToTripView).toList();

        final Map<Long, TripView> byId = new LinkedHashMap<>();
        for (TripView t : asDriver) byId.put(t.id(), t);
        for (TripView t : asPassenger) byId.put(t.id(), t);

        return byId.values().stream()
                .sorted(Comparator.comparing(TripView::departureTime))
                .toList();
    }

    @Override
    public TripView createTrip(CreateTripRequest req) {
        Trip trip = new Trip();
        final int availableSeats = req.availableSeats();

        trip.setDriver(userRepository.findById(req.driverId())
                .orElseThrow(() -> new IllegalArgumentException("reviewer not found")));
        trip.setAvailableSeats(availableSeats);
        trip.setDestination(req.destination());
        trip.setDepartureTime(req.departureTime());
        trip.setStartingPoint(req.startingPoint());
        trip = this.tripRepository.save(trip);

        return this.tripMapper.convertTripToTripView(trip);
    }
}