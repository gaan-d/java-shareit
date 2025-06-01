package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto create(CreateUserDto userCreateDto);

    UserDto findById(Long userId);

    UserDto update(Long userId, UpdateUserDto userUpdateDto);

    void deleteById(Long userId);

    void validateEmail(String email);

    User validateExistenceById(Long userId);
}
