package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User mapToCreateUser(CreateUserDto createDto) {
        return User.builder()
                .name(createDto.getName())
                .email(createDto.getEmail())
                .build();
    }

    public static User updateUser(User user, UpdateUserDto updateDto) {
        if (updateDto.hasEmail()) {
            user.setEmail(updateDto.getEmail());
        }

        if (updateDto.hasName()) {
            user.setName(updateDto.getName());
        }
        return user;
    }
}
