package gr.hua.dit.ds2025.core.security;

import java.util.Set;

public record ClientDetails(
        String id,
        String secret,
        Set<String> roles
) {}
