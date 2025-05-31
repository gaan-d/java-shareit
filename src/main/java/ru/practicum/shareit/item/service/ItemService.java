package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public interface ItemService {
    ItemDto create(Long userId, NewItemDto itemDto);

    ItemDto update(Long userId, Long itemId, UpdateItemDto itemDto);

    ItemDto findById(Long itemId);

    List<ItemDto> findItemsByOwner(Long userId);

    List<ItemDto> findItemsByText(String text);

    Item validateItem(Long itemId);
}
