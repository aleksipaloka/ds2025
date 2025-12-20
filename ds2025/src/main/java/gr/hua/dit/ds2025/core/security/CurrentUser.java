package gr.hua.dit.ds2025.core.security;

import gr.hua.dit.ds2025.core.model.Role;

public record CurrentUser(long id, String username, Role role) {
}
