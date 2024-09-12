package com.dimon.movieapp.api.security;

import com.dimon.movieapp.models.LocalUser;
import com.dimon.movieapp.repositories.LocalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Primary
public class JUnitUserDetailsService {

    @Autowired
    private LocalUserRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<LocalUser> opUser = userRepository.findByUsernameIgnoreCase(username);
        if(opUser.isPresent())
            return opUser.get();
        return null;
    }
}
