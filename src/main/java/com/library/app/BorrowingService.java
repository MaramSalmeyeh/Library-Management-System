package com.library.app;

import com.library.domain.model.User;
import com.library.domain.model.Book;
import com.library.domain.model.CD;
import com.library.domain.service.BorrowingDomainService;

/**
 * Application service for borrowing operations
 * @author Your Name
 * @version 1.0
 */
public class BorrowingService {
    private final BorrowingDomainService borrowingDomainService;

    public BorrowingService(BorrowingDomainService borrowingDomainService) {
        this.borrowingDomainService = borrowingDomainService;
    }

    /**
     * Borrow a book for user
     * @param user user borrowing the book
     * @param book book to borrow
     * @return true if borrow successful
     */
    public boolean borrowBook(User user, Book book) {
        try {
            return borrowingDomainService.borrowBook(user, book);
        } catch (IllegalStateException e) {
            System.out.println("Borrow failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Borrow a CD for user
     * @param user user borrowing the CD
     * @param cd CD to borrow
     * @return true if borrow successful
     */
    public boolean borrowCD(User user, CD cd) {
        try {
            return borrowingDomainService.borrowCD(user, cd);
        } catch (IllegalStateException e) {
            System.out.println("Borrow failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Return a borrowed book
     * @param user user returning the book
     * @param book book to return
     * @return true if return successful
     */
    public boolean returnBook(User user, Book book) {
        return borrowingDomainService.returnItem(user, book);
    }

    /**
     * Return a borrowed CD
     * @param user user returning the CD
     * @param cd CD to return
     * @return true if return successful
     */
    public boolean returnCD(User user, CD cd) {
        return borrowingDomainService.returnItem(user, cd);
    }

    /**
     * Calculate total fines for user
     * @param user user to calculate fines for
     * @return total fine amount
     */
    public double calculateTotalFines(User user) {
        return user.getFineBalance(); // Simple version - can be enhanced
    }

    /**
     * Pay fine for user
     * @param user user paying fine
     * @param amount amount to pay
     * @return remaining balance
     */
    public double payFine(User user, double amount) {
        return user.payFine(amount);
    }

    /**
     * Check if user can borrow
     * @param user user to check
     * @return true if user can borrow
     */
    public boolean canUserBorrow(User user) {
        return user.canBorrow();
    }
}