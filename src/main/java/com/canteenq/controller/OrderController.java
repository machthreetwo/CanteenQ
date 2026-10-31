package com.canteenq.controller;

import com.canteenq.dto.OrderRequest;
import com.canteenq.dto.Receipt;
import com.canteenq.service.OfferService;
import com.canteenq.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final OfferService offerService;

    public OrderController(OrderService orderService, OfferService offerService) {
        this.orderService = orderService;
        this.offerService = offerService;
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Receipt place(@RequestBody OrderRequest request) {
        return orderService.place(request);
    }

    @GetMapping("/orders/{id}")
    public Receipt get(@PathVariable Long id) {
        return orderService.find(id);
    }

    @PutMapping("/orders/{id}/collect")
    public Receipt collect(@PathVariable Long id) {
        return orderService.collect(id);
    }

    @GetMapping("/offers")
    public Map<String, String> offers() {
        return offerService.available();
    }
}
