package com.canteenq.service;

import com.canteenq.dto.Receipt;
import com.canteenq.exception.CanteenException;
import com.canteenq.exception.NotFoundException;
import com.canteenq.model.FoodOrder;
import com.canteenq.model.OrderStatus;
import com.canteenq.repository.FoodOrderRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class KitchenService {

    private final Queue<Long> pending = new LinkedList<>();
    private FoodOrderRepository orders;

    @Autowired
    public void setOrders(FoodOrderRepository orders) {
        this.orders = orders;
    }

    @PostConstruct
    public void open() {
        System.out.println("[Kitchen] Stoves on, ready to take orders");
    }

    @PreDestroy
    public void close() {
        System.out.println("[Kitchen] Closing for the day with " + pending.size() + " order(s) still in queue");
    }

    public synchronized void enqueue(Long orderId) {
        pending.offer(orderId);
    }

    public synchronized List<FoodOrder> preparing() {
        return orders.findAllById(pending).stream()
                .sorted((a, b) -> Integer.compare(a.getToken(), b.getToken()))
                .toList();
    }

    public List<FoodOrder> ready() {
        return orders.findByStatusOrderByToken(OrderStatus.READY);
    }

    @Transactional
    public synchronized Receipt serveNext() {
        Long id = pending.poll();
        if (id == null) {
            throw new CanteenException("No orders waiting in the kitchen right now");
        }
        FoodOrder order = orders.findById(id).orElseThrow(() -> new NotFoundException("Order", id));
        order.setStatus(OrderStatus.READY);
        return Receipt.from(order);
    }
}
