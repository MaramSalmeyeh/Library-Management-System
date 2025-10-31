package com.library.domain.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import com.library.domain.strategies.FineStrategy;
import com.library.domain.strategies.BookFineStrategy;

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
    private FineStrategy fineStrategy;
    private LocalDate lastFineAppliedDate;

    /**
     * Constructor for Loan with default BookFineStrategy
     */
    public Loan(String id, User user, Book book, int loanPeriodDays) {
        this(id, user, book, loanPeriodDays, new BookFineStrategy());
    }

    /**
     * Constructor for Loan with custom FineStrategy
     */
    public Loan(String id, User user, Book book, int loanPeriodDays, FineStrategy fineStrategy) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.borrowDate = LocalDate.now();
        this.dueDate = this.borrowDate.plusDays(loanPeriodDays);
        this.returnDate = null;
        this.fineStrategy = fineStrategy;
        this.lastFineAppliedDate = null;

        // Validate parameters
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Loan ID cannot be null or empty");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        if (loanPeriodDays <= 0) {
            throw new IllegalArgumentException("Loan period must be positive");
        }
        if (fineStrategy == null) {
            throw new IllegalArgumentException("Fine strategy cannot be null");
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public FineStrategy getFineStrategy() {
        return fineStrategy;
    }

    public LocalDate getLastFineAppliedDate() {
        return lastFineAppliedDate;
    }

    // Setters
    public void setReturnDate(LocalDate returnDate) {
        if (returnDate != null && returnDate.isBefore(borrowDate)) {
            throw new IllegalArgumentException("Return date cannot be before borrow date");
        }
        this.returnDate = returnDate;
    }

    public void setFineStrategy(FineStrategy fineStrategy) {
        if (fineStrategy == null) {
            throw new IllegalArgumentException("Fine strategy cannot be null");
        }
        this.fineStrategy = fineStrategy;
    }

    public void setLastFineAppliedDate(LocalDate lastFineAppliedDate) {
        this.lastFineAppliedDate = lastFineAppliedDate;
    }

    /**
     * Check if loan is overdue (US2.2)
     * @return true if loan is overdue
     */
    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && returnDate == null;
    }

    /**
     * Calculate number of overdue days (US2.2)
     * @return number of overdue days
     */
    public int getOverdueDays() {
        if (!isOverdue()) return 0;
        return Math.max(0, (int) ChronoUnit.DAYS.between(dueDate, LocalDate.now()));
    }

    /**
     * Calculate fine amount using the configured strategy
     * @return calculated fine amount
     */
    public double calculateFine() {
        if (!isOverdue()) return 0.0;
        int overdueDays = getOverdueDays();
        return fineStrategy.calculateFine(overdueDays);
    }

    /**
     * Calculate fine for a specific date (useful for testing)
     * @param checkDate date to check fine for
     * @return calculated fine amount
     */
    public double calculateFine(LocalDate checkDate) {
        if (returnDate != null || checkDate.isBefore(dueDate) || checkDate.equals(dueDate)) {
            return 0.0;
        }

        if (checkDate.isAfter(dueDate)) {
            int overdueDays = Math.max(0, (int) ChronoUnit.DAYS.between(dueDate, checkDate));
            return fineStrategy.calculateFine(overdueDays);
        }

        return 0.0;
    }

    /**
     * Check if loan is active (not returned)
     * @return true if loan is active
     */
    public boolean isActive() {
        return returnDate == null;
    }

    /**
     * Check if fine was already applied today
     * @return true if fine was applied today
     */
    public boolean isFineAppliedToday() {
        return lastFineAppliedDate != null && lastFineAppliedDate.equals(LocalDate.now());
    }

    /**
     * Mark fine as applied today
     */
    public void markFineApplied() {
        this.lastFineAppliedDate = LocalDate.now();
    }

    /**
     * Get days remaining until due date
     * @return days remaining (negative if overdue)
     */
    public int getDaysRemaining() {
        if (returnDate != null) return 0;
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    /**
     * Check if loan can be renewed
     * @return true if loan can be renewed
     */
    public boolean canBeRenewed() {
        return isActive() && !isOverdue() && getDaysRemaining() <= 7; // Can renew within 7 days of due date
    }

    /**
     * Renew loan for additional period
     * @param additionalDays number of days to extend loan
     * @return new due date
     */
    public LocalDate renew(int additionalDays) {
        if (!canBeRenewed()) {
            throw new IllegalStateException("Loan cannot be renewed");
        }
        if (additionalDays <= 0) {
            throw new IllegalArgumentException("Additional days must be positive");
        }

        this.dueDate = this.dueDate.plusDays(additionalDays);
        return this.dueDate;
    }

    /**
     * Get loan status description
     * @return status description
     */
    public String getStatus() {
        if (returnDate != null) {
            return "Returned on " + returnDate;
        } else if (isOverdue()) {
            return "OVERDUE (" + getOverdueDays() + " days)";
        } else {
            int daysRemaining = getDaysRemaining();
            if (daysRemaining == 0) {
                return "Due today";
            } else if (daysRemaining == 1) {
                return "Due tomorrow";
            } else {
                return "Due in " + daysRemaining + " days";
            }
        }
    }

    /**
     * Get detailed loan information
     * @return detailed loan info
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Loan ID: ").append(id).append("\n");
        sb.append("Book: ").append(book.getTitle()).append(" by ").append(book.getAuthor()).append("\n");
        sb.append("User: ").append(user.getName()).append(" (").append(user.getEmail()).append(")\n");
        sb.append("Borrowed: ").append(borrowDate).append("\n");
        sb.append("Due: ").append(dueDate).append("\n");
        sb.append("Status: ").append(getStatus()).append("\n");

        if (returnDate != null) {
            sb.append("Returned: ").append(returnDate).append("\n");
        }

        if (isOverdue()) {
            sb.append("Overdue days: ").append(getOverdueDays()).append("\n");
            sb.append("Current fine: ").append(String.format("%.2f", calculateFine())).append(" NIS\n");
        }

        if (lastFineAppliedDate != null) {
            sb.append("Last fine applied: ").append(lastFineAppliedDate).append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        String status;
        if (returnDate != null) {
            status = "Returned";
        } else if (isOverdue()) {
            status = "OVERDUE (" + getOverdueDays() + " days)";
        } else {
            status = "Active";
        }

        return book.getTitle() +
                " - Borrowed: " + borrowDate +
                ", Due: " + dueDate +
                " - " + status +
                " (Fine: " + String.format("%.2f", calculateFine()) + " NIS)";
    }

    /**
     * Check if this loan is equal to another object
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Loan other = (Loan) obj;
        return id.equals(other.id);
    }

    /**
     * Hash code for use in collections
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Create a copy of this loan (useful for testing)
     * @return new Loan instance with same properties
     */
    public Loan copy() {
        Loan copy = new Loan(this.id, this.user, this.book,
                (int) ChronoUnit.DAYS.between(this.borrowDate, this.dueDate),
                this.fineStrategy);
        copy.returnDate = this.returnDate;
        copy.lastFineAppliedDate = this.lastFineAppliedDate;
        return copy;
    }

    /**
     * Validate loan state consistency
     * @return true if loan state is valid
     */
    public boolean isValid() {
        if (id == null || id.trim().isEmpty()) return false;
        if (user == null) return false;
        if (book == null) return false;
        if (borrowDate == null) return false;
        if (dueDate == null) return false;
        if (borrowDate.isAfter(dueDate)) return false;
        if (returnDate != null && returnDate.isBefore(borrowDate)) return false;
        if (fineStrategy == null) return false;
        return true;
    }
}