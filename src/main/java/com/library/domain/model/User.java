package com.library.domain.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String name;
    private String email;
    private double fineBalance;
    private List<Loan> activeLoans;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.fineBalance = 0.0;
        this.activeLoans = new ArrayList<>();
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public double getFineBalance() { return fineBalance; }
    public List<Loan> getActiveLoans() { return activeLoans; }

    // Setters
    public void setFineBalance(double fineBalance) {
        this.fineBalance = fineBalance;
    }

    public void addLoan(Loan loan) {
        activeLoans.add(loan);
    }

    public void removeLoan(Loan loan) {
        activeLoans.remove(loan);
    }

    /**
     * Check if user has any overdue items
     */
    public boolean hasOverdueItems() {
        return activeLoans.stream().anyMatch(Loan::isOverdue);
    }

    /**
     * Check if user can borrow new items (US4.1)
     */
    public boolean canBorrow() {
        return fineBalance == 0 && !hasOverdueItems();
    }

    /**
     * Pay fine amount (US2.3)
     */
    public double payFine(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        this.fineBalance = Math.max(0, this.fineBalance - amount);
        return this.fineBalance;
    }

    /**
     * Add fine to user's balance
     */
    public void addFine(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Fine amount cannot be negative");
        }
        this.fineBalance += amount;
    }

    /**
     * Get total fines from active loans
     */
    public double calculateTotalFines() {
        return fineBalance + activeLoans.stream()
                .mapToDouble(Loan::calculateFine)
                .sum();
    }
}