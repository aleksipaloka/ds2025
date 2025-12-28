package gr.hua.dit.ds2025.web.rest.model;

import gr.hua.dit.ds2025.web.rest.ClientAuthResource;

public record ClientTokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
