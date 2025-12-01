package com.library.service;

import com.library.domain.FileStorage;
import com.library.domain.Loan;
import com.library.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @TempDir
    Path tempDir;

    private FileStorage storage;
    private UserService userService;
    private LoanService loanService;
    private FineService fineService;

    @BeforeEach
    void setUp() throws IOException {
        // ملفات DB الأساسية
        Files.write(tempDir.resolve("admins.txt"), List.of());
        Files.write(tempDir.resolve("librarians.txt"), List.of());
        Files.write(tempDir.resolve("users.txt"), List.of());
        Files.write(tempDir.resolve("loans.txt"), List.of());
        Files.write(tempDir.resolve("fines.txt"), List.of());
        Files.write(tempDir.resolve("books.txt"), List.of()); // حتى لو مش مستخدم هون

        storage = new FileStorage(tempDir.toString());
        userService = new UserService(storage);
        loanService = new LoanService(storage);
        fineService = new FineService(storage);
    }

    // ✅ عنده loans فعّالة → ممنوع unregister
    @Test
    void unregisterUser_whenUserHasActiveLoans_throwsIllegalState() {
        User u1 = userService.register("User1", "u1@example.com", "pass");

        LocalDate today = LocalDate.now();
        List<Loan> loans = new ArrayList<>();
        loans.add(new Loan(
                "L1",
                u1.getId(),
                "B1",
                today.minusDays(1),
                today.plusDays(27), // لسه مش متأخر
                null                // not returned → active
        ));
        storage.saveLoans(loans);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> userService.unregisterUser(u1.getId(), loanService, fineService)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("active"),
                "Message should mention active loans");

        // لسه موجود في users.txt
        assertEquals(1, storage.loadUsers().size());
    }

    // ✅ عنده غرامات غير مدفوعة → ممنوع unregister
    @Test
    void unregisterUser_whenUserHasUnpaidFines_throwsIllegalState() {
        User u1 = userService.register("User1", "u1@example.com", "pass");

        fineService.createFine(u1.getId(), 50.0); // غرامة غير مدفوعة

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> userService.unregisterUser(u1.getId(), loanService, fineService)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("unpaid"),
                "Message should mention unpaid fines");

        assertEquals(1, storage.loadUsers().size());
    }

    // ✅ لو ال id مش موجود أصلاً → IllegalArgumentException
    @Test
    void unregisterUser_whenUserDoesNotExist_throwsIllegalArgument() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.unregisterUser("UNKNOWN", loanService, fineService)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("not found"));
    }

    // ✅ لا loans ولا fines → ينحذف من الملف بنجاح
    @Test
    void unregisterUser_whenNoLoansAndNoFines_removesUserFromFile() {
        User u1 = userService.register("User1", "u1@example.com", "pass");

        assertEquals(1, storage.loadUsers().size());

        userService.unregisterUser(u1.getId(), loanService, fineService);

        List<User> usersAfter = storage.loadUsers();
        assertEquals(0, usersAfter.size(), "User should be removed from users.txt");
    }
}
