package com.example.bankcards.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.bankcards.util.Const.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class ControllerAccessTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn403Cards() throws Exception {
        mockMvc.perform(get(REST_MAP+REST_CARD))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn403Users() throws Exception {
        mockMvc.perform(get(REST_MAP+REST_USER))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn403Client() throws Exception {
        mockMvc.perform(get(REST_MAP+REST_CLIENT))
                .andExpect(status().isForbidden());
    }
}
