package ru.practicum.shareit.user.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.baseClass.BaseInMemoryRepository;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRepositoryInMemoryImpl extends BaseInMemoryRepository<User> implements UserRepository {

    @Override
    public List<User> findAll() {
        return storage.values().stream().toList();
    }

    @Override
    public User save(User user) {
        long userId = generateId();
        user.setId(userId);
        storage.put(userId, user);
        return storage.get(userId);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(storage.get(userId));
    }

    @Override
    public void deleteById(Long userId) {
        storage.remove(userId);
    }

    @Override
    public User update(User user) {
        Long userId = user.getId();
        storage.put(userId, user);
        return storage.get(userId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        for (User user : storage.values()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
