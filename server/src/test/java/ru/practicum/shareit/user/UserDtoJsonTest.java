package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoJsonTest {
    private final JacksonTester<UserDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Vasya");
        dto.setEmail("VasyaPupkin@mail.ru");

        JsonContent<UserDto> result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(dto.getEmail());
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonString = "{ \"id\": 1, \"name\": \"Vasya\", \"email\": \"VasyaPupkin@mail.ru\" }";

        UserDto dto = json.parse(jsonString).getObject();

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Vasya");
        assertThat(dto.getEmail()).isEqualTo("VasyaPupkin@mail.ru");
    }
}
