package gr.hua.dit.ds2025.core.security;

import gr.hua.dit.ds2025.core.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("RedundantMethodOverride")
public class ApplicationUserDetails implements UserDetails {

    private final long personId;
    private final String username;
    private final String passwordHash;
    private final Role role;
    private final boolean enabled;

    public ApplicationUserDetails(final long personId,
                                  final String username,
                                  final String passwordHash,
                                  final Role role,
                                  boolean enabled) {
        if (personId <= 0) throw new IllegalArgumentException();
        if (username == null) throw new NullPointerException();
        if (username.isBlank()) throw new IllegalArgumentException();
        if (passwordHash == null) throw new NullPointerException();
        if (passwordHash.isBlank()) throw new IllegalArgumentException();
        if (role == null) throw new NullPointerException();

        this.personId = personId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.enabled = enabled;
    }

    public long personId() {
        return this.personId;
    }

    public Role type() {
        return this.role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final String role;
        if (this.role == Role.USER) role = "ROLE_USER";
        else if (this.role == Role.ADMIN) role = "ROLE_ADMIN";
        else throw new RuntimeException("Invalid type: " + this.role);
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
