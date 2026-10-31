package com.canteenq.pattern;

import com.canteenq.model.Member;
import com.canteenq.model.Staff;
import org.springframework.stereotype.Component;

@Component("STAFFMEAL")
public class StaffMealOffer implements OfferPolicy {

    @Override
    public double discountFor(Member member, double subtotal) {
        if (member instanceof Staff && subtotal >= 100) {
            return 15;
        }
        return 0;
    }

    @Override
    public String label() {
        return "Flat Rs 15 off for staff on Rs 100+";
    }
}
