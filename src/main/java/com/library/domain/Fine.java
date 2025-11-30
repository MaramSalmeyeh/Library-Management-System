package com.library.domain;

public class Fine {

    private final String id;
    private final String userId;
    private double amount;
    private boolean paid;

    public Fine(String id, String userId, double amount, boolean paid) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.paid = paid;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
