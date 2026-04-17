package com.example.bankcards.controller;


import com.example.bankcards.entity.User;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.InputMismatchException;
import java.util.List;

import static com.example.bankcards.util.Const.*;


@Slf4j
@RestController
@RequestMapping(REST_MAP)
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    //View users + filter + pagination
    @Operation(summary="Админ просматривает пользователей. Фильтр по login, пагинация.")
    @GetMapping(REST_USER)
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsers(@RequestParam(defaultValue = "0") int page, //page number
                               @RequestParam(defaultValue = "5") int size, //page size
                               @RequestParam(required = false) String login //find by
                               ) {
        return userService.getAllUsers(page, size, login);
    }

    @Operation(summary="Админ создаёт пользователя.")
    @PostMapping(REST_USER)
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@RequestBody User user) {
        user.setId(0);
        return userService.save(user);
    }

    @Operation(summary="Админ изменяет пользователя.")
    @PutMapping(REST_USER+"/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        if (id.equals(user.getId() )) {
            log.info("Updating user with id {}", id);
            return userService.save(user);
        }else{
            log.error("Try to update User with incorrect id {}", id);
            throw badRequest(new InputMismatchException("Incorrect id"));
        }
    }

    @Operation(summary="Админ удаляет пользователя.")
    @DeleteMapping(REST_USER+"/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        log.info("Try to delete User with id {}", id);
        userService.deleteById(id);
    }

    private ResponseStatusException badRequest(Exception e) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

}
