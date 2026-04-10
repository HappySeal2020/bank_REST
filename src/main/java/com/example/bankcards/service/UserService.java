package com.example.bankcards.service;

import com.example.bankcards.dto.specification.UserSpecification;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


//ADMIN - CREATE user
@Transactional
public User save(User user) {
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

    public List<User> getAllUsers(int page, int size, String login) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<User> userPage;
        if (login != null && !login.isBlank()) {
            userPage = userRepository.findAll(UserSpecification.filter(login),pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        return userPage.getContent();
    }

}
