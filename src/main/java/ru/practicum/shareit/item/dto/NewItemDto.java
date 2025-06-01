package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewItemDto {
    @NotBlank(message = "Наименование предмета не может быть пустым")
    String name;

    @NotBlank(message = "Описание предмета не может быть пустым")
    String description;

    @NotNull(message = "Параметр \"Статус аренды\" не может быть пустым")
    Boolean available;

    Long ownerId;

    @Builder.Default
    int rentalCount = 0;
}
