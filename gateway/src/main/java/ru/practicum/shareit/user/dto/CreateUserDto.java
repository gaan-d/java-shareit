package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserDto {
    @NotBlank(message = "Имя пользователя не может быть пустым.")
    String name;

    @NotBlank(message = "Email не может быть пустым.")
    @Email(message = "Неверный формат email.")
    String email;
}
