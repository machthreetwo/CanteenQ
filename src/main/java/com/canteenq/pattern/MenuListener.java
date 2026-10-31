package com.canteenq.pattern;

import com.canteenq.model.MenuItem;

public interface MenuListener {

    void onNewItem(MenuItem item);

    default void onPriceChange(MenuItem item, double oldPrice) {
    }
}
