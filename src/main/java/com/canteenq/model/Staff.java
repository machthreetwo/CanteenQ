package com.canteenq.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("STAFF")
public class Staff extends Member {

    private String department;

    protected Staff() {
    }

    public Staff(String name, String phone, double balance, String department) {
        super(name, phone, balance);
        this.department = department;
    }

    @Override
    public String getRole() {
        return "Staff";
    }

    @Override
    public int getPriority() {
        return Thread.MAX_PRIORITY;
    }

    @Override
    public String getDetails() {
        return getName() + " | Staff, " + department + " Dept.";
    }

    public String getDepartment() {
        return department;
    }
}
