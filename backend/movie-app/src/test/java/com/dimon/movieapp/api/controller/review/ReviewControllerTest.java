package com.dimon.movieapp.api.controller.review;

import com.dimon.movieapp.dto.ReviewDto;
import com.dimon.movieapp.models.LocalUser;
import com.dimon.movieapp.models.Review;
import com.dimon.movieapp.repositories.LocalUserRepository;
import com.dimon.movieapp.services.JWTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewControllerTest {

    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetup.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private LocalUserRepository userRepository;

    private ObjectMapper mapper = new ObjectMapper();


    @Test
    public void addReviewTest() throws Exception {
        ReviewDto body = new ReviewDto();
        body.setReview(null);
        body.setFilmId(null);

        LocalUser user = userRepository.findByUsernameIgnoreCase("UserA").get();
        String jwt = jwtService.generateJWT(user);


        mvc.perform(post("/reviews/add")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

}
