package com.canteenq.pattern;

import com.canteenq.model.Member;
import com.canteenq.model.Student;
import org.springframework.stereotype.Component;

@Component("STUDENT10")
public class StudentOffer implements OfferPolicy {

    @Override
    public double discountFor(Member member, double subtotal) {
        if (member instanceof Student) {
            return subtotal * 0.10;
        }
        return 0;
    }

    @Override
    public String label() {
        return "10% off for students";
    }
}
