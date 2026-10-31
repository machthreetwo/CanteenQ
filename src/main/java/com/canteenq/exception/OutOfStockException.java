package com.canteenq.exception;

public class OutOfStockException extends CanteenException {

    public OutOfStockException(String item, int left, int asked) {
        super("Only " + left + " plate(s) of " + item + " left, you asked for " + asked);
    }
}
