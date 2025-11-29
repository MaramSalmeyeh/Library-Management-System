package com.library.service;

import com.library.domain.FileStorage;
import com.library.domain.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BorrowingServiceTest {

    @TempDir
    Path tempDir;

    private FileStorage storage;
    private LoanService loanService;
    private FineService fineService;
    private BorrowingService borrowingService;

    @BeforeEach
    void setUp() throws IOException {
        // تجهيز ملفات DB الأساسية
        Files.write(tempDir.resolve("admins.txt"), List.of());
        Files.write(tempDir.resolve("librarians.txt"), List.of());
        Files.write(tempDir.resolve("loans.txt"), List.of());
        Files.write(tempDir.resolve("fines.txt"), List.of());
        Files.write(tempDir.resolve("books.txt"),
                List.of("B1;Harry;Author;111;false"));

        storage = new FileStorage(tempDir.toString());
        loanService = new LoanService(storage);
        fineService = new FineService(storage);
        borrowingService = new BorrowingService(loanService, fineService);
    }

    @Test
    void borrowBook_whenUserHasNoFines_succeeds() {
        // لا يوجد غرامات على U1
        Loan loan = borrowingService.borrowBook("U1", "B1");

        assertNotNull(loan);
        assertEquals("U1", loan.getUserId());
        assertEquals("B1", loan.getBookId());
    }

    @Test
    void borrowBook_whenUserHasUnpaidFines_throwsException() {
        // نضيف غرامة على U1
        fineService.createFine("U1", 20.0);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> borrowingService.borrowBook("U1", "B1")
        );

        assertTrue(ex.getMessage().toLowerCase().contains("unpaid"),
                "Message should mention unpaid fines");
    }
}
