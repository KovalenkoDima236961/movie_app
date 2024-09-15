package com.dimon.movieapp.config.security;


import com.dimon.movieapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    private UserService userService;
    private JWTRequestFilter jwtRequestFilter;

    @Autowired
    @Lazy
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setJwtRequestFilter(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtRequestFilter,  UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/auth/me").authenticated().
                        requestMatchers("/api/addwatchlist", "/api/inwatchlist/**", "/api/removewatchlist/**").authenticated().
                        requestMatchers("/api/watchlist").authenticated().
                        requestMatchers("/reviews/add").authenticated().
                                anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // CORS PROBLEM
    @Bean
    public WebMvcConfigurer corsConfigurer () {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Authorization", "Content-Type", "X-Requested-With")
                        .allowCredentials(true);
            }
        };
    }
}
