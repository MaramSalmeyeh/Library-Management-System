package com.library.domain.service;

import com.library.domain.model.User;
import com.library.domain.model.Book;
import com.library.domain.model.CD;
import com.library.domain.model.Loan;
import com.library.repository.LoanRepository;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Domain service for borrowing operations
 * @author Your Name
 * @version 1.0
 */
public class BorrowingDomainService {
    private final LoanRepository loanRepository;

    public BorrowingDomainService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    /**
     * Borrow a book for user (28 days for books - US2.1)
     * @param user user borrowing the book
     * @param book book to borrow
     * @return true if borrow successful
     */
    public boolean borrowBook(User user, Book book) {
        // Check if user can borrow (US4.1)
        if (!user.canBorrow()) {
            throw new IllegalStateException("User cannot borrow - has overdue items or unpaid fines");
        }

        if (!book.isAvailable()) return false;

        // Create loan with 28 days period (US2.1)
        String loanId = UUID.randomUUID().toString();
        Loan loan = new Loan(loanId, user, book, 28);

        // Update book status
        book.markBorrowed();

        // Add loan to user and repository
        user.addLoan(loan);
        loanRepository.save(loan);

        return true;
    }

    /**
     * Borrow a CD for user (7 days for CDs - US5.1)
     * @param user user borrowing the CD
     * @param cd CD to borrow
     * @return true if borrow successful
     */
    public boolean borrowCD(User user, CD cd) {
        // Check if user can borrow (US4.1)
        if (!user.canBorrow()) {
            throw new IllegalStateException("User cannot borrow - has overdue items or unpaid fines");
        }

        if (!cd.isAvailable()) return false;

        // Create loan with 7 days period (US5.1)
        String loanId = UUID.randomUUID().toString();
        Loan loan = new Loan(loanId, user, cd, 7);

        // Update CD status
        cd.setAvailable(false);

        // Add loan to user and repository
        user.addLoan(loan);
        loanRepository.save(loan);

        return true;
    }

    /**
     * Return a borrowed item (book or CD)
     * @param user user returning the item
     * @param item item to return
     * @return true if return successful
     */
    public boolean returnItem(User user, Object item) {
        // Find the active loan for this item
        Loan activeLoan = user.getActiveLoans().stream()
                .filter(loan -> loan.getItem().equals(item) && loan.getReturnDate() == null)
                .findFirst()
                .orElse(null);

        if (activeLoan == null) return false;

        // Mark as returned
        activeLoan.setReturnDate(LocalDate.now());

        // Update item availability
        if (item instanceof Book) {
            ((Book) item).markReturned();
        } else if (item instanceof CD) {
            ((CD) item).setAvailable(true);
        }

        // Remove from user's active loans
        user.removeLoan(activeLoan);

        return true;
    }

    /**
     * Check if item is overdue
     * @param item item to check
     * @param user user who borrowed the item
     * @return true if item is overdue
     */
    public boolean isItemOverdue(Object item, User user) {
        return user.getActiveLoans().stream()
                .filter(loan -> loan.getItem().equals(item))
                .anyMatch(Loan::isOverdue);
    }

    /**
     * Calculate overdue days for an item
     * @param item item to calculate for
     * @param user user who borrowed the item
     * @return number of overdue days
     */
    public int getOverdueDays(Object item, User user) {
        return user.getActiveLoans().stream()
                .filter(loan -> loan.getItem().equals(item))
                .findFirst()
                .map(Loan::getOverdueDays)
                .orElse(0);
    }
}