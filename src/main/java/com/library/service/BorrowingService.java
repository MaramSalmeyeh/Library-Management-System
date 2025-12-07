package com.library.service;

import com.library.domain.Loan;

/**
 * Handles borrowing operations for users.
 * <p>
 * This service enforces borrowing rules such as:
 * <ul>
 *     <li>Users cannot borrow if they have unpaid fines</li>
 *     <li>Users cannot borrow if they have overdue loans</li>
 * </ul>
 * The actual creation of loans is delegated to {@link LoanService}.

 *
 * <p>
 * Supports borrowing both books and CDs.
 * </p>
 *
 * @author Maram
 * @version 1.0
 */
public class BorrowingService {

    /**
     * Service responsible for handling loan creation and retrieval.
     */
    private final LoanService loanService;

    /**
     * Service responsible for calculating and checking user fines.
     */
    private final FineService fineService;

    /**
     * Creates a new BorrowingService instance.
     *
     * @param loanService service managing book/CD loans
     * @param fineService service for calculating fines and balances
     */
    public BorrowingService(LoanService loanService, FineService fineService) {
        this.loanService = loanService;
        this.fineService = fineService;
    }

    /**
     * Allows a user to borrow a book.
     * <p>
     * Borrowing is blocked if:
     * <ul>
     *     <li>The user has unpaid fines</li>
     *     <li>The user has overdue loans</li>
     * </ul>
     * If all checks pass, the request is forwarded to
     * {@link LoanService#borrowBook(String, String)}.

     *
     * @param userId the ID of the borrowing user
     * @param bookId the ID of the book being borrowed
     * @return a newly created {@link Loan}
     * @throws IllegalStateException if the user is not allowed to borrow
     */
    public Loan borrowBook(String userId, String bookId) {

        double outstanding = fineService.getUserOutstandingBalance(userId);
        if (outstanding > 0) {
            throw new IllegalStateException(
                    "User has unpaid fines (" + outstanding + "). Borrowing not allowed."
            );
        }

        if (loanService.hasOverdueLoans(userId)) {
            throw new IllegalStateException(
                    "User has overdue loans. Borrowing not allowed until overdue items are returned."
            );
        }

        return loanService.borrowBook(userId, bookId);
    }

    /**
     * Allows a user to borrow a CD.
     * <p>
     * Same borrowing rules apply as with books:
     * <ul>
     *     <li>No unpaid fines</li>
     *     <li>No overdue loans</li>
     * </ul>
     * Uses {@link LoanService#borrowCd(String, String)} internally.

     *
     * @param userId the ID of the borrowing user
     * @param cdId   the ID of the CD to borrow
     * @return a newly created {@link Loan}
     * @throws IllegalStateException if the user cannot borrow due to fines or overdue loans
     */
    public Loan borrowCd(String userId, String cdId) {

        double outstanding = fineService.getUserOutstandingBalance(userId);
        if (outstanding > 0) {
            throw new IllegalStateException(
                    "User has unpaid fines (" + outstanding + "). Borrowing not allowed."
            );
        }

        if (loanService.hasOverdueLoans(userId)) {
            throw new IllegalStateException(
                    "User has overdue loans. Borrowing not allowed until overdue items are returned."
            );
        }

        return loanService.borrowCd(userId, cdId);
    }

}
