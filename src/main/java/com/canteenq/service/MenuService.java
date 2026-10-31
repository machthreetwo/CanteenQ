package com.canteenq.service;

import com.canteenq.exception.NotFoundException;
import com.canteenq.model.Category;
import com.canteenq.model.MenuItem;
import com.canteenq.pattern.MenuListener;
import com.canteenq.repository.MenuItemRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuService {

    private final MenuItemRepository repo;
    private final List<MenuListener> listeners;

    public MenuService(MenuItemRepository repo, List<MenuListener> listeners) {
        this.repo = repo;
        this.listeners = listeners;
    }

    public List<MenuItem> all() {
        return repo.findAll(Sort.by("category", "name"));
    }

    public List<MenuItem> byCategory(Category category) {
        return repo.findByCategoryOrderByPrice(category);
    }

    public MenuItem find(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Menu item", id));
    }

    public MenuItem find(String name) {
        return repo.findByNameIgnoreCase(name.trim())
                .orElseThrow(() -> new NotFoundException("Nothing called '" + name + "' on the menu"));
    }

    @Transactional
    public MenuItem add(MenuItem item) {
        check(item);
        item.setId(null);
        MenuItem saved = repo.save(item);
        listeners.forEach(listener -> listener.onNewItem(saved));
        return saved;
    }

    @Transactional
    public MenuItem update(Long id, MenuItem changes) {
        check(changes);
        MenuItem item = find(id);
        double oldPrice = item.getPrice();
        item.setName(changes.getName());
        item.setCategory(changes.getCategory());
        item.setPrice(changes.getPrice());
        item.setStock(changes.getStock());
        if (oldPrice != item.getPrice()) {
            listeners.forEach(listener -> listener.onPriceChange(item, oldPrice));
        }
        return item;
    }

    @Transactional
    public void remove(Long id) {
        repo.delete(find(id));
        repo.flush();
    }

    private void check(MenuItem item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new IllegalArgumentException("Dish name is required");
        }
        if (item.getCategory() == null) {
            throw new IllegalArgumentException("Category is required");
        }
        if (item.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be more than zero");
        }
        if (item.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
    }
}
