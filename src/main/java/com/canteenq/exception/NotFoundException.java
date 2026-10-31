package com.canteenq.exception;

public class NotFoundException extends CanteenException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String what, Long id) {
        super(what + " with id " + id + " does not exist");
    }
}
