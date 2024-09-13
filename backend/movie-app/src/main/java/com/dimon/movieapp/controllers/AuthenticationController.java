package com.dimon.movieapp.controllers;

import com.dimon.movieapp.dto.LoginBody;
import com.dimon.movieapp.dto.LoginResponse;
import com.dimon.movieapp.dto.PasswordResetBody;
import com.dimon.movieapp.dto.RegistrationBody;
import com.dimon.movieapp.exceptions.EmailFailureException;
import com.dimon.movieapp.exceptions.EmailNotFoundException;
import com.dimon.movieapp.exceptions.UserAlreadyExistsException;
import com.dimon.movieapp.exceptions.UserNotVerifiedException;
import com.dimon.movieapp.models.LocalUser;
import com.dimon.movieapp.repositories.LocalUserRepository;
import com.dimon.movieapp.services.JWTService;
import com.dimon.movieapp.services.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final LocalUserRepository localUserRepository;
    private UserService userService;
    private JWTService jwtService;
    private static final String CLIENT_ID = "853926160324-e2hab7pkqlh5jnjqlbaoa6v1ju1iujp4.apps.googleusercontent.com";

    @Autowired
    public AuthenticationController(UserService userService, LocalUserRepository localUserRepository, JWTService jwtService) {
        this.userService = userService;
        this.localUserRepository = localUserRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationBody registrationBody) {
        System.out.println("Register user");
        System.out.println(registrationBody);
        try {
            userService.registerUser(registrationBody);
            return ResponseEntity.ok().build();
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EmailFailureException e) {
            System.out.println("Email Failure Exception");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody Map<String, String> request) {
        String googleToken = request.get("token");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(CLIENT_ID)) // Set your Google client ID here
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(googleToken);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");

                // Check if the user exists in the database, and if not, create a new user
                // Assuming userService manages your user logic
                Optional<LocalUser> optionalUser = localUserRepository.findByEmailIgnoreCase(email);
                LocalUser user = optionalUser.orElseGet(() -> {
                    LocalUser newUser = new LocalUser();
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setEmailVerified(true); // Google already verifies email
                    return localUserRepository.save(newUser);
                });

                // Generate JWT for the user
                String jwt = jwtService.generateJWT(user);
                return ResponseEntity.ok(Map.of("jwt", jwt));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google token");
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Google token verification failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
        String jwt = null;
        try {
            jwt = userService.loginUser(loginBody);
        } catch (UserNotVerifiedException ex) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            String reason = "USER_NOT_VERIFIED";
            if(ex.isNewEmailSent()) {
                reason += "_EMAIL_RESENT";
            }
            response.setFailureReason(reason);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (EmailFailureException ex ) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        System.out.println("Jwt: " + jwt);

        if(jwt == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/check_authenticated")
    public ResponseEntity<?> checkAuthentication(@RequestHeader("Authorization") String token) {
        // Remove "Bearer " if it's present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        System.out.println(token);

        if (userService.checkAuthentication(token)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        System.out.println(token);
        if(userService.verifyUser(token)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/me")
    public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user) {
        return user;
    }

    @PostMapping("/forgot")
    public ResponseEntity forgotPassword(@RequestBody String email) {
        try {
            userService.forgotPassword(email);
            return ResponseEntity.ok().build();
        } catch (EmailNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/reset")
    public ResponseEntity resetPassword(@Valid @RequestBody PasswordResetBody body) {
        userService.resetPassword(body);
        return ResponseEntity.ok().build();
    }

}
