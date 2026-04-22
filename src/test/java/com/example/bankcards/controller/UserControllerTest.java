package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreateDto;
import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.dto.UserUpdateDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.service.JwtServiceImpl;
import com.example.bankcards.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import static com.example.bankcards.util.Const.*;
import org.springframework.http.MediaType;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class
})


public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userServiceImpl;

    @MockBean
    private JwtServiceImpl jwtServiceImpl;

    @Test
    void shouldViewUser() throws Exception {
        when(userServiceImpl.getAllUsers(0, 5, null))
                .thenReturn(List.of());
        mockMvc.perform(get(REST_MAP+REST_USER))
                .andExpect(status().isOk());
        verify(userServiceImpl).getAllUsers(0, 5, null);
    }

    //успешное создание пользователя
    @Test
    void shouldCreateUser() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setLogin("test_login");
        userCreateDto.setPassword("password");
        userCreateDto.setRole(Role.USER);
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setLogin("test_login");
        userResponseDto.setPassword("password");
        userResponseDto.setRole(Role.USER);
        userResponseDto.setId(1L);

        when(userServiceImpl.create(any(UserCreateDto.class)))
                .thenReturn(userResponseDto);

        mockMvc.perform(post(REST_MAP+REST_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                             "id": 0,
                             "login": "john_login",
                             "password": "123456",
                             "role": "USER"
                }
            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("test_login"));


    }
    //успешное изменение пользователя
    @Test
    void shouldUpdateUser() throws Exception {
        UserUpdateDto user = new UserUpdateDto();
        user.setLogin("john_login");
        user.setRole(Role.USER);
        user.setPassword("{noop}123456");
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setLogin("test_login");
        userResponseDto.setPassword("password");
        userResponseDto.setRole(Role.USER);
        userResponseDto.setId(1L);
        when(userServiceImpl.update(any(Long.class),any(UserUpdateDto.class)))
                .thenReturn(userResponseDto);

        mockMvc.perform(put(REST_MAP+REST_USER+"/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                             "id": 1,
                             "login": "test_login",
                             "password": "123456",
                             "role": "USER"
                }
            """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("test_login"));
    }

    //удаление пользователя
    @Test
    void shouldDeleteUser() throws Exception {
        Long id = 1L;
        when(userServiceImpl.deleteById(any(Long.class)))
                .thenReturn(id);
        mockMvc.perform(delete(REST_MAP+REST_USER+"/1"))
                .andExpect(status().isNoContent());
    }

}
