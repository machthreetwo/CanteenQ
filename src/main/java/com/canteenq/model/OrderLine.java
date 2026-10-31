package com.canteenq.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_lines")
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id")
    private MenuItem item;

    private int quantity;
    private double amount;

    protected OrderLine() {
    }

    public OrderLine(MenuItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.amount = item.getPrice() * quantity;
    }

    public Long getId() {
        return id;
    }

    public MenuItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getAmount() {
        return amount;
    }
}
