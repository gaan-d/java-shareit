package ru.practicum.shareit.baseClass;

import java.util.HashMap;
import java.util.Map;

public class BaseInMemoryRepository<T> {

    protected final Map<Long, T> storage = new HashMap<>();

    protected Long generateId() {
        return storage.isEmpty() ? 1L : storage.keySet().stream().max(Long::compareTo).orElse(0L) + 1;
    }
}
