package ru.practicum.shareit.item.repository;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.baseClass.BaseInMemoryRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ItemRepositoryInMemoryImpl extends BaseInMemoryRepository<Item> implements ItemRepository {

    @Override
    public Item save(Item item) {
        item.setId(generateId());
        storage.put(item.getId(), item);
        return storage.get(item.getId());
    }

    @Override
    public Item update(Item item) {
        Long itemId = item.getId();
        storage.put(itemId, item);
        return storage.get(itemId);
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(storage.get(itemId));
    }

    @Override
    public List<Item> findAllForOwner(Long userId) {
        return storage.values().stream().filter(item -> item.getOwnerId().equals(userId)).toList();
    }

    @Override
    public List<Item> findItemsBySearch(String text) {
        String searchText = text.toLowerCase(Locale.ROOT);
        return storage.values().stream()
                .filter(item -> (item.getName().toLowerCase(Locale.ROOT).contains(searchText)
                        || item.getDescription().toLowerCase(Locale.ROOT).contains(searchText)))
                .filter(Item::getAvailable)
                .toList();
    }
}
