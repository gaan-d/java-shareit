package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceTest {
    @Autowired
    UserService userService;

    static CreateUserDto user1;
    static CreateUserDto user2;

    @BeforeAll
    static void beforeAll() {
        user1 = CreateUserDto.builder().name("Vasya").email("vasyapupkin@yandex.ru").build();
        user2 = CreateUserDto.builder().name("Andrey").email("andreyivanov@yandex.ru").build();
    }

    @Test
    void getAllUsers() {
        userService.create(user1);
        UserDto newUser = userService.create(user2);
        List<UserDto> users = userService.findAll().stream().toList();

        assertThat(users.get(1).getId()).isEqualTo(newUser.getId());
        assertThat(users.get(1).getName()).isEqualTo(newUser.getName());
        assertThat(users.get(1).getEmail()).isEqualTo(newUser.getEmail());
    }

    @Test
    void createAndGetUser() {
        UserDto user = userService.create(user1);
        UserDto getUser = userService.findById(user.getId());

        assertThat(user.getId()).isEqualTo(getUser.getId());
        assertThat(user.getName()).isEqualTo(getUser.getName());
        assertThat(user.getEmail()).isEqualTo(getUser.getEmail());
    }

    @Test
    void throwExceptionWhenEmailIsDuplicateWhenCreateUser() {
        userService.create(user1);
        CreateUserDto newUser = CreateUserDto.builder().name("Test").email("vasyapupkin@yandex.ru").build();

        assertThatThrownBy(() -> userService.create(newUser)).isInstanceOf(DuplicatedDataException.class);
    }

    @Test
    void throwExceptionWhenIdIsNull() {
        assertThatThrownBy(() -> userService.findById(null)).isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void updateUser() {
        UserDto user = userService.create(user1);
        UpdateUserDto updateUserDto = UpdateUserDto.builder().name(user2.getName()).email(user2.getEmail()).build();
        UserDto updateUser = userService.update(user.getId(), updateUserDto);

        assertThat(updateUser.getId()).isEqualTo(user.getId());
        assertThat(updateUser.getName()).isEqualTo(user2.getName());
        assertThat(updateUser.getEmail()).isEqualTo(user2.getEmail());
    }

    @Test
    void updateUserNameIsNull() {
        UserDto user = userService.create(user1);
        UpdateUserDto updateDto = UpdateUserDto.builder().email("test@yandex.ru").build();
        UserDto updateUser = userService.update(user.getId(), updateDto);

        assertThat(updateUser.getId()).isEqualTo(user.getId());
        assertThat(updateUser.getName()).isEqualTo(user.getName());
        assertThat(updateUser.getEmail()).isEqualTo(updateDto.getEmail());
    }

    @Test
    void updateUserEmailIsNull() {
        UserDto user = userService.create(user1);
        UpdateUserDto updateDto = UpdateUserDto.builder().name("Vasiliy").build();
        UserDto updateUser = userService.update(user.getId(), updateDto);

        assertThat(updateUser.getId()).isEqualTo(user.getId());
        assertThat(updateUser.getName()).isEqualTo(updateDto.getName());
        assertThat(updateUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void throwExceptionWhenEmailIsDuplicateWhenUpdateUser() {
        UserDto user = userService.create(user1);
        UserDto testUser = userService.create(user2);
        UpdateUserDto updateUser = UpdateUserDto.builder().name("Andrey").email("vasyapupkin@yandex.ru").build();

        assertThatThrownBy(() -> userService.update(testUser.getId(), updateUser))
                .isInstanceOf(DuplicatedDataException.class);
    }

    @Test
    void deleteUser() {
        UserDto user = userService.create(user1);
        userService.deleteById(user.getId());

        assertThatThrownBy(() -> userService.findById(user.getId()))
                .isInstanceOf(NotFoundException.class);
    }
}
