package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

import static ru.practicum.shareit.item.mapper.ItemMapper.mapToItemDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.updateItem;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserService userService;

    @Override
    public ItemDto create(Long userId, NewItemDto itemDto) {
        userService.validateExistenceById(userId);
        Item item = ItemMapper.mapToNewItem(itemDto);
        item.setOwnerId(userId);
        return mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemDto itemDto) {
        userService.validateExistenceById(userId);
        Item item = validateItem(itemId);
        if (!item.getOwnerId().equals(userId)) {
            throw new ValidationException(String.format("Пользователь с id=%d не является владельцем предмета с id=%d", userId, itemId));
        }
        updateItem(item, itemDto);
        return mapToItemDto(item);
    }

    @Override
    public ItemDto findById(Long itemId) {
        return mapToItemDto(validateItem(itemId));
    }

    @Override
    public List<ItemDto> findItemsByOwner(Long userId) {
        userService.validateExistenceById(userId);

        return itemRepository.findAllForOwner(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> findItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.findItemsBySearch(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public Item validateItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет с id=%d не найден", itemId)));
    }
}
