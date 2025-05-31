package ru.practicum.shareit.item.repository;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ItemRepositoryInMemoryImpl implements ItemRepository {
    Map<Long, Item> items = new HashMap<>();

    @Override
    public Item save(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item update(Item item) {
        Long itemId = item.getId();
        items.put(itemId, item);
        return items.get(itemId);
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> findAllForOwner(Long userId) {
        return items.values().stream().filter(item -> item.getOwnerId().equals(userId)).toList();
    }

    @Override
    public List<Item> findItemsBySearch(String text) {
        String searchText = text.toLowerCase(Locale.ROOT);
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase(Locale.ROOT).contains(searchText)
                        || item.getDescription().toLowerCase(Locale.ROOT).contains(searchText)))
                .filter(Item::getAvailable)
                .toList();
    }

    Long generateId() {
        long nextId = items.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L);
        return nextId + 1;
    }
}
