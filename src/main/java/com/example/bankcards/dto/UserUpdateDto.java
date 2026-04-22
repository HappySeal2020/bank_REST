package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import lombok.Data;

@Data
public class UserUpdateDto {
    private String login;
    private String password;
    private Role role;
}

