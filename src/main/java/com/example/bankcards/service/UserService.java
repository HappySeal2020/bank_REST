package com.example.bankcards.service;

import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.bankcards.entity.User;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
/*
    public UserService(UserRepository userRepository
    //        , BCryptPasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        //this.passwordEncoder = passwordEncoder;
    }
  */


//ADMIN - CREATE user
@Transactional
public User save(User user) {
    //BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    log.info("Adding user: {}", user.getLogin());
    String hashedPassword = user.getPassword();
    user.setPassword(passwordEncoder.encode(hashedPassword));
    log.info("Save user: {}", user);
    return userRepository.save(user);
}

    //READ
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("User not found id=" + id));
    }

}
