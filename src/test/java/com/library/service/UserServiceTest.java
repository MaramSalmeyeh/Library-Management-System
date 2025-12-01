package com.library.service;

import com.library.domain.FileStorage;
import com.library.domain.Fine;
import com.library.domain.Loan;
import com.library.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
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
        // نجهز ملفات DB فاضية
        Files.write(tempDir.resolve("admins.txt"), Collections.emptyList());
        Files.write(tempDir.resolve("librarians.txt"), Collections.emptyList());
        Files.write(tempDir.resolve("books.txt"), Collections.emptyList());
        Files.write(tempDir.resolve("users.txt"), Collections.emptyList());
        Files.write(tempDir.resolve("loans.txt"), Collections.emptyList());
        Files.write(tempDir.resolve("fines.txt"), Collections.emptyList());

        storage = new FileStorage(tempDir.toString());
        loanService = new LoanService(storage);
        fineService = new FineService(storage);
        userService = new UserService(storage);
    }

    @Test
    void register_createsNewUserAndPersistsToFile() {
        User u = userService.register("Aseel", "aseel@example.com", "pwd");

        assertNotNull(u);
        assertEquals("U1", u.getId());
        assertEquals("aseel@example.com", u.getEmail());

        List<User> fromFile = storage.loadUsers();
        assertEquals(1, fromFile.size());
        assertEquals("aseel@example.com", fromFile.get(0).getEmail());
    }

    @Test
    void register_withDuplicateEmail_throwsException() {
        // موجود مستخدم بنفس الايميل
        storage.saveUsers(List.of(
                new User("U1", "Old User", "aseel@example.com", "oldpwd")
        ));

        assertThrows(IllegalArgumentException.class,
                () -> userService.register("Aseel", "aseel@example.com", "pwd"));
    }

    @Test
    void login_returnsUserOnCorrectCredentials_elseNull() {
        storage.saveUsers(List.of(
                new User("U1", "Dana", "dana@example.com", "1234")
        ));

        User ok = userService.login("dana@example.com", "1234");
        assertNotNull(ok);
        assertEquals("U1", ok.getId());

        User wrongPass = userService.login("dana@example.com", "xxxx");
        assertNull(wrongPass);

        User unknown = userService.login("nope@example.com", "1234");
        assertNull(unknown);
    }

    @Test
    void unregisterUser_whenNoLoansAndNoFines_removesUserFromFile() {
        storage.saveUsers(List.of(
                new User("U1", "User1", "u1@example.com", "pwd")
        ));
        storage.saveLoans(Collections.emptyList());
        storage.saveFines(Collections.emptyList());

        userService.unregisterUser("U1", loanService, fineService);

        List<User> remaining = storage.loadUsers();
        assertTrue(remaining.isEmpty(), "User should be removed from users.txt");
    }

    @Test
    void unregisterUser_whenUserHasActiveLoans_throwsIllegalState() {
        storage.saveUsers(List.of(
                new User("U1", "User1", "u1@example.com", "pwd")
        ));

        LocalDate today = LocalDate.now();
        storage.saveLoans(List.of(
                new Loan("L1", "U1", "B1",
                        today.minusDays(1),
                        today.plusDays(7),
                        null) // active loan
        ));

        storage.saveFines(Collections.emptyList());

        assertThrows(IllegalStateException.class,
                () -> userService.unregisterUser("U1", loanService, fineService));

        // لسه موجود
        assertEquals(1, storage.loadUsers().size());
    }

    @Test
    void unregisterUser_whenUserHasUnpaidFines_throwsIllegalState() {
        storage.saveUsers(List.of(
                new User("U1", "User1", "u1@example.com", "pwd")
        ));
        storage.saveLoans(Collections.emptyList());

        storage.saveFines(List.of(
                new Fine("F1", "U1", 20.0, false)
        ));

        assertThrows(IllegalStateException.class,
                () -> userService.unregisterUser("U1", loanService, fineService));

        assertEquals(1, storage.loadUsers().size());
    }

    @Test
    void unregisterUser_whenUserDoesNotExist_throwsIllegalArgument() {
        storage.saveUsers(Collections.emptyList());
        storage.saveLoans(Collections.emptyList());
        storage.saveFines(Collections.emptyList());

        assertThrows(IllegalArgumentException.class,
                () -> userService.unregisterUser("UNKNOWN", loanService, fineService));
    }
}
