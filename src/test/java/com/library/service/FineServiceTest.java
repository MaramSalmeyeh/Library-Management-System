package com.library.service;

import com.library.domain.FileStorage;
import com.library.domain.Fine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FineServiceTest {

    @TempDir
    Path tempDir;

    private FileStorage storage;
    private FineService fineService;

    @BeforeEach
    void setUp() throws IOException {

        Files.write(tempDir.resolve("admins.txt"), List.of());
        Files.write(tempDir.resolve("librarians.txt"), List.of());
        Files.write(tempDir.resolve("books.txt"), List.of());
        Files.write(tempDir.resolve("loans.txt"), List.of());


        Files.write(tempDir.resolve("fines.txt.txt"), List.of());

        storage = new FileStorage(tempDir.toString());
        fineService = new FineService(storage);
    }

    @Test
    void getUserOutstandingBalance_noFines_returnsZero() {
        double balance = fineService.getUserOutstandingBalance("U1");
        assertEquals(0.0, balance);
    }

    @Test
    void createFine_addsFineAndPersistsIt() {
        Fine fine = fineService.createFine("U1", 20.0);

        assertNotNull(fine);
        assertEquals("U1", fine.getUserId());
        assertEquals(20.0, fine.getAmount());

        List<Fine> fines = storage.loadFines();
        assertEquals(1, fines.size());
        assertEquals(20.0, fines.get(0).getAmount());
    }

    @Test
    void getUserOutstandingBalance_sumsOnlyUnpaidFinesForUser() {

        fineService.createFine("U1", 30.0);
        fineService.createFine("U1", 10.0);


        fineService.createFine("U2", 50.0);

        double u1Balance = fineService.getUserOutstandingBalance("U1");
        double u2Balance = fineService.getUserOutstandingBalance("U2");

        assertEquals(40.0, u1Balance);
        assertEquals(50.0, u2Balance);
    }

    @Test
    void payFine_partialPayment_reducesBalanceButLeavesSomeUnpaid() {

        fineService.createFine("U1", 30.0);
        fineService.createFine("U1", 10.0);

        double newBalance = fineService.payFine("U1", 25.0);


        assertEquals(15.0, newBalance, 0.0001);


        List<Fine> fines = storage.loadFines();

        assertEquals(2, fines.size());


        assertEquals(5.0, fines.get(0).getAmount(), 0.0001);
        assertFalse(fines.get(0).isPaid());

        assertEquals(10.0, fines.get(1).getAmount(), 0.0001);
        assertFalse(fines.get(1).isPaid());
    }

    @Test
    void payFine_fullPayment_marksAllFinesPaid() {
        fineService.createFine("U1", 30.0);
        fineService.createFine("U1", 10.0);

        double newBalance = fineService.payFine("U1", 50.0);

        assertEquals(0.0, newBalance, 0.0001);

        List<Fine> fines = storage.loadFines();
        assertEquals(2, fines.size());
        assertTrue(fines.get(0).isPaid());
        assertTrue(fines.get(1).isPaid());
        assertEquals(0.0, fines.get(0).getAmount(), 0.0001);
        assertEquals(0.0, fines.get(1).getAmount(), 0.0001);
    }
}
