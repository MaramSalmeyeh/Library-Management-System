
package com.library.domain.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class Loan {
    private String id;
    private User user;
    private Object item; // Can be Book or CD
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(String id, User user, Object item, int loanPeriodDays) {
        this.id = id;
        this.user = user;
        this.item = item;
        this.borrowDate = LocalDate.now();
        this.dueDate = this.borrowDate.plusDays(loanPeriodDays);
    }

    // Getters
    public String getId() { return id; }
    public User getUser() { return user; }
    public Object getItem() { return item; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Check if loan is overdue
     * @return true if current date is after due date
     */
    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && returnDate == null;
    }

    /**
     * Calculate overdue days
     * @return number of overdue days
     */
    public int getOverdueDays() {
        if (!isOverdue()) return 0;
        return (int) ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
}