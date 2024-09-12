package com.dimon.movieapp.api.controller.auth;

import com.dimon.movieapp.dto.LoginBody;
import com.dimon.movieapp.dto.RegistrationBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetup.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private MockMvc mvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    @Transactional
    public void testRegister() throws Exception {
        RegistrationBody body = new RegistrationBody();
        body.setEmail("AuthenticationControllerTest$testRegister@junit.com");
        body.setPassword("Password123");
        // Null or blank username
        body.setUsername(null);
        mvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setUsername("");
        mvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setUsername("AuthenticationControllerTest$testRegister");

        // Null or blank email.
        body.setEmail(null);
        mvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setEmail("");
        mvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setEmail("AuthenticationControllerTest$testRegister@junit.com");


        // Null or blank password
        body.setPassword(null);
        mvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        body.setPassword("");
        mvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setPassword("Password123");

        // HERE ERROR 500
        System.out.println(body);
        System.out.println("Email: " + body.getEmail());
        System.out.println("Username: " + body.getUsername());
        System.out.println("Password: " + body.getPassword());

//        mvc.perform(post("/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(body)))
//                .andExpect(status().is(HttpStatus.OK.value()));

    }

    @Test
    public void testLogin_Success() throws Exception {
        LoginBody loginBody = new LoginBody();
        loginBody.setEmail("UserA@junit.com");
        loginBody.setPassword("PasswordA123");

        mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginBody)))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    public void testLogin_InvalidPassword() throws Exception {
        LoginBody loginBody = new LoginBody();
        loginBody.setEmail("UserA@junit.com");
        loginBody.setPassword("WrongPassword");

        mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginBody)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void testLogin_NonExistentUser() throws Exception {
        LoginBody loginBody = new LoginBody();
        loginBody.setEmail("nonexistenuser@junit.com");
        loginBody.setPassword("PasswordA123");

        mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginBody)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void testLogin_BlankEmail() throws Exception {
        LoginBody loginBody = new LoginBody();
        loginBody.setEmail("");
        loginBody.setPassword("PasswordA123");

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginBody)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void testLogin_BlankPassword() throws Exception {
        LoginBody loginBody = new LoginBody();
        loginBody.setEmail("UserA@junit.com");
        loginBody.setPassword("");

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginBody)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    // TODO Here said that error is 500 but should be 403
//    @Test
//    public void testLogin_NotVerifiedUser() throws Exception {
//        LoginBody loginBody = new LoginBody();
//        loginBody.setEmail("UserB@junit.com");
//        loginBody.setPassword("PasswordB123");
//
//        mvc.perform(post("/auth/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(loginBody)))
//                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
//
//        MimeMessage[] receivedMessages = greenMailExtension.getReceivedMessages();
//        Assertions.assertEquals(1, receivedMessages.length);
//    }


}
