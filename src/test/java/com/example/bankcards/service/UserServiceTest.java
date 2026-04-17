package com.example.bankcards.service;
import com.example.bankcards.entity.*;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldSaveUser() {
        User user = new User();
        user.setId(1L);
        user.setPassword("1234");
        user.setLogin("Pierre");
        user.setRole(Role.USER);
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        User userSaved = userService.save(user);
        assertNotNull(userSaved);
        assertEquals(user.getId(), userSaved.getId());
        assertEquals(user.getPassword(), userSaved.getPassword());
        assertEquals(user.getLogin(), userSaved.getLogin());
        assertEquals(user.getRole(), userSaved.getRole());
    }

    @Test
    void shouldEncodePasswordBeforeSaving() {
        User user = new User();
        user.setPassword("123456");
        when(passwordEncoder.encode("123456"))
                .thenReturn("encoded_password");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        User saved = userService.save(user);
        assertEquals("encoded_password", saved.getPassword());
        verify(passwordEncoder).encode("123456");
        assertNotEquals("123456", saved.getPassword());
    }

    @Test
    void shouldGetAllUsersNoFilter() {
        User user = new User();
        user.setId(1L);
        user.setPassword("1234");
        user.setLogin("Pierre");
        user.setRole(Role.USER);
        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(page);
        List <User> response=userService.getAllUsers(0, 5, null);

        assertEquals(1, response.size());
        assertEquals("Pierre", response.get(0).getLogin());
    }

    @Test
    void shouldGetAllUsersWithFilter() {
        User user = new User();
        user.setId(1L);
        user.setLogin("Pierre");

        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        List<User> response = userService.getAllUsers(0, 5, "Pier");

        assertEquals(1, response.size());
        assertEquals("Pierre", response.get(0).getLogin());
    }

    @Test
    void shouldGetAllUsers() {
        Long id = 1L;
        doNothing().when(userRepository).deleteById(id);
        userService.deleteById(id);
        verify(userRepository,times(1)).deleteById(id);
    }


}
