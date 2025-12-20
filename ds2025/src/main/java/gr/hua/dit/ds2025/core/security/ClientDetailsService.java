package gr.hua.dit.ds2025.core.security;

import java.util.Optional;

public interface ClientDetailsService {

    Optional<ClientDetails> authenticate(final String id, final String secret);

}
