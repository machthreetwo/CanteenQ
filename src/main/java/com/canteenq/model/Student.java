package com.canteenq.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends Member {

    private String rollNumber;
    private String branch;

    protected Student() {
    }

    public Student(String name, String phone, double balance, String rollNumber, String branch) {
        super(name, phone, balance);
        this.rollNumber = rollNumber;
        this.branch = branch;
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public int getPriority() {
        return Thread.NORM_PRIORITY;
    }

    @Override
    public String getDetails() {
        return getName() + " | Roll No: " + rollNumber + " | " + branch;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public String getBranch() {
        return branch;
    }
}
