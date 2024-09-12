package com.dimon.movieapp.config.security;


import com.auth0.jwt.exceptions.JWTDecodeException;
import com.dimon.movieapp.models.LocalUser;
import com.dimon.movieapp.repositories.LocalUserRepository;
import com.dimon.movieapp.services.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private JWTService jwtService;
    private LocalUserRepository userRepository;

    @Autowired
    public JWTRequestFilter(JWTService jwtService, LocalUserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        System.out.println(request);
        UsernamePasswordAuthenticationToken token = checkToken(tokenHeader);
        if(token != null) {
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        }
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken checkToken(String token) {
        if(token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            System.out.println(token);
            try {
                String username = jwtService.getUsername(token);
                System.out.println(username);
                Optional<LocalUser> opUser = userRepository.findByUsernameIgnoreCase(username);
                if(opUser.isPresent()) {
                    LocalUser user = opUser.get();
                    if(user.isEmailVerified()) {
                        // SET ROLES
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("Return authentication");
                        return authentication;
                    }
                }
            }catch (JWTDecodeException ex) {
                // log here
            }
        }
        SecurityContextHolder.getContext().setAuthentication(null);
        return null;
    }
}
