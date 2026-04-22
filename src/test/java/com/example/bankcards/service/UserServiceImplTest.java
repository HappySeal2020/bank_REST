package com.example.bankcards.service;
import com.example.bankcards.dto.UserCreateDto;
import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.entity.*;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    private UserMapper userMapper = new UserMapper();

    //@Spy
    //private PasswordEncoder passwordEncoder;

    @Test
    void shouldCreateUser() {

        UserCreateDto user = new UserCreateDto();
        user.setPassword("1234");
        user.setLogin("Pierre");
        user.setRole(Role.USER);
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        UserResponseDto userSaved = userServiceImpl.create(user);
        assertNotNull(userSaved);
        assertEquals(user.getLogin(), userSaved.getLogin());
        assertEquals(user.getRole(), userSaved.getRole());
    }

    @Test
    void shouldEncodePasswordBeforeSaving() {
        UserCreateDto user = new UserCreateDto();
        user.setPassword("123456");
        when(passwordEncoder.encode("123456"))
                .thenReturn("encoded_password");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        UserResponseDto saved = userServiceImpl.create(user);
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
        List <User> response= userServiceImpl.getAllUsers(0, 5, null);

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

        List<User> response = userServiceImpl.getAllUsers(0, 5, "Pier");

        assertEquals(1, response.size());
        assertEquals("Pierre", response.get(0).getLogin());
    }

    @Test
    void shouldGetAllUsers() {
        Long id = 1L;
        doNothing().when(userRepository).deleteById(id);
        userServiceImpl.deleteById(id);
        verify(userRepository,times(1)).deleteById(id);
    }


}
