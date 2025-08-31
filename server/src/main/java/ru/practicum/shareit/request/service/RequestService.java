package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto create(Long userId, NewRequestDto request);

    List<RequestDto> GetAllRequestsById(Long userId);

    List<RequestDto> findAll(Long userId, Integer from, Integer size);

    RequestDto findById(Long userId, Long requestId);
}
