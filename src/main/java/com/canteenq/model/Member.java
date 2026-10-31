package com.canteenq.model;

import com.canteenq.exception.InsufficientBalanceException;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "members")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "member_type")
public abstract class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq")
    @SequenceGenerator(name = "member_seq", sequenceName = "member_seq", initialValue = 101, allocationSize = 1)
    private Long id;

    private String name;
    private String phone;
    private double balance;

    @ManyToMany
    @JoinTable(name = "member_favourites",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private Set<MenuItem> favourites = new HashSet<>();

    protected Member() {
    }

    protected Member(String name, String phone, double balance) {
        this.name = name;
        this.phone = phone;
        this.balance = balance;
    }

    public abstract String getRole();

    public abstract int getPriority();

    public abstract String getDetails();

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Recharge amount must be more than zero");
        }
        if (amount > 5000) {
            throw new IllegalArgumentException("Cannot recharge more than Rs 5000 at once");
        }
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount > balance) {
            throw new InsufficientBalanceException(name, balance, amount);
        }
        balance -= amount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public double getBalance() {
        return balance;
    }

    public Set<MenuItem> getFavourites() {
        return favourites;
    }
}
