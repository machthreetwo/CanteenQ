package com.canteenq.pattern;

import java.time.LocalDate;

public final class TokenMachine {

    private static TokenMachine instance;

    private int lastToken;
    private LocalDate today = LocalDate.now();

    private TokenMachine() {
    }

    public static synchronized TokenMachine getInstance() {
        if (instance == null) {
            instance = new TokenMachine();
        }
        return instance;
    }

    public synchronized int next() {
        LocalDate now = LocalDate.now();
        if (!now.equals(today)) {
            today = now;
            lastToken = 0;
        }
        return ++lastToken;
    }

    public synchronized int current() {
        return lastToken;
    }
}
