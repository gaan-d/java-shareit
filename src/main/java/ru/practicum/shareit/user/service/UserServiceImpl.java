package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.user.mapper.UserMapper.mapToUserDto;
import static ru.practicum.shareit.user.mapper.UserMapper.updateUser;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto create(CreateUserDto userCreateDto) {
        validateEmail(userCreateDto.getEmail());
        User user = UserMapper.mapToCreateUser(userCreateDto);
        return mapToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto findById(Long userId) {
        return mapToUserDto(validateExistenceById(userId));
    }

    @Override
    public UserDto update(Long userId, UpdateUserDto userUpdateDto) {
        User user = validateExistenceById(userId);
        validateEmailOnUpdate(userUpdateDto.getEmail(), userId);
        updateUser(user, userUpdateDto);
        userRepository.save(user);
        return mapToUserDto(user);
    }

    @Override
    public void deleteById(Long userId) {
        validateExistenceById(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public User validateExistenceById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
    }

    public void validateEmail(String email) {
        Optional<User> userExistsCheck = userRepository.findByEmail(email);
        if (userExistsCheck.isPresent()) {
            throw new DuplicatedDataException(String.format("Пользователь с email %s уже существует", email));
        }
    }

    public void validateEmailOnUpdate(String email, Long userId) {
        userRepository.findByEmail(email).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(userId)) {
                throw new DuplicatedDataException(String.format("Пользователь с email %s уже существует", email));
            }
        });
    }
}
