package com.library.service;

import com.library.domain.Loan;


public class BorrowingService {

    private final LoanService loanService;
    private final FineService fineService;

    public BorrowingService(LoanService loanService, FineService fineService) {
        this.loanService = loanService;
        this.fineService = fineService;
    }


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
