package com.canteenq.dto;

import com.canteenq.model.FoodOrder;

import java.time.LocalDateTime;
import java.util.List;

public record Receipt(Long orderId, int token, String member, List<String> items, double subtotal,
                      double discount, double total, String offer, String status, LocalDateTime placedAt) {

    public static Receipt from(FoodOrder order) {
        List<String> items = order.getLines().stream()
                .map(line -> line.getQuantity() + " x " + line.getItem().getName())
                .toList();
        return new Receipt(order.getId(), order.getToken(), order.getMember().getName(), items,
                order.getSubtotal(), order.getDiscount(), order.getTotal(), order.getOffer(),
                order.getStatus().name(), order.getPlacedAt());
    }
}
