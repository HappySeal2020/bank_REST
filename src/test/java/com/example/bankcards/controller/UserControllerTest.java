package com.example.bankcards.controller;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.JwtService;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
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
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @Test
    void shouldViewUser() throws Exception {
        when(userService.getAllUsers(0, 5, null))
                .thenReturn(List.of());
        mockMvc.perform(get(REST_MAP+REST_USER))
                .andExpect(status().isOk());
        verify(userService).getAllUsers(0, 5, null);
    }

    //успешное изменение пользователя
    @Test
    void shouldCreateUser() throws Exception {
        User user = new User();
        user.setId(1);
        user.setLogin("john_login");
        user.setRole(Role.USER);
        user.setPassword("{noop}123456");
        when(userService.save(any(User.class)))
                .thenReturn(user);

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
                .andExpect(jsonPath("$.login").value("john_login"));


    }
    //успешное изменение пользователя
    @Test
    void shouldUpdateUser() throws Exception {
        User user = new User();
        user.setId(1);
        user.setLogin("john_login");
        user.setRole(Role.USER);
        user.setPassword("{noop}123456");
        when(userService.save(any(User.class)))
                .thenReturn(user);

        mockMvc.perform(put(REST_MAP+REST_USER+"/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                             "id": 1,
                             "login": "john_login",
                             "password": "123456",
                             "role": "USER"
                }
            """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("john_login"));
    }

    //изменение пользователя - несоответствие id
    @Test
    void shouldThrowBadIdUpdateUser() throws Exception {
        User user = new User();
        user.setId(1);
        user.setLogin("john_login");
        user.setRole(Role.USER);
        user.setPassword("{noop}123456");
        when(userService.save(any(User.class)))
                .thenReturn(user);
        mockMvc.perform(put(REST_MAP+REST_USER+"/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                             "id": 1,
                             "login": "john_login",
                             "password": "123456",
                             "role": "USER"
                }
            """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Incorrect id")));
    }

    //удаление пользователя
    @Test
    void shouldDeleteUser() throws Exception {
        Long id = 1L;
        when(userService.deleteById(any(Long.class)))
                .thenReturn(id);
        mockMvc.perform(delete(REST_MAP+REST_USER+"/1"))
                .andExpect(status().isNoContent());
    }

}
