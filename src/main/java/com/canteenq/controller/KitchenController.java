package com.canteenq.controller;

import com.canteenq.dto.Receipt;
import com.canteenq.dto.RushReport;
import com.canteenq.service.KitchenService;
import com.canteenq.service.RushHourSimulator;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kitchen")
public class KitchenController {

    private final KitchenService kitchen;
    private final RushHourSimulator rush;

    public KitchenController(KitchenService kitchen, RushHourSimulator rush) {
        this.kitchen = kitchen;
        this.rush = rush;
    }

    @GetMapping("/queue")
    public List<Receipt> queue() {
        return kitchen.preparing().stream().map(Receipt::from).toList();
    }

    @PostMapping("/serve")
    public Receipt serve() {
        return kitchen.serveNext();
    }

    @PostMapping("/rush")
    public RushReport rush(@RequestParam(defaultValue = "Samosa") String dish,
                           @RequestParam(defaultValue = "3") int plates) {
        return rush.run(dish, plates);
    }
}
