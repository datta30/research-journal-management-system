package com.journal.auth.dto;

import com.journal.user.model.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @Size(min = 8, message = "Password must be at least 8 characters long") String password,
        String organization,
        RoleType role
) {
}
