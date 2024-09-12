package com.dimon.movieapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body to reset a password using a password reset token.
 */
@Getter
@Setter
public class PasswordResetBody {

    /** The token to authenticate the request. */
    @NotBlank
    @NotNull
    private String token;
    /** The password to set to the account. */
    @NotNull
    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")
    @Size(min=6, max=32)
    private String password;

}
