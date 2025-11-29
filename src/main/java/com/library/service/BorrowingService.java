package com.library.service;

import com.library.domain.Loan;

/**
 * Application/Use-case service:
 * ينسّق بين LoanService و FineService
 * حتى يطبّق قاعدة:
 * "لا يمكن الاستعارة مع وجود غرامات غير مدفوعة".
 */
public class BorrowingService {

    private final LoanService loanService;
    private final FineService fineService;

    public BorrowingService(LoanService loanService, FineService fineService) {
        this.loanService = loanService;
        this.fineService = fineService;
    }

    /**
     * Borrow book مع فحص الغرامات.
     *
     * @throws IllegalStateException إذا كان لدى المستخدم غرامات غير مدفوعة.
     */
    public Loan borrowBook(String userId, String bookId) {
        double outstanding = fineService.getUserOutstandingBalance(userId);
        if (outstanding > 0) {
            throw new IllegalStateException(
                    "User has unpaid fines (" + outstanding + "). Borrowing is not allowed until full payment."
            );
        }

        // مافي غرامات → نسمح بالاستعارة
        return loanService.borrowBook(userId, bookId);
    }
}
