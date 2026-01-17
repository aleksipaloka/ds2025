package gr.hua.dit.ds2025.web.rest;


import gr.hua.dit.ds2025.core.service.TripDataService;
import gr.hua.dit.ds2025.core.service.model.CreateReviewRequest;
import gr.hua.dit.ds2025.core.service.model.CreateTripRequest;
import gr.hua.dit.ds2025.core.service.model.ReviewView;
import gr.hua.dit.ds2025.core.service.model.TripView;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/trip", produces = MediaType.APPLICATION_JSON_VALUE)
public class TripResource {

    private final TripDataService tripDataService;

    public TripResource(final TripDataService tripDataService) {
        if (tripDataService == null) throw new NullPointerException();
        this.tripDataService = tripDataService;
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("")
    public List<TripView> getTrips() {
        return this.tripDataService.getAllTrips();
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("/{id}")
    public TripView getTrip(@PathVariable("id") long id) {
        return this.tripDataService.getTrip(id);
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("/user/driver/{id}")
    public List<TripView> getTripsAsDriver(@PathVariable("id") long id) {
        return this.tripDataService.getTripsAsDriver(id);
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("/user/passenger/{id}")
    public List<TripView> getTripsAsPassenger(@PathVariable("id") long id) {
        return this.tripDataService.getTripsAsPassenger(id);
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("/available")
    public List<TripView> getAvailableTrips() {
        return this.tripDataService.getAvailableTrips();
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("/user/upcoming/{id}")
    public List<TripView> getUpcomingTripsForUser(@PathVariable("id") long id) {
        return this.tripDataService.getUpcomingTripsForUser(id);
    }

    @PreAuthorize("hasAuthority('INTEGRATION_WRITE')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public TripView create(@RequestBody @Valid CreateTripRequest req) {
        return tripDataService.createTrip(req);
    }
}
