package gr.hua.dit.ds2025.core.service.model;

public record ReviewView(
        long id,
        UserView reviewer,
        UserView reviewee,
        int rating,
        String comments
) {
}
