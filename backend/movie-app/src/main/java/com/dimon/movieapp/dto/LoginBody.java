package com.dimon.movieapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginBody {

    /** The username to log in with. */
    @NotNull
    @NotBlank
    private String email;
    /** The password to log in with. */
    @NotNull
    @NotBlank
    private String password;

}
