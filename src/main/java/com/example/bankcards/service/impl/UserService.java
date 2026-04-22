package com.example.bankcards.service.impl;


import com.example.bankcards.dto.UserCreateDto;
import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.dto.UserUpdateDto;
import com.example.bankcards.entity.User;
import java.util.List;

/**
 * Interface for managing users
 * Provides operations: save, delete, read
 */
public interface UserService {

    /**
     * Create user
     * @param dto user
     * @return user after create
     */

    UserResponseDto create(UserCreateDto dto);

    /**
     * Update user
     * @param id user id
     * @param dto user
     * @return user after update
     */
    UserResponseDto update(Long id, UserUpdateDto dto);
    /**
     * Retrieve list of users
     * @param page number of page
     * @param size page size
     * @param login user's login
     * @return list of users
     */
    List<User> getAllUsers(int page, int size, String login);

    /**
     * delete user
     * @param id user id
     * @return id of deleted user
     */
    Long deleteById(Long id);
}
