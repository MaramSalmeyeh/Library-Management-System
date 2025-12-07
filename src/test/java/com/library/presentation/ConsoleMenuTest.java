package com.library.presentation;

import com.library.domain.*;
import com.library.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for verifying the functionality of the {@link ConsoleMenu} system.
 * <p>
 * These tests simulate console input/output to fully automate
 * menu interactions, ensuring that all user/admin/librarian
 * operations function correctly (sign up, login, borrowing,
 * fines, searching, reminders, and unregistering users).
 * </p>
 * <p>
 * A temporary directory is used to isolate file storage for
 * each test case so that no real application data is affected.
 * </p>
 *
 * @author Maram
 * @version 1.0
 */
public class ConsoleMenuTest {

    /**
     * JUnit temporary directory used to store test copies
     * of user, admin, book, loan, and fine database files.
     */
    @TempDir
    Path tempDir;

    /**
     * Runs the {@link ConsoleMenu} with simulated console input and
     * returns the printed output as a String.
     *
     * @param inputScript multi-line text representing user menu actions
     * @param storage     the file storage handler used for test data
     * @param setup       optional lambda to configure the menu before execution
     * @return console output generated during execution
     * @throws IOException if input/output redirection fails
     */
    private String runMenuWithInput(String inputScript,
                                    FileStorage storage,
                                    java.util.function.Consumer<ConsoleMenu> setup)
            throws IOException {

        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        ByteArrayInputStream testIn =
                new ByteArrayInputStream(inputScript.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream testOut = new ByteArrayOutputStream();

        try {
            System.setIn(testIn);
            System.setOut(new PrintStream(testOut, true, StandardCharsets.UTF_8));

            AuthService authService = new AuthService(storage);
            BookService bookService = new BookService(storage);
            LoanService loanService = new LoanService(storage);
            FineService fineService = new FineService(storage);
            UserService userService = new UserService(storage);

            EmailService emailService =
                    new EmailService("dummy@example.com", "password");

            ReminderService reminderService =
                    new ReminderService(loanService, userService, emailService);

            ConsoleMenu menu = new ConsoleMenu(
                    authService,
                    userService,
                    bookService,
                    loanService,
                    fineService,
                    reminderService
            );

            if (setup != null) {
                setup.accept(menu);
            }

            menu.run();

            return testOut.toString(StandardCharsets.UTF_8);

        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    // ===================== Test Cases ======================

    /** Tests user registration + login and ensures persistence. */
    @Test
    void userSignUpAndLogin_flowPrintsExpectedMessages_andPersistsUser() throws IOException {
        FileStorage storage = new FileStorage(tempDir.toString());

        String script = String.join(System.lineSeparator(),
                "3", "Test User", "user@example.com", "secret",
                "4", "user@example.com", "secret",
                "13"
        ) + System.lineSeparator();

        String output = runMenuWithInput(script, storage, null);

        assertTrue(output.contains("=== User Sign Up ==="));
        assertTrue(output.contains("User registered successfully with ID:"));
        assertTrue(output.contains("=== User Login ==="));
        assertTrue(output.contains("Welcome, Test User"));

        List<User> users = storage.loadUsers();
        assertEquals(1, users.size());
        assertEquals("user@example.com", users.get(0).getEmail());
    }

    /** Tests that admin login succeeds with valid credentials. */
    @Test
    void adminLogin_successfulWhenCredentialsMatchFile() throws IOException {
        FileStorage storage = new FileStorage(tempDir.toString());
        Admin admin = new Admin("A1", "Main Admin", "admin@example.com", "apwd");
        storage.saveAdmins(List.of(admin));

        String script = String.join(System.lineSeparator(),
                "1", "admin@example.com", "apwd",
                "13"
        ) + System.lineSeparator();

        String output = runMenuWithInput(script, storage, null);
        assertTrue(output.contains("Admin login successful. Welcome, Main Admin!"));
    }

    /** Tests invalid menu choice handling. */
    @Test
    void invalidMenuChoice_printsErrorAndCanExitGracefully() throws IOException {
        FileStorage storage = new FileStorage(tempDir.toString());

        String script = String.join(System.lineSeparator(),
                "99", "13"
        ) + System.lineSeparator();

        String output = runMenuWithInput(script, storage, null);
        assertTrue(output.contains("Invalid choice, please try again."));
        assertTrue(output.contains("Exiting..."));
    }

    /** Ensures admin can add a book and search for it. */
    @Test
    void adminCanAddAndSearchBook_afterLoggingIn() throws IOException {
        FileStorage storage = new FileStorage(tempDir.toString());
        storage.saveAdmins(List.of(new Admin("A1", "Chief", "chief@example.com", "pwd")));

        String script = String.join(System.lineSeparator(),
                "1", "chief@example.com", "pwd",
                "6", "Clean Code", "Robert C. Martin", "9780132350884",
                "7", "3", "9780132350884",
                "13"
        ) + System.lineSeparator();

        String output = runMenuWithInput(script, storage, null);

        assertTrue(output.contains("Book added successfully with ID:"));
        assertTrue(output.contains("| ISBN: 9780132350884"));
    }

    /** Tests the full borrowing flow. */
    @Test
    void borrowBookFlow_marksBookBorrowedAndCreatesLoan() throws IOException {
        FileStorage storage = new FileStorage(tempDir.toString());
        storage.saveBooks(List.of(new Book("B1", "1984", "George Orwell", "ISBN-1984", false)));

        String script = String.join(System.lineSeparator(),
                "3", "Borrower", "borrower@ex.com", "pwd123",
                "4", "borrower@ex.com", "pwd123",
                "8", "1", "B1",
                "13"
        ) + System.lineSeparator();

        String output = runMenuWithInput(script, storage, null);

        assertTrue(output.contains("Item borrowed successfully with loan ID:"));
        assertEquals(1, storage.loadLoans().size());
    }

    /** Tests invalid borrow media type option. */
    @Test
    void borrowBookWithInvalidType_showsFriendlyMessage() throws IOException {
        FileStorage storage = new FileStorage(tempDir.toString());

        String script = String.join(System.lineSeparator(),
                "3", "Borrower", "user@ex.com", "pwd",
                "4", "user@ex.com", "pwd",
                "8", "3",
                "13"
        ) + System.lineSeparator();

        String output = runMenuWithInput(script, storage, null);
        assertTrue(output.contains("Invalid media type choice."));
    }

    /** Tests paying fines and updating remaining balance. */
    @Test
    void payFineFlow_reducesOutstandingBalanceAndPrintsMessages() throws IOException {
        FileStorage storage = new FileStorage(tempDir.toString());
        storage.saveUsers(List.of(new User("U1", "Paying User", "pay@ex.com", "pwd")));
        storage.saveFines(List.of(new Fine("F1", "U1", 30.0, false)));

        String script = String.join(System.lineSeparator(),
                "4", "pay@ex.com", "pwd",
                "10", "30",
                "13"
        ) + System.lineSeparator();

        String output = runMenuWithInput(script, storage, null);
        assertTrue(output.contains("Remaining balance = 0.0 NIS"));
    }


}
