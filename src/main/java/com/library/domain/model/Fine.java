package com.library.domain.model;

public class Fine {
    private final User user;
    private double amount; // NIS
    public Fine(User user, double amount) { this.user = user; this.amount = amount; }
    public User getUser() { return user; }
    public double getAmount() { return amount; }
    public void pay(double value) { amount = Math.max(0, amount - value); }
    public boolean isCleared() { return amount <= 0.0001; }
}
