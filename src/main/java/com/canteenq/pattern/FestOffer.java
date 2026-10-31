package com.canteenq.pattern;

import com.canteenq.model.Member;
import org.springframework.stereotype.Component;

@Component("FEST20")
public class FestOffer implements OfferPolicy {

    @Override
    public double discountFor(Member member, double subtotal) {
        if (subtotal < 200) {
            return 0;
        }
        return Math.min(subtotal * 0.20, 80);
    }

    @Override
    public String label() {
        return "Fest week: 20% off on Rs 200+ (max Rs 80)";
    }
}
