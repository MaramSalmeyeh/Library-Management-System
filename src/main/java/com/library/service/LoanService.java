package com.library.service;

import com.library.domain.Book;
import com.library.domain.FileStorage;
import com.library.domain.Loan;
import com.library.domain.MediaType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanService {

    private final FileStorage storage;

    public LoanService(FileStorage storage) {
        this.storage = storage;
    }
    public List<Loan> getLoansForUser(String userId) {
        return storage.loadLoans().stream()
                .filter(l -> l.getUserId().equals(userId))
                .toList();
    }

    public Loan borrowBook(String userId, String bookId) {

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


        target.setBorrowed(true);
        storage.saveBooks(books);


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

            return;
        }


        targetLoan.markReturned(LocalDate.now());
        storage.saveLoans(loans);


        List<Book> books = storage.loadBooks();
        for (Book book : books) {
            if (book.getId().equals(targetLoan.getBookId())) {
                book.setBorrowed(false);
                break;
            }
        }
        storage.saveBooks(books);
    }


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


    public List<Loan> getAllLoans() {
        return storage.loadLoans();
    }

    public boolean hasOverdueLoans(String userId) {
        LocalDate today = LocalDate.now();
        List<Loan> loans = storage.loadLoans();

        for (Loan loan : loans) {

            // إعارة لنفس المستخدم
            if (!loan.getUserId().equals(userId)) {
                continue;
            }

            // لو رجّع الكتاب، لا تعتبر متأخرة
            if (loan.getReturnDate() != null) {
                continue;
            }

            // إذا تاريخ الاستحقاق قبل اليوم → متأخر
            if (loan.getDueDate().isBefore(today)) {
                return true;
            }
        }

        return false;
    }
    /**
     * Sprint 4 – يساعد US4.2:
     * يفحص إذا كان لدى المستخدم أي إعارات فعّالة (لم تُرجع بعد).
     */
    public boolean hasActiveLoans(String userId) {
        List<Loan> loans = storage.loadLoans();

        for (Loan loan : loans) {
            if (!loan.getUserId().equals(userId)) {
                continue;
            }
            // loan فعّال إذا ما رجّع الكتاب
            if (!loan.isReturned()) {
                return true;
            }
        }

        return false; // ما في ولا إعارة فعّالة
    }

    /**
     * استعارة CD لمدة 7 أيام.
     * US5.1 Borrow CD
     */
    public Loan borrowCd(String userId, String cdId) {

        // ما في عندنا حالياً جدول CDs، فبننشئ Loan مباشرة
        List<Loan> loans = storage.loadLoans();
        String loanId = "L" + (loans.size() + 1);

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(7);

        Loan loan = new Loan(
                loanId,
                userId,
                cdId,
                borrowDate,
                dueDate,
                null,
                MediaType.CD        // ← أهم سطر: نوع الوسيط CD
        );

        loans.add(loan);
        storage.saveLoans(loans);

        return loan;
    }




}



