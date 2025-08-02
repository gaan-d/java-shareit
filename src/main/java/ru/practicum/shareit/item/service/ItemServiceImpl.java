package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
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
    BookingRepository bookingRepository;
    CommentRepository commentRepository;

    @Override
    public ItemDto create(Long userId, NewItemDto itemDto) {
        User owner = userService.validateExistenceById(userId);
        Item item = ItemMapper.mapToNewItem(itemDto);
        item.setOwner(owner);
        return mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemDto itemDto) {
        userService.validateExistenceById(userId);
        Item item = validateItem(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException(String.format("Пользователь с id=%d не является владельцем предмета с id=%d", userId, itemId));
        }
        updateItem(item, itemDto);
        item = itemRepository.save(item);

        return mapToItemDto(item);
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        Item item = validateItem(itemId);
        userService.validateExistenceById(userId);

        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        loadItemDetails(itemDto);

        return itemDto;
    }

    @Override
    public List<ItemDto> findItemsByOwner(Long userId) {
        userService.validateExistenceById(userId);
        List<ItemDto> ownedItemDtos = itemRepository.findAllByOwnerIdOrderByIdAsc(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .map(itemDto -> {
                    loadItemDetails(itemDto);
                    return itemDto;
                })
                .toList();

        return ownedItemDtos;
    }

    @Override
    public List<ItemDto> findItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.findItemsByNameOrDescription(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public Item validateItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Предмет с id=%d не найден", itemId)));
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, NewCommentDto commentDto) {
        User author = userService.validateExistenceById(userId);
        Item item = validateItem(itemId);

        validateCommentEligibility(userId, itemId);
        Comment comment = CommentMapper.mapToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());

        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    private void validateCommentEligibility(Long userId, Long itemId) {
        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        if (booking == null) {
            throw new ValidationException(String.format("Пользователь с id=%d не может оставить комментарий к предмету с id=%d", userId, itemId));
        }
    }

    @Transactional
    private void loadItemDetails(ItemDto itemDto) {

        List<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
        itemDto.setComments(comments.stream().map(CommentMapper::mapToCommentDto).toList());

        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerId(itemDto.getId(), Sort.by(Sort.Direction.DESC, "start"));

        if (!bookings.isEmpty()) {
            Booking onlyBooking = bookings.getFirst();
            LocalDateTime now = LocalDateTime.now();

            if (onlyBooking.getStart().isAfter(now)) {
                itemDto.setNextBooking(BookingMapper.mapToBookingDto(onlyBooking));
                itemDto.setLastBooking(null);
            } else {
                itemDto.setLastBooking(BookingMapper.mapToBookingDto(onlyBooking));
                itemDto.setNextBooking(null);
            }
        } else {
            itemDto.setNextBooking(null);
            itemDto.setLastBooking(null);
        }
    }
}
