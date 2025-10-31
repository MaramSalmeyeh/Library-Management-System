package com.library.domain.service;

import com.library.domain.model.Book;
import com.library.domain.model.Loan;
import com.library.domain.model.User;
import com.library.repository.LoanRepository;

import java.util.UUID;

/**
 * Domain service for borrowing operations
 */
public class BorrowingDomainService {
    private final LoanRepository loanRepository;

    public BorrowingDomainService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    /**
     * Borrow a book for 28 days (US2.1)
     */
    public boolean borrowBook(User user, Book book) {
        // Check if user can borrow
        if (!user.canBorrow()) {
            throw new IllegalStateException("User cannot borrow - has overdue items or unpaid fines");
        }

        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is not available");
        }

        // Create loan with 28 days period
        String loanId = UUID.randomUUID().toString();
        Loan loan = new Loan(loanId, user, book, 28); // 28 days for books

        // Update book status
        book.markBorrowed();

        // Add loan to user and repository
        user.addLoan(loan);
        loanRepository.save(loan);

        return true;
    }

    /**
     * Return a borrowed book
     */
    public boolean returnBook(User user, Book book) {
        // Find the active loan for this book
        Loan activeLoan = user.getActiveLoans().stream()
                .filter(loan -> loan.getBook().equals(book) && loan.getReturnDate() == null)
                .findFirst()
                .orElse(null);

        if (activeLoan == null) {
            throw new IllegalStateException("User has not borrowed this book");
        }

        // Mark as returned
        activeLoan.setReturnDate(java.time.LocalDate.now());

        // Update book availability
        book.markReturned();

        // Remove from user's active loans
        user.removeLoan(activeLoan);


        loanRepository.save(activeLoan);

        return true;
    }

    /**
     * Renew a borrowed book
     * @param user user renewing the book
     * @param book book to renew
     * @param additionalDays number of days to extend
     * @return true if renewal successful
     */
    public boolean renewBook(User user, Book book, int additionalDays) {
        // Find the active loan for this book
        Loan activeLoan = user.getActiveLoans().stream()
                .filter(loan -> loan.getBook().equals(book) && loan.isActive())
                .findFirst()
                .orElse(null);

        if (activeLoan == null) {
            throw new IllegalStateException("User has not borrowed this book");
        }

        if (!activeLoan.canBeRenewed()) {
            throw new IllegalStateException("Book cannot be renewed - may be overdue or too early");
        }

        // Renew the loan
        activeLoan.renew(additionalDays);


        loanRepository.save(activeLoan);

        return true;
    }

    /**
     * Check if user can borrow a specific book
     * @param user user to check
     * @param book book to check
     * @return true if user can borrow the book
     */
    public boolean canUserBorrowBook(User user, Book book) {
        if (!user.canBorrow()) {
            return false;
        }

        if (!book.isAvailable()) {
            return false;
        }

        // Check if user already has this book borrowed
        boolean alreadyBorrowed = user.getActiveLoans().stream()
                .anyMatch(loan -> loan.getBook().equals(book) && loan.isActive());

        return !alreadyBorrowed;
    }

    /**
     * Get active loan for user and book
     * @param user user to check
     * @param book book to check
     * @return active loan or null if not found
     */
    public Loan getActiveLoan(User user, Book book) {
        return user.getActiveLoans().stream()
                .filter(loan -> loan.getBook().equals(book) && loan.isActive())
                .findFirst()
                .orElse(null);
    }

    /**
     * Calculate total fines for user from active loans
     * @param user user to calculate fines for
     * @return total fines amount
     */
    public double calculateActiveLoansFines(User user) {
        return user.getActiveLoans().stream()
                .filter(Loan::isOverdue)
                .mapToDouble(Loan::calculateFine)
                .sum();
    }

    /**
     * Check if user has overdue books
     * @param user user to check
     * @return true if user has overdue books
     */
    public boolean hasOverdueBooks(User user) {
        return user.getActiveLoans().stream()
                .anyMatch(Loan::isOverdue);
    }

    /**
     * Get number of active loans for user
     * @param user user to check
     * @return number of active loans
     */
    public int getActiveLoansCount(User user) {
        return user.getActiveLoans().size();
    }

    /**
     * Get total possible loans for user (could be based on user type)
     * @param user user to check
     * @return maximum allowed loans
     */
    public int getMaxAllowedLoans(User user) {
        // Default maximum loans per user
        return 5;
    }

    /**
     * Check if user has reached loan limit
     * @param user user to check
     * @return true if user has reached loan limit
     */
    public boolean hasReachedLoanLimit(User user) {
        return getActiveLoansCount(user) >= getMaxAllowedLoans(user);
    }
}