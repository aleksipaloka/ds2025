package gr.hua.dit.ds2025.core.service.impl;

import gr.hua.dit.ds2025.core.model.Trip;
import gr.hua.dit.ds2025.core.repositories.TripRepository;
import gr.hua.dit.ds2025.core.service.TripDataService;
import gr.hua.dit.ds2025.core.service.mapper.TripMapper;
import gr.hua.dit.ds2025.core.service.model.TripView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripDataServiceImpl implements TripDataService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    public TripDataServiceImpl(final TripRepository tripRepository,
                                 final TripMapper tripMapper) {
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
}
