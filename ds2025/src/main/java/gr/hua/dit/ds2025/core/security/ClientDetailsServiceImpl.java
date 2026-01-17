package gr.hua.dit.ds2025.core.security;

import gr.hua.dit.ds2025.core.model.Client;
import gr.hua.dit.ds2025.core.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {

    private final ClientRepository clientRepository;

    public ClientDetailsServiceImpl(final ClientRepository clientRepository) {
        if (clientRepository == null) throw new NullPointerException();
        this.clientRepository = clientRepository;
    }

    @Override
    public Optional<ClientDetails> authenticate(final String id, final String secret) {
        if (id == null) throw new NullPointerException();
        if (id.isBlank()) throw new IllegalArgumentException();
        if (secret == null) throw new NullPointerException();
        if (secret.isBlank()) throw new IllegalArgumentException();

        final Client client = this.clientRepository.findByName(id).orElse(null);
        if (client == null) {
            return Optional.empty();
        }

        if (Objects.equals(client.getSecret(), secret)) {

            final ClientDetails clientDetails = new ClientDetails(
                    client.getName(),
                    client.getSecret(),
                    Arrays.stream(client.getRolesCsv().split(","))
                            .map(String::strip)
                            .map(String::toUpperCase)
                            .collect(Collectors.toSet()));
            return Optional.of(clientDetails);
        } else {
            return Optional.empty();
        }
    }
}