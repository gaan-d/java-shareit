package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    BookingRepository bookingRepository;
    UserService userService;
    ItemRepository itemRepository;

    static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public BookingDto create(Long userId, NewBookingDto bookingDto) {
        validateDate(bookingDto);
        User user = userService.validateExistenceById(userId);
        Item item = validateItemExistence(bookingDto.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с id = " + item.getId() + " недоступна для бронирования");
        }
        Booking booking = BookingMapper.mapToNewBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto updateStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = validateBookingExistence(bookingId);
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Пользователь с id = " + userId + " не является владельцем вещи с id = " + item.getId());
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(updatedBooking);
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        return BookingMapper.mapToBookingDto(validateBooking(userId, bookingId));
    }

    @Override
    public List<BookingDto> findAllByBookerId(Long userId, BookingState state) {
        userService.validateExistenceById(userId);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookings = bookingRepository.findAllByBookerId(userId, SORT_BY_START_DESC);
            case CURRENT -> bookings = bookingRepository.findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual(
                    userId, now, now, SORT_BY_START_DESC);
            case PAST -> bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, now, SORT_BY_START_DESC);
            case FUTURE -> bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, now, SORT_BY_START_DESC);
            case REJECTED -> bookings = bookingRepository.findAllByBookerIdAndStatusIn(userId,
                    List.of(BookingStatus.REJECTED, BookingStatus.CANCELED), SORT_BY_START_DESC);
            case WAITING ->
                    bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, SORT_BY_START_DESC);
            default -> new ArrayList<>();
        };
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> findAllByOwnerId(Long userId, BookingState state) {
        userService.validateExistenceById(userId);

        List<Item> userItems = itemRepository.findAllByOwnerIdOrderByIdAsc(userId);

        if (userItems.isEmpty()) {
            throw new ValidationException("У пользователя нет ни одной вещи");
        }
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookings = bookingRepository.findAllByItemOwnerId(userId, SORT_BY_START_DESC);
            case WAITING -> bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING,
                    SORT_BY_START_DESC);
            case REJECTED -> bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED,
                    SORT_BY_START_DESC);
            case PAST ->  bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, now, SORT_BY_START_DESC);
            case CURRENT -> bookings = bookingRepository.findAllByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqual(userId,
                    now, now, SORT_BY_START_DESC);
            case FUTURE -> bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, now, SORT_BY_START_DESC);
            default -> new ArrayList<>();
        };

        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    private Booking validateBookingExistence(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с id = " + bookingId + " не найдена"));
    }

    private Booking validateBooking(Long userId, Long bookingId) {
        User user = userService.validateExistenceById(userId);
        Booking booking = validateBookingExistence(bookingId);
        Item item = booking.getItem();

        if (!booking.getBooker().equals(user) && !item.getOwner().equals(user)) {
            throw new AccessDeniedException("Пользователь с id = " + userId + " не является владельцем брони" + bookingId);
        }
        return booking;
    }

    private void validateDate(NewBookingDto bookingDto) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("Неверные даты бронирования");
        }
    }

    private Item validateItemExistence(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));
    }
}
