package com.library.service;

import com.library.domain.FileStorage;
import com.library.domain.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.library.domain.MediaType;


import static org.junit.jupiter.api.Assertions.*;

class BorrowingServiceTest {

    @TempDir
    Path tempDir;

    private FileStorage storage;
    private LoanService loanService;
    private FineService fineService;
    private BorrowingService borrowingService;


    private static final String USER_ID = "U1";
    private static final String BOOK_ID = "B1";

    @BeforeEach
    void setUp() throws IOException {

        Files.write(tempDir.resolve("admins.txt"), List.of());
        Files.write(tempDir.resolve("librarians.txt"), List.of());
        Files.write(tempDir.resolve("users.txt"), List.of());
        Files.write(tempDir.resolve("loans.txt"), List.of());
        Files.write(tempDir.resolve("fines.txt"), List.of());


        Files.write(
                tempDir.resolve("books.txt"),
                List.of("B1;Test Book;Author;111;false")
        );

        storage = new FileStorage(tempDir.toString());
        loanService = new LoanService(storage);
        fineService = new FineService(storage);
        borrowingService = new BorrowingService(loanService, fineService);
    }


    @Test
    void borrowBook_whenUserIsClean_createsLoan() {
        Loan loan = borrowingService.borrowBook(USER_ID, BOOK_ID);

        assertNotNull(loan);
        assertEquals(USER_ID, loan.getUserId());
        assertEquals(BOOK_ID, loan.getBookId());


        List<Loan> loans = storage.loadLoans();
        assertEquals(1, loans.size());
        assertEquals(USER_ID, loans.get(0).getUserId());
    }


    @Test
    void borrowBook_whenUserHasOverdueLoans_throwsException() {
        LocalDate today = LocalDate.now();

        List<Loan> loans = new ArrayList<>();
        loans.add(new Loan(
                "L1",
                USER_ID,
                BOOK_ID,
                today.minusDays(40),
                today.minusDays(10),
                null
        ));
        storage.saveLoans(loans);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> borrowingService.borrowBook(USER_ID, BOOK_ID)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("overdue"),
                "Message should mention overdue loans");
    }


    @Test
    void borrowBook_whenUserHasUnpaidFines_throwsException() {

        fineService.createFine(USER_ID, 20.0);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> borrowingService.borrowBook(USER_ID, BOOK_ID)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("unpaid"),
                "Message should mention unpaid fines");
    }

    @Test
    void borrowCd_whenUserHasNoFines_succeedsWithCdMediaType() {

        Loan loan = borrowingService.borrowCd("U1", "CD1");

        assertNotNull(loan);
        assertEquals("U1", loan.getUserId());
        assertEquals("CD1", loan.getBookId());
        assertEquals(MediaType.CD, loan.getMediaType());
    }

}
