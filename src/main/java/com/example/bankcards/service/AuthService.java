package com.example.bankcards.service;

import com.example.bankcards.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor

public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userInDb = userRepository.findByLogin(username);
        log.info("username: {}, user in db role: {}", username, userInDb.getRole());
        return User.withUsername(userInDb.getLogin())
                .password(userInDb.getPassword())
                .roles(userInDb.getRole().getRole())
                .build();
    }



}
