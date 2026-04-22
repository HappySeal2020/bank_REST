package com.example.bankcards.entity;

import com.example.bankcards.dto.UserCreateDto;
import com.example.bankcards.dto.UserResponseDto;

import org.springframework.stereotype.Component;


@Component
public class UserMapper {
    public User toEntity(UserCreateDto dto) {
        return User.builder()
                .login(dto.getLogin())
                .password(dto.getPassword())
                .role(dto.getRole())
                .build();
    }

    public UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .login(user.getLogin())
                .role(user.getRole())
                .password(user.getPassword())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
