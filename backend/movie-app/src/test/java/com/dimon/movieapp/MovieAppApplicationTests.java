package com.dimon.movieapp;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Primary;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(1)
class MovieAppApplicationTests {

    @Test
    @Order(1)
    void contextLoads() {
    }

}
