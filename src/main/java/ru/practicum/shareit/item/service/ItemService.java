package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Transactional(readOnly = true)
@Service
public interface ItemService {
    @Transactional
    ItemDto create(Long userId, NewItemDto itemDto);

    @Transactional
    ItemDto update(Long userId, Long itemId, UpdateItemDto itemDto);

    ItemDto findById(Long userId, Long itemId);

    List<ItemDto> findItemsByOwner(Long userId);

    List<ItemDto> findItemsByText(String text);

    Item validateItem(Long itemId);

    @Transactional
    CommentDto addComment(Long userId, Long itemId, NewCommentDto commentDto);
}
