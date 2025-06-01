package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable("userId") Long userId) {
        return userService.findById(userId);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody CreateUserDto createRequestDto) {
        return userService.create(createRequestDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable("userId") Long userId, @Valid @RequestBody UpdateUserDto requestDto) {
        return userService.update(userId, requestDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") Long userId) {
        userService.deleteById(userId);
    }
}
