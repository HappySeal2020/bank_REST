package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserDto {
    @JsonProperty("id")
    private final long id;
    @JsonProperty("login")
    private final String login;
    @JsonProperty("password")
    private final String password;
    @JsonProperty("role")
    private final Role role;


}
