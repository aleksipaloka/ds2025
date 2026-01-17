package gr.hua.dit.ds2025.core.service.mapper;

import gr.hua.dit.ds2025.core.model.User;
import gr.hua.dit.ds2025.core.service.model.UserView;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {


    private final ReviewMapper reviewMapper;
    private final TripMapper tripMapper;

    public UserMapper(final ReviewMapper reviewMapper,
                      final TripMapper tripMapper) {

        if (reviewMapper == null) throw new NullPointerException("Review Mapper is null");
        if (tripMapper == null) throw new NullPointerException("Trip Mapper is null");

        this.reviewMapper = reviewMapper;
        this.tripMapper = tripMapper;
    }

    public UserView convertUserToUserView(final User user){
        if(user == null){
            return null;
        }

        final UserView userView = new UserView(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                this.tripMapper.convertTripToTripView(user.getTripsAsDriver()),
                this.tripMapper.convertTripToTripView(user.getTripsAsPassenger()),
                this.reviewMapper.convertReviewToReviewView(user.getReviewsWritten()),
                this.reviewMapper.convertReviewToReviewView(user.getReviewsConcerning())
        );
        return userView;

    }

}
