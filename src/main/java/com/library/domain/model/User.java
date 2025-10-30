package com.library.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a library user.
 * @author Your Name
 * @version 1.0
 */
public class User {
    private final String id;
    private final String name;
    private final String email;
    private double fineBalance;
    private final List<Loan> activeLoans = new ArrayList<>();

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.fineBalance = 0.0;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public double getFineBalance() { return fineBalance; }
    public List<Loan> getActiveLoans() { return new ArrayList<>(activeLoans); }

    // Setters
    public void setFineBalance(double fineBalance) {
        this.fineBalance = fineBalance;
    }

    /**
     * Add a loan to user's active loans
     * @param loan the loan to add
     */
    public void addLoan(Loan loan) {
        activeLoans.add(loan);
    }

    /**
     * Remove a loan from user's active loans
     * @param loan the loan to remove
     */
    public void removeLoan(Loan loan) {
        activeLoans.remove(loan);
    }

    /**
     * Check if user has any overdue items
     * @return true if user has overdue items
     */
    public boolean hasOverdueItems() {
        return activeLoans.stream().anyMatch(Loan::isOverdue);
    }

    /**
     * Check if user can borrow new items
     * According to US4.1: Cannot borrow if has overdue books or unpaid fines
     * @return true if user can borrow
     */
    public boolean canBorrow() {
        return fineBalance == 0 && !hasOverdueItems();
    }

    /**
     * Check if user has unpaid fines
     * @return true if user has unpaid fines
     */
    public boolean hasUnpaidFines() {
        return fineBalance > 0;
    }

    /**
     * Pay fine amount
     * @param amount amount to pay
     * @return remaining fine balance
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
     * @param amount fine amount to add
     */
    public void addFine(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Fine amount cannot be negative");
        }
        this.fineBalance += amount;
    }

    /**
     * Check if user can be unregistered
     * According to US4.2: Users with active loans or unpaid fines cannot be unregistered
     * @return true if user can be unregistered
     */
    public boolean canBeUnregistered() {
        return activeLoans.isEmpty() && fineBalance == 0;
    }

    /**
     * Get count of active loans
     * @return number of active loans
     */
    public int getActiveLoansCount() {
        return activeLoans.size();
    }

    /**
     * Get count of overdue loans
     * @return number of overdue loans
     */
    public int getOverdueLoansCount() {
        return (int) activeLoans.stream()
                .filter(Loan::isOverdue)
                .count();
    }

    public String getBorrowedBooks() {
        return null;
    }
}