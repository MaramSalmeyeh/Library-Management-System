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

        Files.write(tempDir.resolve("admins.txt"), List.of());
        Files.write(tempDir.resolve("librarians.txt"), List.of());
        Files.write(tempDir.resolve("loans.txt"), List.of());
        Files.write(tempDir.resolve("fines.txt.txt"), List.of());


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


        assertEquals(loan.getBorrowDate().plusDays(7), loan.getDueDate());


        assertEquals(MediaType.CD, loan.getMediaType());
    }


    @Test
    void borrowBook_marksBookAsBorrowed_andCreatesLoanFor28Days() {
        Loan loan = loanService.borrowBook("U1", "B1");

        assertNotNull(loan);
        assertEquals("U1", loan.getUserId());
        assertEquals("B1", loan.getBookId());


        assertEquals(loan.getBorrowDate().plusDays(28), loan.getDueDate());


        List<Book> booksAfter = storage.loadBooks();
        assertEquals(1, booksAfter.size());
        assertTrue(booksAfter.get(0).isBorrowed());


        List<Loan> loans = storage.loadLoans();
        assertEquals(1, loans.size());
        assertEquals("U1", loans.get(0).getUserId());
    }

    @Test
    void borrowBook_onAlreadyBorrowedBook_throwsException() {

        loanService.borrowBook("U1", "B1");


        assertThrows(IllegalStateException.class,
                () -> loanService.borrowBook("U2", "B1"));
    }

    @Test
    void returnBook_setsReturnDate_andMarksBookAvailable() {

        Loan loan = loanService.borrowBook("U1", "B1");
        String loanId = loan.getId();


        loanService.returnBook(loanId);


        List<Loan> loans = storage.loadLoans();
        assertEquals(1, loans.size());
        assertTrue(loans.get(0).isReturned());


        List<Book> books = storage.loadBooks();
        assertEquals(1, books.size());
        assertFalse(books.get(0).isBorrowed());
    }

    @Test
    void getOverdueLoans_returnsOnlyLoansPastDueDate_andNotReturned() throws IOException {

        List<Loan> loans = new ArrayList<>();
        LocalDate today = LocalDate.now();


        loans.add(new Loan("L1", "U1", "B1",
                today.minusDays(30),  // borrow
                today.minusDays(2),   // due
                null));               // not returned


        loans.add(new Loan("L2", "U2", "B1",
                today.minusDays(5),
                today.plusDays(5),
                null));


        loans.add(new Loan("L3", "U3", "B1",
                today.minusDays(40),
                today.minusDays(10),
                today.minusDays(5))); // returned

        storage.saveLoans(loans);

        List<Loan> overdue = loanService.getOverdueLoans();

        assertEquals(1, overdue.size());
        assertEquals("L1", overdue.get(0).getId());
    }

    @Test
    void borrowBook_whenBookIdNotFound_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> loanService.borrowBook("U1", "B-DOES-NOT-EXIST"));
    }

    @Test
    void returnBook_whenLoanIdUnknown_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> loanService.returnBook("L-404"));
    }
    @Test
    void hasOverdueLoans_checksOnlyUnreturnedLoansForUser() throws IOException {
        LocalDate today = LocalDate.now();

        List<Loan> loans = new ArrayList<>();

        loans.add(new Loan("L1", "U1", "B1",
                today.minusDays(20), today.minusDays(1), null));


        loans.add(new Loan("L2", "U1", "B2",
                today.minusDays(30), today.minusDays(5), today.minusDays(2)));


        loans.add(new Loan("L3", "U2", "B3",
                today.minusDays(25), today.minusDays(3), null));


        loans.add(new Loan("L4", "U1", "B4",
                today.minusDays(2), today.plusDays(5), null));


        loans.add(new Loan("L5", "U3", "B5",
                today.minusDays(2), today.plusDays(10), null));

        storage.saveLoans(loans);

        assertTrue(loanService.hasOverdueLoans("U1"));
        assertTrue(loanService.hasOverdueLoans("U2"));
        assertFalse(loanService.hasOverdueLoans("U3"));
    }

    @Test
    void hasActiveLoans_detectsUnreturnedLoansForUser() throws IOException {
        LocalDate today = LocalDate.now();
        List<Loan> loans = new ArrayList<>();


        loans.add(new Loan("L1", "U1", "B1",
                today.minusDays(10), today.minusDays(2), today.minusDays(1)));


        loans.add(new Loan("L2", "U1", "B2",
                today.minusDays(3), today.plusDays(10), null));


        loans.add(new Loan("L3", "U2", "B3",
                today.minusDays(5), today.plusDays(2), null));

        storage.saveLoans(loans);

        assertTrue(loanService.hasActiveLoans("U1"));
        assertTrue(loanService.hasActiveLoans("U2"));


        loanService.returnBook("L3");
        assertFalse(loanService.hasActiveLoans("U2"));
    }

}
