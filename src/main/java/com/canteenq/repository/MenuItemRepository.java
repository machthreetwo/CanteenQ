package com.canteenq.repository;

import com.canteenq.model.Category;
import com.canteenq.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByCategoryOrderByPrice(Category category);

    Optional<MenuItem> findByNameIgnoreCase(String name);
}
