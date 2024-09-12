package com.dimon.movieapp.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The information required to register a user.
 */
@Getter
@Setter
public class RegistrationBody {

    /** The username. */
    @NotNull
    @NotBlank
    @Size(min=3, max=255)
    private String username;
    /** The email. */
    @NotNull
    @NotBlank
    @Email
    private String email;
    /** The password. */
    @NotNull
    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")
    @Size(min=6, max=32)
    private String password;

}
