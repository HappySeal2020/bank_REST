package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreateDto;
import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.dto.UserUpdateDto;
import com.example.bankcards.dto.specification.UserSpecification;
import com.example.bankcards.entity.UserMapper;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service for managing users
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

@Override
@Transactional
public UserResponseDto create(UserCreateDto dto){
    User user = userMapper.toEntity(dto);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    User savedUser = userRepository.save(user);
    log.info("Create user: {}", user);
    return userMapper.toDto(savedUser);
}
@Override
@Transactional
public UserResponseDto update(Long id, UserUpdateDto dto){
    User existing = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
    existing.setLogin(dto.getLogin());
    if (dto.getPassword() != null) {
        existing.setPassword(passwordEncoder.encode(dto.getPassword()));
    }
    existing.setRole(dto.getRole());
    User saved = userRepository.save(existing);
    userRepository.flush();
    //log.info("Update user: {}", existing);
    log.info("Update user: {}", saved);
    return userMapper.toDto(saved);
}


@Override
public List<User> getAllUsers(int page, int size, String login) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<User> userPage;
        if (login != null && !login.isBlank()) {
            userPage = userRepository.findAll(UserSpecification.filter(login),pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        return userPage.getContent();
    }

@Override
public Long deleteById(Long id) {
        log.info("Deleting user: {}", id);
        userRepository.deleteById(id);
        return id;
    }

}
