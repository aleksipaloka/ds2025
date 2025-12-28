package gr.hua.dit.ds2025.core.service;

import gr.hua.dit.ds2025.core.service.model.TripView;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TripDataService {

    List<TripView> getAllTrips();
}
