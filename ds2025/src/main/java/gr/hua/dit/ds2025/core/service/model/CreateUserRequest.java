package gr.hua.dit.ds2025.core.service.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotNull @NotBlank @Size(max = 100) String name,
        @NotNull @NotBlank @Size(max = 100) String lastName,
        @NotNull @NotBlank @Size(max = 100) String username,
        @NotNull @NotBlank @Size(max = 100) @Email String email,
        @NotNull @NotBlank @Size(min = 4, max = 24) String password
) {
}
