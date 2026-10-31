package com.canteenq.pattern;

import com.canteenq.dto.MemberRequest;
import com.canteenq.model.Member;
import com.canteenq.model.Staff;
import com.canteenq.model.Student;

public final class MemberFactory {

    private MemberFactory() {
    }

    public static Member create(MemberRequest req) {
        if (req.name() == null || req.name().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (req.balance() < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative");
        }
        String type = req.type() == null ? "" : req.type().trim().toUpperCase();
        return switch (type) {
            case "STUDENT" -> new Student(req.name(), req.phone(), req.balance(), req.rollNumber(), req.branch());
            case "STAFF" -> new Staff(req.name(), req.phone(), req.balance(), req.department());
            default -> throw new IllegalArgumentException("Member type should be STUDENT or STAFF");
        };
    }
}
