package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestService requestService;

    @Autowired
    MockMvc mvc;

    final NewRequestDto newRequestDto = NewRequestDto.builder().description("Test description").build();

    final ItemDto itemDto1 = ItemDto.builder().id(1L).name("Test item").available(true)
            .description("Test description").build();

    final ItemDto itemDto2 = ItemDto.builder().id(2L).name("Test item 2").available(true)
            .description("Test description 2").build();

    final RequestDto requestDto = RequestDto.builder().id(1L).description("Test description")
            .created(LocalDateTime.of(2025, 7, 4, 12, 44, 0))
            .requesterName("Requester name").items(List.of(itemDto1, itemDto2)).build();

    final RequestDto requestDto2 = RequestDto.builder().id(2L).description("Test description2")
            .created(LocalDateTime.of(2025, 7, 4, 12, 44, 5))
            .requesterName("Requester name 2").items(List.of(itemDto1)).build();

    final Long userId = 3L;
    final Long requestId = 1L;

    @Test
    void createRequest() throws Exception {
        when(requestService.create(userId, newRequestDto))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(newRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.requesterName").value(requestDto.getRequesterName()))
                .andExpect(jsonPath("$.created").value("2025-07-04T12:44:00"))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id").value(itemDto1.getId()))
                .andExpect(jsonPath("$.items[1].id").value(itemDto2.getId()));
    }

    @Test
    void getAllRequestsById() throws Exception {
        when(requestService.GetAllRequestsById(userId))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$[0].requesterName").value(requestDto.getRequesterName()))
                .andExpect(jsonPath("$[0].created").value("2025-07-04T12:44:00"))
                .andExpect(jsonPath("$[0].items", hasSize(2)))
                .andExpect(jsonPath("$[0].items[0].id").value(itemDto1.getId()))
                .andExpect(jsonPath("$[0].items[1].id").value(itemDto2.getId()));
    }

    @Test
    void getAllRequests() throws Exception {
        when(requestService.findAll(userId, 0, 50))
                .thenReturn(List.of(requestDto, requestDto2));
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$[0].requesterName").value(requestDto.getRequesterName()))
                .andExpect(jsonPath("$[0].created").value("2025-07-04T12:44:00"))
                .andExpect(jsonPath("$[0].items", hasSize(2)))
                .andExpect(jsonPath("$[0].items[0].id").value(itemDto1.getId()))
                .andExpect(jsonPath("$[0].items[1].id").value(itemDto2.getId()))
                .andExpect(jsonPath("$[1].id").value(requestDto2.getId()))
                .andExpect(jsonPath("$[1].description").value(requestDto2.getDescription()))
                .andExpect(jsonPath("$[1].requesterName").value(requestDto2.getRequesterName()))
                .andExpect(jsonPath("$[1].created").value("2025-07-04T12:44:05"))
                .andExpect(jsonPath("$[1].items", hasSize(1)))
                .andExpect(jsonPath("$[1].items[0].id").value(itemDto1.getId()));
    }

    @Test
    void getRequestById() throws Exception {
        when(requestService.findById(userId, requestId))
                .thenReturn(requestDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.requesterName").value(requestDto.getRequesterName()))
                .andExpect(jsonPath("$.created").value("2025-07-04T12:44:00"))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id").value(itemDto1.getId()))
                .andExpect(jsonPath("$.items[1].id").value(itemDto2.getId()));
    }
}
