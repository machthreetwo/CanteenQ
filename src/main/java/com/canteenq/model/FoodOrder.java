package com.canteenq.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "food_orders")
public class FoodOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq", sequenceName = "order_seq", initialValue = 5001, allocationSize = 1)
    private Long id;

    private int token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderLine> lines = new ArrayList<>();

    private String offer;
    private double subtotal;
    private double discount;
    private double total;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PLACED;

    private LocalDate orderDate;
    private LocalDateTime placedAt;

    protected FoodOrder() {
    }

    public FoodOrder(Member member, String offer) {
        this.member = member;
        this.offer = offer;
        this.placedAt = LocalDateTime.now();
        this.orderDate = placedAt.toLocalDate();
    }

    public void addLine(OrderLine line) {
        lines.add(line);
        subtotal += line.getAmount();
        total = subtotal - discount;
    }

    public void applyDiscount(double discount) {
        this.discount = Math.round(Math.min(discount, subtotal) * 100) / 100.0;
        this.total = subtotal - this.discount;
    }

    public Long getId() {
        return id;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public Member getMember() {
        return member;
    }

    public List<OrderLine> getLines() {
        return lines;
    }

    public String getOffer() {
        return offer;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiscount() {
        return discount;
    }

    public double getTotal() {
        return total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(LocalDateTime placedAt) {
        this.placedAt = placedAt;
        this.orderDate = placedAt.toLocalDate();
    }
}
