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
    /**
     * Borrow book مع فحص الغرامات والقروض المتأخرة.
     *
     * @throws IllegalStateException إذا كان لدى المستخدم غرامات غير مدفوعة
     *                               أو لديه إعارات متأخرة.
     */
    public Loan borrowBook(String userId, String bookId) {

        // 1) غرامات غير مدفوعة
        double outstanding = fineService.getUserOutstandingBalance(userId);
        if (outstanding > 0) {
            throw new IllegalStateException(
                    "User has unpaid fines (" + outstanding + "). Borrowing not allowed."
            );
        }

        // 2) إعارات متأخرة (overdue)
        if (loanService.hasOverdueLoans(userId)) {
            throw new IllegalStateException(
                    "User has overdue loans. Borrowing not allowed until overdue items are returned."
            );
        }

        // 3) لو كل شيء تمام → نسمح بالاستعارة
        return loanService.borrowBook(userId, bookId);
    }

    /**
     * Borrow CD مع نفس قواعد Sprint 4:
     * - ممنوع يستعير لو عنده غرامات غير مدفوعة
     * - ممنوع يستعير لو عنده إعارات متأخرة
     */
    public Loan borrowCd(String userId, String cdId) {

        // 1) غرامات غير مدفوعة
        double outstanding = fineService.getUserOutstandingBalance(userId);
        if (outstanding > 0) {
            throw new IllegalStateException(
                    "User has unpaid fines (" + outstanding + "). Borrowing not allowed."
            );
        }

        // 2) إعارات متأخرة (overdue)
        if (loanService.hasOverdueLoans(userId)) {
            throw new IllegalStateException(
                    "User has overdue loans. Borrowing not allowed until overdue items are returned."
            );
        }

        // 3) لو كل شيء تمام → نسمح بالاستعارة
        return loanService.borrowCd(userId, cdId);
    }



}
