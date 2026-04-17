package com.example.bankcards.service;

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
public class UserServiceIntegrationTest {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Transactional
    @Rollback
    @Test
    void shouldStoreEncodedPassword() {
        User user = new User();
        user.setId(1L);
        user.setLogin("TrustedInstaller");
        user.setPassword("123456");
        user.setRole(Role.USER);

        User saved = userService.save(user);
        assertTrue(passwordEncoder.matches("123456", saved.getPassword()));
    }
}
