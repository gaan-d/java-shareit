package ru.practicum.shareit.request;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.time.LocalDateTime;

@NoArgsConstructor
public class RequestMapper {
    public static Request mapToRequest(NewRequestDto newRequestDto) {
        return Request.builder()
                .description(newRequestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }

    public static RequestDto mapToRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requesterName(request.getRequester().getName())
                .created(request.getCreated())
                .build();
    }
}
