package com.dimon.movieapp.service;

import com.dimon.movieapp.dto.LoginBody;
import com.dimon.movieapp.dto.PasswordResetBody;
import com.dimon.movieapp.dto.RegistrationBody;
import com.dimon.movieapp.exceptions.EmailFailureException;
import com.dimon.movieapp.exceptions.EmailNotFoundException;
import com.dimon.movieapp.exceptions.UserAlreadyExistsException;
import com.dimon.movieapp.exceptions.UserNotVerifiedException;
import com.dimon.movieapp.models.LocalUser;
import com.dimon.movieapp.models.VerificationToken;
import com.dimon.movieapp.repositories.LocalUserRepository;
import com.dimon.movieapp.repositories.VerificationRepository;
import com.dimon.movieapp.services.EncryptionService;
import com.dimon.movieapp.services.JWTService;
import com.dimon.movieapp.services.UserService;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

// OK

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private UserService userService;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private LocalUserRepository userRepository;
    @Autowired
    private EncryptionService encryptionService;
    @Autowired
    private VerificationRepository verificationRepository;

    /**
     * Tests the registration process of the user.
     * @throws MessagingException Thrown if the mocked email service fails somehow.
     */
    @Test
    @Transactional
    @Order(1)
    public void testRegisterUser() throws MessagingException {

        RegistrationBody body = new RegistrationBody();
        body.setUsername("UserA");
        body.setEmail("UserServiceTest$testRegisterUser@junit.com");
        body.setPassword("MySecretPassword123");
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(body), "Username should already be in use.");

        body.setUsername("UserServiceTest$testRegisterUser");
        body.setEmail("UserA@junit.com");
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(body), "Email should already be in use.");

        body.setEmail("UserServiceTest$testRegisterUser@junit.com");
        Assertions.assertDoesNotThrow(() -> userService.registerUser(body),
                "User should register successfully.");

        Assertions.assertEquals(body.getEmail(), greenMailExtension.getReceivedMessages()[0]
                .getRecipients(Message.RecipientType.TO)[0].toString());
    }

    /**
     * Tests the loginUser method.
     * @throws UserNotVerifiedException
     * @throws EmailFailureException
     */
    @Test
    @Transactional
    @Order(3)
    public void testLoginUser() throws UserNotVerifiedException, EmailFailureException {
        LoginBody body = new LoginBody();
        body.setEmail("UserA-NotExists@junit.com");
        body.setPassword("PasswordA123-BadPassword");
        Assertions.assertNull(userService.loginUser(body), "The user should not exist.");

        body.setEmail("UserA@junit.com");
        Assertions.assertNull(userService.loginUser(body), "The password should be incorrect.");

        body.setPassword("PasswordA123");
        Assertions.assertNotNull(userService.loginUser(body), "The user should login successfully.");

        body.setEmail("UserB@junit.com");
        body.setPassword("PasswordB123");
        try {
            userService.loginUser(body);
            Assertions.assertTrue(false, "User should not have email verified.");
        } catch (UserNotVerifiedException exception) {
            Assertions.assertTrue(exception.isNewEmailSent(), "Email verification should be sent.");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }

        try {
            userService.loginUser(body);
            Assertions.assertTrue(false, "User should not have email verified.");
        }catch (UserNotVerifiedException ex) {
            Assertions.assertFalse(ex.isNewEmailSent(), "Email verification should not be resent.");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }
    }

    /**
     * Tests the verifyUser method.
     * @throws EmailFailureException
     */
    @Test
    @Transactional
    @Order(2)
    public void testVerifyUser() throws EmailFailureException {
        Assertions.assertFalse(userService.verifyUser("Bad Token"), "Token that is bad or does not exist should return false.");
        LoginBody body = new LoginBody();
        body.setEmail("UserB@junit.com");
        body.setPassword("PasswordB123");

        try {
            userService.loginUser(body);
            Assertions.assertTrue(false, "User should not have email verified.");
        }catch (UserNotVerifiedException ex) {
            List<VerificationToken> tokens = verificationRepository.findByUser_IdOrderByIdDesc(2L);
            String token = tokens.get(0).getToken();
            Assertions.assertTrue(userService.verifyUser(token), "Token should be valid.");
            Assertions.assertNotNull(body, "The user should now be verified.");
        }
    }

    /**
     * Tests the forgotPassword method in the User Service.
     * @throws MessagingException
     */
    @Test
    @Transactional
    public void testForgotPassword() throws MessagingException {
        Assertions.assertThrows(EmailNotFoundException.class,
                () -> userService.forgotPassword("UserNotExist@junit.com"));
        Assertions.assertDoesNotThrow(() -> userService.forgotPassword(
                "UserA@junit.com"), "Non existing email should be rejected.");
        Assertions.assertEquals("UserA@junit.com",
                greenMailExtension.getReceivedMessages()[0]
                        .getRecipients(Message.RecipientType.TO)[0].toString(), "Password " +
                        "reset email should be sent.");
    }

    /**
     * Tests the resetPassword method in the User Service.
     * @throws MessagingException
     */
    @Test
    @Transactional
    public void testResetPassword() {
        LocalUser user = userRepository.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generatePasswordResetJWT(user);
        PasswordResetBody body = new PasswordResetBody();
        body.setToken(token);
        body.setPassword("Password123456");
        userService.resetPassword(body);
        user = userRepository.findByUsernameIgnoreCase("UserA").get();
        Assertions.assertTrue(encryptionService.verifyPassword("Password123456",
                user.getPassword()), "Password change should be written to DB.");
    }
}
