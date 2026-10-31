package com.canteenq.pattern;

import com.canteenq.model.MenuItem;
import org.springframework.stereotype.Component;

@Component
public class KitchenDisplay implements MenuListener {

    @Override
    public void onNewItem(MenuItem item) {
        System.out.println("[Kitchen] New dish to prepare from today: " + item.getName()
                + " (" + item.getStock() + " plates)");
    }
}
