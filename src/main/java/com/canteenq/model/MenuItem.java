package com.canteenq.model;

import com.canteenq.exception.OutOfStockException;
import jakarta.persistence.*;

@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq")
    @SequenceGenerator(name = "item_seq", sequenceName = "item_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private Category category;

    private double price;
    private int stock;

    public MenuItem() {
    }

    public MenuItem(String name, Category category, double price, int stock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public void takeFromStock(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity of " + name + " should be at least 1");
        }
        if (qty > stock) {
            throw new OutOfStockException(name, stock, qty);
        }
        stock -= qty;
    }

    public boolean isAvailable() {
        return stock > 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return name + " (Rs " + price + ")";
    }
}
