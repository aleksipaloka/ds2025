package gr.hua.dit.ds2025.core.service.model;

public record ReviewView(
        long id,
        UserSummaryView reviewer,
        UserSummaryView reviewee,
        int rating,
        String comments
) {
}
