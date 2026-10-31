package com.canteenq.pattern;

import com.canteenq.model.MenuItem;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class NoticeBoard implements MenuListener {

    private static final int MAX_NOTICES = 10;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("hh:mm a");

    private final LinkedList<String> notices = new LinkedList<>();

    @Override
    public void onNewItem(MenuItem item) {
        post("New on the menu: " + item.getName() + " at just Rs " + item.getPrice());
    }

    @Override
    public void onPriceChange(MenuItem item, double oldPrice) {
        String word = item.getPrice() < oldPrice ? "dropped" : "changed";
        post("Price of " + item.getName() + " " + word + " from Rs " + oldPrice + " to Rs " + item.getPrice());
    }

    public synchronized void post(String text) {
        notices.addFirst(LocalTime.now().format(TIME) + " - " + text);
        if (notices.size() > MAX_NOTICES) {
            notices.removeLast();
        }
    }

    public synchronized List<String> all() {
        return new ArrayList<>(notices);
    }
}
