package com.canteenq.exception;

public class InsufficientBalanceException extends CanteenException {

    public InsufficientBalanceException(String name, double balance, double bill) {
        super(String.format("%s has only Rs %.2f in wallet but the bill is Rs %.2f", name, balance, bill));
    }
}
