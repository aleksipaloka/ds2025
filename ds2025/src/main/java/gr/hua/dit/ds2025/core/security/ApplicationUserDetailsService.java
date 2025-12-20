package gr.hua.dit.ds2025.core.security;

import gr.hua.dit.ds2025.core.model.User;
import gr.hua.dit.ds2025.core.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class ApplicationUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public ApplicationUserDetailsService(final UserRepository userRepository) {
        if (userRepository == null) throw new NullPointerException();
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        if (username == null) throw new NullPointerException();
        if (username.isBlank()) throw new IllegalArgumentException();
        final User person = this.userRepository
                .findByUsername(username)
                .orElse(null);
        if (person == null) {
            throw new UsernameNotFoundException("Person with username" + username + " does not exist");
        }
        return new ApplicationUserDetails(
                person.getId(),
                person.getEmail(),
                person.getPassword(),
                person.getRole()
        );

    }
}
