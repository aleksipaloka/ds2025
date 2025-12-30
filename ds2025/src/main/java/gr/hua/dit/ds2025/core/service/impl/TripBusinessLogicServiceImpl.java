package gr.hua.dit.ds2025.core.service.impl;

import gr.hua.dit.ds2025.core.model.Review;
import gr.hua.dit.ds2025.core.model.Trip;
import gr.hua.dit.ds2025.core.model.User;
import gr.hua.dit.ds2025.core.repositories.TripRepository;
import gr.hua.dit.ds2025.core.repositories.UserRepository;
import gr.hua.dit.ds2025.core.security.CurrentUser;
import gr.hua.dit.ds2025.core.security.CurrentUserProvider;
import gr.hua.dit.ds2025.core.service.TripBusinessLogicService;
import gr.hua.dit.ds2025.core.service.mapper.TripMapper;
import gr.hua.dit.ds2025.core.service.model.CreateTripRequest;
import gr.hua.dit.ds2025.core.service.model.ReviewView;
import gr.hua.dit.ds2025.core.service.model.TripView;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TripBusinessLogicServiceImpl implements TripBusinessLogicService {

        private static final Logger LOGGER = LoggerFactory.getLogger(gr.hua.dit.ds2025.core.service.impl.TripBusinessLogicServiceImpl.class);

        private final TripMapper tripMapper;
        private final TripRepository tripRepository;
        private final UserRepository userRepository;
        private final CurrentUserProvider currentUserProvider;

        public TripBusinessLogicServiceImpl(final TripMapper tripMapper,
                                              final TripRepository tripRepository,
                                              final UserRepository userRepository,

                                              final CurrentUserProvider currentUserProvider) {
            if (tripMapper == null) throw new NullPointerException();
            if (tripRepository == null) throw new NullPointerException();
            if (userRepository == null) throw new NullPointerException();
            if (currentUserProvider == null) throw new NullPointerException();

            this.tripMapper = tripMapper;
            this.tripRepository = tripRepository;
            this.userRepository = userRepository;
            this.currentUserProvider = currentUserProvider;
        }

        @Override
        public Optional<TripView> getTrip(final Long id) {
            if (id == null) throw new NullPointerException();
            if (id <= 0) throw new IllegalArgumentException();

            final Trip trip;
            try {
                trip = this.tripRepository.getReferenceById(id);
            } catch (EntityNotFoundException ignored) {
                return Optional.empty();
            }

            final TripView tripView = this.tripMapper.convertTripToTripView(trip);

            return Optional.of(tripView);
        }

        @Override
        public List<TripView> getTrips() {
            final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
            final List<Trip> tripList;

            tripList = this.tripRepository.findAll();
            return tripList.stream()
                    .map(this.tripMapper::convertTripToTripView)
                    .toList();
        }

        public List<TripView> getPublicAvailableTrips() {

            final List<Trip> tripList;

            tripList = this.tripRepository.findAll();
            return tripList.stream()
                    .map(this.tripMapper::convertTripToTripView)
                    .toList();
        }


        @Override
        public List<TripView> getTripsAsDriver() {
            final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
            final List<Trip> tripList;

            tripList = this.tripRepository.findAllByDriverId(currentUser.id());
            return tripList.stream()
                    .map(this.tripMapper::convertTripToTripView)
                    .toList();
        }

        @Override
        public List<TripView> getTripsAsPassenger() {
            final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
            final List<Trip> tripList;

            tripList = this.tripRepository.findAllByPassengersId(currentUser.id());
            return tripList.stream()
                    .map(this.tripMapper::convertTripToTripView)
                    .toList();
        }

        @Transactional
        @Override
        public TripView createTrip(@Valid final CreateTripRequest createTripRequest, final boolean notify) {
            if (createTripRequest == null) throw new NullPointerException();

            final long driverId = createTripRequest.driverId();
            final int availableSeats = createTripRequest.availableSeats();
            final String destination = createTripRequest.destination();
            final String startingPoint = createTripRequest.startingPoint();
            final LocalDateTime departureTime = createTripRequest.departureTime();

            final LocalDateTime now = LocalDateTime.now();
            if (departureTime == null || !departureTime.isAfter(now)) {
                throw new IllegalArgumentException("Departure time must be in the future.");
            }

            final User driver = this.userRepository.findById(driverId)
                    .orElseThrow(() -> new IllegalArgumentException("driver not found"));

            Trip trip = new Trip();
            trip.setDriver(driver);
            trip.setAvailableSeats(availableSeats);
            trip.setDestination(destination);
            trip.setDepartureTime(departureTime);
            trip.setStartingPoint(startingPoint);
            trip = this.tripRepository.save(trip);

            return this.tripMapper.convertTripToTripView(trip);
        }


    @Transactional
        @Override
        public void bookTrip(final long tripId) {
            final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();

            final Trip trip = tripRepository.findById(tripId)
                    .orElseThrow(() -> new IllegalArgumentException("Trip not found"));

            final Long currentUserId = currentUser.id();
            final Long driverId = trip.getDriver() != null ? trip.getDriver().getId() : null;

            if (driverId != null && driverId.equals(currentUserId)) {
                throw new IllegalStateException("Driver cannot book own trip");
            }

            if (trip.getAvailableSeats() <= 0) {
                throw new IllegalStateException("No available seats");
            }

            final boolean alreadyPassenger = trip.getPassengers() != null
                    && trip.getPassengers().stream().anyMatch(u -> currentUserId.equals(u.getId()));

            if (alreadyPassenger) {
                throw new IllegalStateException("Already booked");
            }

            final User passenger = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            trip.getPassengers().add(passenger);
            trip.setAvailableSeats(trip.getAvailableSeats() - 1);

            tripRepository.save(trip);
        }


        @Transactional
        @Override
        public void cancelTrip(final long tripId) {
            final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();

            final Trip trip = tripRepository.findById(tripId)
                    .orElseThrow(() -> new IllegalArgumentException("Trip not found"));

            final LocalDateTime now = LocalDateTime.now();
            final LocalDateTime latestAllowed = trip.getDepartureTime().minusMinutes(10);
            if (!now.isBefore(latestAllowed)) {
                throw new IllegalStateException("Trip can be cancelled only up to 10 minutes before departure.");
            }

            final Long currentUserId = currentUser.id();
            final Long driverId = (trip.getDriver() != null) ? trip.getDriver().getId() : null;

            if (driverId != null && driverId.equals(currentUserId)) {
                tripRepository.delete(trip);
                return;
            }

            System.out.println(trip.getPassengers().getClass());
            final boolean wasPassenger = trip.getPassengers() != null
                    && trip.getPassengers().removeIf(u -> currentUserId.equals(u.getId()));

            if (!wasPassenger) {
                throw new SecurityException("not authorized");
            }

            trip.setAvailableSeats(trip.getAvailableSeats() + 1);
            tripRepository.save(trip);
        }

        @Override
        public List<TripView> getAvailableTripsForHomepage(final LocalDateTime now) {
            if (now == null) throw new NullPointerException();

            return tripRepository.findAllByDepartureTimeAfterAndAvailableSeatsGreaterThan(now, 0).stream()
                    .map(tripMapper::convertTripToTripView)
                    .sorted(Comparator.comparing(TripView::departureTime))
                    .toList();
        }

        @Override
        public List<TripView> getUpcomingTripsForCurrentUser(final LocalDateTime now) {
            if (now == null) throw new NullPointerException();

            final CurrentUser currentUser = currentUserProvider.requireCurrentUser();

            final List<TripView> asDriver = tripRepository.findAllByDriverIdAndDepartureTimeAfter(currentUser.id(), now)
                    .stream().map(tripMapper::convertTripToTripView).toList();

            final List<TripView> asPassenger = tripRepository.findAllByPassengersIdAndDepartureTimeAfter(currentUser.id(), now)
                    .stream().map(tripMapper::convertTripToTripView).toList();

            final Map<Long, TripView> byId = new LinkedHashMap<>();
            for (TripView t : asDriver) byId.put(t.id(), t);
            for (TripView t : asPassenger) byId.put(t.id(), t);

            return byId.values().stream()
                    .sorted(Comparator.comparing(TripView::departureTime))
                    .toList();
        }

}
