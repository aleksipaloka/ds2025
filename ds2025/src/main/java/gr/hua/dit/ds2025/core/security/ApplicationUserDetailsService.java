package gr.hua.dit.ds2025.core.security;

import gr.hua.dit.ds2025.core.model.User;
import gr.hua.dit.ds2025.core.repositories.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public ApplicationUserDetailsService(final UserRepository userRepository) {
        if (userRepository == null) throw new NullPointerException();
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final @NonNull String username) throws UsernameNotFoundException {
        if (username.isBlank()) throw new IllegalArgumentException();
        final User user = this.userRepository
                .findByUsername(username)
                .orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("Person with username" + username + " does not exist");
        }
        return new ApplicationUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );

    }
}
