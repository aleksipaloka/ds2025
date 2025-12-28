package gr.hua.dit.ds2025.web.rest;


import gr.hua.dit.ds2025.core.service.TripDataService;
import gr.hua.dit.ds2025.core.service.model.TripView;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<TripView> tickets() {
        final List<TripView> tripViewList = this.tripDataService.getAllTrips();
        return tripViewList;
    }
}
