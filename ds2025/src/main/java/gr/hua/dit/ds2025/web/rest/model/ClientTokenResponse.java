package gr.hua.dit.ds2025.web.rest.model;

public record ClientTokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
