package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public interface ItemService {

    ItemDto create(Long userId, NewItemDto itemDto);

    ItemDto update(Long userId, Long itemId, UpdateItemDto itemDto);

    ItemDto findById(Long userId, Long itemId);

    List<ItemDto> findItemsByOwner(Long userId);

    List<ItemDto> findItemsByText(Long userId, String text);

    Item validateItem(Long itemId);

    CommentDto addComment(Long userId, Long itemId, NewCommentDto commentDto);
}
