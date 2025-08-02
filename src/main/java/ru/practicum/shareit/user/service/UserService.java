package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {
    List<UserDto> findAll();

    @Transactional
    UserDto create(CreateUserDto userCreateDto);

    UserDto findById(Long userId);

    @Transactional
    UserDto update(Long userId, UpdateUserDto userUpdateDto);

    @Transactional
    void deleteById(Long userId);

    User validateExistenceById(Long userId);
}
