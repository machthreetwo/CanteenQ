package com.canteenq.pattern;

import com.canteenq.model.Member;

public interface OfferPolicy {

    double discountFor(Member member, double subtotal);

    String label();
}
