package com.library.service;

import com.library.domain.Book;
import com.library.domain.FileStorage;
import com.library.domain.Loan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanService {

    private final FileStorage storage;

    public LoanService(FileStorage storage) {
        this.storage = storage;
    }

    /**
     * US2.1 Borrow book:
     * - يتأكد إن الكتاب موجود ومش مستعار
     * - يعلّم الكتاب borrowed = true
     * - ينشئ Loan لمدة 28 يوم
     */
    public Loan borrowBook(String userId, String bookId) {
        // 1) نجيب الكتب
        List<Book> books = storage.loadBooks();

        Book target = null;
        for (Book b : books) {
            if (b.getId().equals(bookId)) {
                target = b;
                break;
            }
        }

        if (target == null) {
            throw new IllegalArgumentException("Book with id " + bookId + " not found");
        }

        if (target.isBorrowed()) {
            throw new IllegalStateException("Book is already borrowed");
        }

        // 2) نحدّث حالة الكتاب
        target.setBorrowed(true);
        storage.saveBooks(books);

        // 3) ننشئ Loan جديد
        List<Loan> loans = storage.loadLoans();
        String loanId = "L" + (loans.size() + 1);

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(28);

        Loan loan = new Loan(
                loanId,
                userId,
                bookId,
                borrowDate,
                dueDate,
                null // returnDate
        );

        loans.add(loan);
        storage.saveLoans(loans);

        return loan;
    }

    /**
     * إرجاع كتاب:
     * - يحدد returnDate
     * - يعلّم الكتاب إنه مش مستعار
     */
    public void returnBook(String loanId) {
        List<Loan> loans = storage.loadLoans();
        Loan targetLoan = null;

        for (Loan loan : loans) {
            if (loan.getId().equals(loanId)) {
                targetLoan = loan;
                break;
            }
        }

        if (targetLoan == null) {
            throw new IllegalArgumentException("Loan with id " + loanId + " not found");
        }

        if (targetLoan.isReturned()) {
            // already returned
            return;
        }

        // حدد تاريخ الإرجاع
        targetLoan.markReturned(LocalDate.now());
        storage.saveLoans(loans);

        // نحدّث حالة الكتاب
        List<Book> books = storage.loadBooks();
        for (Book book : books) {
            if (book.getId().equals(targetLoan.getBookId())) {
                book.setBorrowed(false);
                break;
            }
        }
        storage.saveBooks(books);
    }

    /**
     * US2.2 Overdue detection:
     * يرجع كل الـ loans المتأخرة عن تاريخ اليوم.
     */
    public List<Loan> getOverdueLoans() {
        LocalDate today = LocalDate.now();
        List<Loan> loans = storage.loadLoans();
        List<Loan> overdue = new ArrayList<>();

        for (Loan loan : loans) {
            if (loan.isOverdue(today)) {
                overdue.add(loan);
            }
        }
        return overdue;
    }

    /**
     * ممكن تستخدمها لعرض كل الإعارات
     */
    public List<Loan> getAllLoans() {
        return storage.loadLoans();
    }
}
