package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual(Long bookerId,
                                                                               LocalDateTime start,
                                                                               LocalDateTime end,
                                                                               Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndStatusIn(Long ownerId, List<BookingStatus> statuses, Sort sort);

    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqual(Long ownerId,
                                                                                  LocalDateTime start,
                                                                                  LocalDateTime end,
                                                                                  Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemId(Long itemId, Sort sort);

    Booking findFirstByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime end);
}
