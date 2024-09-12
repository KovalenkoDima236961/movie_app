package com.dimon.movieapp.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * The response object sent from login request.
 */
@Getter
@Setter
public class LoginResponse {

    /** The JWT token to be used for authentication. */
    private String jwt;
    /** Was the login process successful? */
    private boolean success;
    /** The reason for failure on login. */
    private String failureReason;


}
