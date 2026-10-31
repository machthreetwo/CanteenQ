package com.canteenq.exception;

public class InvalidOfferException extends CanteenException {

    public InvalidOfferException(String code) {
        super("Offer code '" + code + "' is not valid today");
    }
}
