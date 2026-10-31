package com.canteenq.controller;

import com.canteenq.model.Category;
import com.canteenq.model.MenuItem;
import com.canteenq.pattern.NoticeBoard;
import com.canteenq.service.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;
    private final NoticeBoard noticeBoard;

    public MenuController(MenuService menuService, NoticeBoard noticeBoard) {
        this.menuService = menuService;
        this.noticeBoard = noticeBoard;
    }

    @GetMapping
    public List<MenuItem> list(@RequestParam(required = false) Category category) {
        return category == null ? menuService.all() : menuService.byCategory(category);
    }

    @GetMapping("/{id}")
    public MenuItem get(@PathVariable Long id) {
        return menuService.find(id);
    }

    @GetMapping("/search")
    public MenuItem search(@RequestParam String name) {
        return menuService.find(name);
    }

    @PostMapping
    public ResponseEntity<MenuItem> add(@RequestBody MenuItem item) {
        MenuItem saved = menuService.add(item);
        return ResponseEntity.created(URI.create("/api/menu/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public MenuItem update(@PathVariable Long id, @RequestBody MenuItem item) {
        return menuService.update(id, item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notices")
    public List<String> notices() {
        return noticeBoard.all();
    }
}
