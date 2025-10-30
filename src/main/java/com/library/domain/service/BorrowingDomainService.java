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

        return true;
    }
}