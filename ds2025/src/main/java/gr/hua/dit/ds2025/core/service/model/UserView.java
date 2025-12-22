package gr.hua.dit.ds2025.core.service.model;

import gr.hua.dit.ds2025.core.model.Review;
import gr.hua.dit.ds2025.core.model.Role;

import java.util.List;

public record UserView(

        long id,
        String name,
        String lastName,
        String username,
        String email,
        Role role,
        //List<TripView> tripsAsDriver,
        //List<TripView> tripsAsPassenger,
        List<ReviewView> reviewsWritten,
        List<ReviewView> reviewsConcerning

) {

    public String fullName() {
        return this.name + " " + this.lastName;
    }

}
