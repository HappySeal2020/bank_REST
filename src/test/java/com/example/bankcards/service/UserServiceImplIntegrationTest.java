package com.example.bankcards.service;

import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.dto.UserUpdateDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserServiceImplIntegrationTest {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Transactional
    @Rollback
    @Test
    void shouldStoreEncodedPassword() {
        UserUpdateDto user = new UserUpdateDto();
        user.setLogin("TrustedInstaller");
        user.setPassword("123456");
        user.setRole(Role.USER);

        UserResponseDto saved = userServiceImpl.update(1L, user);
        assertTrue(passwordEncoder.matches("123456", saved.getPassword()));
    }
}
