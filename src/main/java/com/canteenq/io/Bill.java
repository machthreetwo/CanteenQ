package com.canteenq.io;

import com.canteenq.model.FoodOrder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Bill implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private int token;
    private String customer;
    private ArrayList<String> items;
    private double total;
    private LocalDate date;
    private String copyType = "ORIGINAL";
    private transient String printedAt;

    private Bill() {
    }

    public static Bill of(FoodOrder order) {
        Bill bill = new Bill();
        bill.orderId = order.getId();
        bill.token = order.getToken();
        bill.customer = order.getMember().getName();
        bill.items = new ArrayList<>();
        order.getLines().forEach(l -> bill.items.add(l.getQuantity() + " x " + l.getItem().getName()));
        bill.total = order.getTotal();
        bill.date = order.getOrderDate();
        bill.printedAt = "counter-1";
        return bill;
    }

    public Bill duplicate() {
        try {
            Bill copy = (Bill) super.clone();
            copy.items = new ArrayList<>(items);
            copy.copyType = "DUPLICATE";
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Could not reprint bill " + orderId);
        }
    }

    public Long getOrderId() {
        return orderId;
    }

    public int getToken() {
        return token;
    }

    public String getCustomer() {
        return customer;
    }

    public List<String> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCopyType() {
        return copyType;
    }

    public String getPrintedAt() {
        return printedAt;
    }

    @Override
    public String toString() {
        return String.format("#%d | %s | Token %d | %-15s | %s | Rs %.2f",
                orderId, date, token, customer, String.join(", ", items), total);
    }
}
