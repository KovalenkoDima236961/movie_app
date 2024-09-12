package com.dimon.movieapp.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.dimon.movieapp.models.LocalUser;
import com.dimon.movieapp.repositories.LocalUserRepository;
import com.dimon.movieapp.services.JWTService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

// OK

@SpringBootTest
@AutoConfigureMockMvc
public class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private LocalUserRepository userRepository;

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;


    /**
     * Tests that the verification token is not usable for login.
     */
    @Test
    public void testVerificationTokenNotUsableForLogin() {
        LocalUser user = userRepository.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generateVerificationJWT(user);
        Assertions.assertNull(jwtService.getUsername(token), "Verification token should not contain username.");
    }

    /**
     * Tests that the authentication token generate still returns the username.
     */
    @Test
    public void testAuthTokenReturnsUsername() {
        LocalUser user = userRepository.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generateJWT(user);
        Assertions.assertEquals(user.getUsername(), jwtService.getUsername(token), "Token for auth should contain users username.");
    }

    /**
     * Tests that when someone generates a JWT with an algorithm different to
     * ours the verification rejects the token as the signature is not verified.
     */
    @Test
    public void testLoginJWTNotGeneratedByUs() {
        String token = JWT.create().withClaim("USERNAME", "UserA").sign(Algorithm.HMAC256("NotTheRealSecret"));
        Assertions.assertThrows(SignatureVerificationException.class, () -> jwtService.getUsername(token));
    }

    /**
     * Tests that when a JWT token is generated if it does not contain us as
     * the issuer we reject it.
     */
    @Test
    public void testLoginJWTCorrectlySignedNoIssuer() {
        String token = JWT.create().withClaim("USERNAME", "UserA")
                .sign(Algorithm.HMAC256(algorithmKey));
        Assertions.assertThrows(MissingClaimException.class, () -> jwtService.getUsername(token));
    }

    /**
     * Tests that when someone generates a JWT with an algorithm different to
     * ours the verification rejects the token as the signature is not verified.
     */
    @Test
    public void testResetPasswordJWTNotGeneratedByUs() {
        String token = JWT.create().withClaim("RESET_PASSWORD_EMAIL", "UserA@junit.com").sign(Algorithm.HMAC256("NotTheRealSecret"));
        Assertions.assertThrows(SignatureVerificationException.class, () -> jwtService.getResetPasswordEmail(token));
    }

    /**
     * Tests that when a JWT token is generated if it does not contain us as
     * the issuer we reject it.
     */
    @Test
    public void testResetPasswordJWTCorrectlySignedNoIssuer() {
        String token = JWT.create().withClaim("RESET_PASSWORD_EMAIL", "UserA@junit.com")
                .sign(Algorithm.HMAC256(algorithmKey));

        Assertions.assertThrows(MissingClaimException.class, () -> jwtService.getResetPasswordEmail(token));
    }

    /**
     * Tests the password reset generation and verification.
     */
    @Test
    public void testPasswordResetToken() {
        LocalUser user =  userRepository.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generatePasswordResetJWT(user);
        Assertions.assertEquals(user.getEmail(),
                jwtService.getResetPasswordEmail(token), "Email should match inside JWT.");
    }
}
