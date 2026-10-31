package com.canteenq.dto;

public record MemberRequest(String type, String name, String phone, double balance,
                            String rollNumber, String branch, String department) {
}
