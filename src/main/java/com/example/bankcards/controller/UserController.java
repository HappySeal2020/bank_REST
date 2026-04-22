package com.example.bankcards.controller;


import com.example.bankcards.dto.UserCreateDto;
import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.dto.UserUpdateDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.InputMismatchException;
import java.util.List;

import static com.example.bankcards.util.Const.*;

/**
 * Controller for operations with users
 */
@Slf4j
@RestController
@RequestMapping(REST_MAP)
public class UserController {
    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }


    //View users + filter + pagination
    @Operation(summary="Админ просматривает пользователей. Фильтр по login, пагинация.")
    @GetMapping(REST_USER)
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsers(@RequestParam(defaultValue = "0") int page, //page number
                               @RequestParam(defaultValue = "5") int size, //page size
                               @RequestParam(required = false) String login //find by
                               ) {
        return userServiceImpl.getAllUsers(page, size, login);
    }

    @Operation(summary="Админ создаёт пользователя.")
    @PostMapping(REST_USER)
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody UserCreateDto dto) {
        return userServiceImpl.create(dto);
    }

    @Operation(summary="Админ изменяет пользователя.")
    @PutMapping(REST_USER+"/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserResponseDto updateUser(@PathVariable Long id, @RequestBody UserUpdateDto dto) {
        return userServiceImpl.update(id, dto);
    }

    @Operation(summary="Админ удаляет пользователя.")
    @DeleteMapping(REST_USER+"/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        log.info("Try to delete User with id {}", id);
        userServiceImpl.deleteById(id);
    }

    private ResponseStatusException badRequest(Exception e) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

}
