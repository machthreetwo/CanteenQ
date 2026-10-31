package com.canteenq.pattern;

import com.canteenq.model.Member;
import org.springframework.stereotype.Component;

@Component("NONE")
public class NoOffer implements OfferPolicy {

    @Override
    public double discountFor(Member member, double subtotal) {
        return 0;
    }

    @Override
    public String label() {
        return "No offer";
    }
}
