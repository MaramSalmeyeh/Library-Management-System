package com.library.domain.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a book loan transaction
 * @author Your Name
 * @version 1.0
 */
public class Loan {
    private String id;
    private User user;
    private Book book;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(String id, User user, Book book, int loanPeriodDays) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.borrowDate = LocalDate.now();
        this.dueDate = this.borrowDate.plusDays(loanPeriodDays);
        this.returnDate = null;
    }

    // Getters
    public String getId() { return id; }
    public User getUser() { return user; }
    public Book getBook() { return book; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Check if loan is overdue (US2.2)
     */
    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && returnDate == null;
    }

    /**
     * Calculate number of overdue days (US2.2)
     */
    public int getOverdueDays() {
        if (!isOverdue()) return 0;
        return (int) ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    /**
     * Calculate fine amount (10 NIS per day for books) - US2.2
     */
    public double calculateFine() {
        if (!isOverdue()) return 0;
        int overdueDays = getOverdueDays();
        return overdueDays * 10.0; // 10 NIS per day for books
    }

    /**
     * Check if loan is active (not returned)
     */
    public boolean isActive() {
        return returnDate == null;
    }

    @Override
    public String toString() {
        return book.getTitle() + " - Borrowed: " + borrowDate +
                ", Due: " + dueDate + " - " + (isOverdue() ? "OVERDUE" : "Active");
    }
}