package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {
    private final JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto booker = UserDto.builder()
                .id(1L)
                .name("Vasya")
                .email("vasyapupkin@yandex.ru")
                .build();

        ItemDto item = ItemDto.builder()
                .id(2L)
                .name("TestItem")
                .description("TestDescription")
                .available(true)
                .comments(Collections.emptyList())
                .requestId(3L)
                .build();

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 10, 1, 10, 0))
                .end(LocalDateTime.of(2024, 10, 2, 10, 0))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .item(item)
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.item");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());

        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(booker.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(booker.getName());
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo(booker.getEmail());

        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(item.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo(item.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(item.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(item.getRequestId().intValue());
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonString = "{ " +
                "\"id\": 1, " +
                "\"start\": \"2025-07-04T14:00:00\", " +
                "\"end\": \"2025-07-05T11:00:00\", " +
                "\"status\": \"APPROVED\", " +
                "\"booker\": { \"id\": 1, \"name\": \"Vasya\", \"email\": \"vasyapupkin@yandex.ru\" }, " +
                "\"item\": { \"id\": 2, \"name\": \"TestItem\", \"description\": \"TestDescription\", \"available\": true, \"requestId\": 3 } " +
                "}";

        BookingDto bookingDto = json.parse(jsonString).getObject();

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getId()).isEqualTo(1L);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.of(2025, 7, 4, 14, 0));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.of(2025, 7, 5, 11, 0));
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);

        assertThat(bookingDto.getBooker()).isNotNull();
        assertThat(bookingDto.getBooker().getId()).isEqualTo(1L);
        assertThat(bookingDto.getBooker().getName()).isEqualTo("Vasya");
        assertThat(bookingDto.getBooker().getEmail()).isEqualTo("vasyapupkin@yandex.ru");

        assertThat(bookingDto.getItem()).isNotNull();
        assertThat(bookingDto.getItem().getId()).isEqualTo(2L);
        assertThat(bookingDto.getItem().getName()).isEqualTo("TestItem");
        assertThat(bookingDto.getItem().getDescription()).isEqualTo("TestDescription");
        assertThat(bookingDto.getItem().getAvailable()).isTrue();
        assertThat(bookingDto.getItem().getRequestId()).isEqualTo(3L);
    }
}
