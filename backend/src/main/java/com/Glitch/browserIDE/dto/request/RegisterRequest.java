package com.Glitch.browserIDE.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username is Required")
    @Size(min = 3, max = 50, message = "Username must be betwen 3 and 50")
    private String username;

    @NotBlank(message = "Email is Required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is Required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullname;
}
