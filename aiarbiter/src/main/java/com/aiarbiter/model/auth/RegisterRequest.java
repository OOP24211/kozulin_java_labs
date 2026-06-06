package com.aiarbiter.model.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50)
        @Pattern(regexp = "[A-Za-z0-9_]+",
                 message = "Username must contain only Latin letters, digits and underscores")
        String username,
        @NotBlank @Size(min = 8, max = 100)
        @Pattern(regexp = "[A-Za-z0-9!@#$%^&*()_+\\-=\\[\\]{};':|,.<>/?`~\\\\]+",
                 message = "Password must contain only Latin letters, digits and special characters")
        String password
) {}
