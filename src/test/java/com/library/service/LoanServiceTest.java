package com.library.service;

import com.library.domain.Book;
import com.library.domain.FileStorage;
import com.library.domain.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.library.domain.MediaType;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {

    @TempDir
    Path tempDir;

    private FileStorage storage;
    private LoanService loanService;

    @BeforeEach
    void setUp() throws IOException {
        // نجهز ملفات DB الفاضية/الأساسية
        Files.write(tempDir.resolve("admins.txt"), List.of());
        Files.write(tempDir.resolve("librarians.txt"), List.of());
        Files.write(tempDir.resolve("loans.txt"), List.of());
        Files.write(tempDir.resolve("fines.txt.txt"), List.of());

        // نضيف كتاب واحد قابل للاستعارة
        Files.write(
                tempDir.resolve("books.txt"),
                List.of("B1;Harry Potter;Rowling;111;false")
        );

        storage = new FileStorage(tempDir.toString());
        loanService = new LoanService(storage);
    }

    @Test
    void borrowCd_createsLoanWith7DayDueDate_andMediaTypeCd() {
        Loan loan = loanService.borrowCd("U1", "CD1");

        assertNotNull(loan);
        assertEquals("U1", loan.getUserId());
        assertEquals("CD1", loan.getBookId());

        // due date = borrowDate + 7
        assertEquals(loan.getBorrowDate().plusDays(7), loan.getDueDate());

        // media type لازم يكون CD
        assertEquals(MediaType.CD, loan.getMediaType());
    }


    @Test
    void borrowBook_marksBookAsBorrowed_andCreatesLoanFor28Days() {
        Loan loan = loanService.borrowBook("U1", "B1");

        assertNotNull(loan);
        assertEquals("U1", loan.getUserId());
        assertEquals("B1", loan.getBookId());

        // نتأكد إن الـ dueDate بعد 28 يوم من borrowDate
        assertEquals(loan.getBorrowDate().plusDays(28), loan.getDueDate());

        // نتأكد إن الكتاب صار borrowed = true
        List<Book> booksAfter = storage.loadBooks();
        assertEquals(1, booksAfter.size());
        assertTrue(booksAfter.get(0).isBorrowed());

        // نتأكد إن الـ loan انخزن في الملف
        List<Loan> loans = storage.loadLoans();
        assertEquals(1, loans.size());
        assertEquals("U1", loans.get(0).getUserId());
    }

    @Test
    void borrowBook_onAlreadyBorrowedBook_throwsException() {
        // أول مرة: borrow عادي
        loanService.borrowBook("U1", "B1");

        // ثاني مرة: لازم يرمي IllegalStateException
        assertThrows(IllegalStateException.class,
                () -> loanService.borrowBook("U2", "B1"));
    }

    @Test
    void returnBook_setsReturnDate_andMarksBookAvailable() {
        // نعمل إعارة
        Loan loan = loanService.borrowBook("U1", "B1");
        String loanId = loan.getId();

        // نرجّع الكتاب
        loanService.returnBook(loanId);

        // نتأكد أن الـ loan صار له returnDate
        List<Loan> loans = storage.loadLoans();
        assertEquals(1, loans.size());
        assertTrue(loans.get(0).isReturned());

        // نتأكد أن الكتاب صار مش مستعار
        List<Book> books = storage.loadBooks();
        assertEquals(1, books.size());
        assertFalse(books.get(0).isBorrowed());
    }

    @Test
    void getOverdueLoans_returnsOnlyLoansPastDueDate_andNotReturned() throws IOException {
        // نحضّر loans بشكل مباشر
        List<Loan> loans = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Loan متأخر (dueDate قبل اليوم، ما رجع)
        loans.add(new Loan("L1", "U1", "B1",
                today.minusDays(30),  // borrow
                today.minusDays(2),   // due
                null));               // not returned

        // Loan مش متأخر (dueDate بعد اليوم)
        loans.add(new Loan("L2", "U2", "B1",
                today.minusDays(5),
                today.plusDays(5),
                null));

        // Loan قديم لكن already returned
        loans.add(new Loan("L3", "U3", "B1",
                today.minusDays(40),
                today.minusDays(10),
                today.minusDays(5))); // returned

        storage.saveLoans(loans);

        List<Loan> overdue = loanService.getOverdueLoans();

        assertEquals(1, overdue.size());
        assertEquals("L1", overdue.get(0).getId());
    }
}
