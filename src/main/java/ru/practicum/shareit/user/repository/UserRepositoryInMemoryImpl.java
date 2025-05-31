package ru.practicum.shareit.user.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRepositoryInMemoryImpl implements UserRepository {
    Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public User save(User user) {
        long userId = getNextId();
        user.setId(userId);
        users.put(userId, user);
        return users.get(userId);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void deleteById(Long userId) {
        users.remove(userId);
    }

    @Override
    public User update(User user) {
        Long userId = user.getId();
        users.put(userId, user);
        return users.get(userId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    Long getNextId() {
        long nextId = users.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L);
        return nextId + 1;
    }
}
