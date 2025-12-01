package com.library.presentation;

import com.library.domain.Admin;
import com.library.domain.FileStorage;
import com.library.domain.User;
import com.library.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ConsoleMenu:
 *  - user sign up + login
 *  - admin login
 *  - invalid choice + graceful exit
 */
public class ConsoleMenuTest {

    @TempDir
    Path tempDir;

    /**
     * Helper to run the menu with scripted input and capture output.
     */
    private String runMenuWithInput(String inputScript, FileStorage storage,
                                    java.util.function.Consumer<ConsoleMenu> setup) throws IOException {
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

            // dummy email service – we won't actually send real emails
            EmailService emailService = new EmailService("dummy@example.com", "password");
            ReminderService reminderService = new ReminderService(loanService, userService, emailService);

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

            // runs until choice 13 (Exit) is read
            menu.run();

            return testOut.toString(StandardCharsets.UTF_8);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    @Test
    void userSignUpAndLogin_flowPrintsExpectedMessages_andPersistsUser() throws IOException {
        FileStorage storage = new FileStorage(tempDir.toString());

        String script = String.join(System.lineSeparator(),
                // 3 = user sign up
                "3",
                "Test User",              // name
                "user@example.com",       // email
                "secret",                 // password
                // 4 = user login
                "4",
                "user@example.com",
                "secret",
                // 13 = exit
                "13"
        ) + System.lineSeparator();

        String output = runMenuWithInput(script, storage, null);

        // user created and logged in
        assertTrue(output.contains("=== User Sign Up ==="));
        assertTrue(output.contains("User registered successfully with ID:"));
        assertTrue(output.contains("=== User Login ==="));
        assertTrue(output.contains("Welcome, Test User"));

        List<User> users = storage.loadUsers();
        assertEquals(1, users.size());
        assertEquals("user@example.com", users.get(0).getEmail());
    }

    @Test
    void adminLogin_successfulWhenCredentialsMatchFile() throws IOException {
        FileStorage storage = new FileStorage(tempDir.toString());

        // prepare one admin in admins.txt
        Admin admin = new Admin("A1", "Main Admin", "admin@example.com", "apwd");
        storage.saveAdmins(List.of(admin));

        String script = String.join(System.lineSeparator(),
                // 1 = admin login
                "1",
                "admin@example.com",
                "apwd",
                // 13 = exit
                "13"
        ) + System.lineSeparator();

        String output = runMenuWithInput(script, storage, null);

        assertTrue(output.contains("Admin login successful. Welcome, Main Admin!"));
    }

    @Test
    void invalidMenuChoice_printsErrorAndCanExitGracefully() throws IOException {
        FileStorage storage = new FileStorage(tempDir.toString());

        String script = String.join(System.lineSeparator(),
                "99",   // invalid choice
                "13"    // exit
        ) + System.lineSeparator();

        String output = runMenuWithInput(script, storage, null);

        assertTrue(output.contains("Invalid choice, please try again."));
        assertTrue(output.contains("Exiting..."));
    }

    @Test
    void adminCanAddAndSearchBook_afterLoggingIn() throws IOException {
        FileStorage storage = new FileStorage(tempDir.toString());
        storage.saveAdmins(List.of(new Admin("A1", "Chief", "chief@example.com", "pwd")));

        String script = String.join(System.lineSeparator(),
                "1",                     // admin login
                "chief@example.com",
                "pwd",
                "6",                     // add book
                "Clean Code",            // title
                "Robert C. Martin",      // author
                "9780132350884",         // isbn
                "7",                     // search book
                "3",                     // search by isbn
                "9780132350884",
                "13"                    // exit
        ) + System.lineSeparator();

        String output = runMenuWithInput(script, storage, null);

        assertTrue(output.contains("Book added successfully with ID:"));
        assertTrue(output.contains("| ISBN: 9780132350884"));

        List<com.library.domain.Book> books = storage.loadBooks();
        assertEquals(1, books.size());
        assertEquals("Clean Code", books.get(0).getTitle());
    }

    @Test
    void borrowBookFlow_marksBookBorrowedAndCreatesLoan() throws IOException {
        FileStorage storage = new FileStorage(tempDir.toString());
        storage.saveBooks(List.of(new com.library.domain.Book("B1", "1984", "George Orwell", "ISBN-1984", false)));

        String script = String.join(System.lineSeparator(),
                "3",               // sign up
                "Borrower",        // name
                "borrower@ex.com", // email
                "pwd123",          // password
                "4",               // login
                "borrower@ex.com",
                "pwd123",
                "8",               // borrow book
                "1",               // media type book
                "B1",              // book id
                "13"               // exit
        ) + System.lineSeparator();

        String output = runMenuWithInput(script, storage, null);

        assertTrue(output.contains("Item borrowed successfully with loan ID:"));

        List<com.library.domain.Book> books = storage.loadBooks();
        assertTrue(books.get(0).isBorrowed());

        assertEquals(1, storage.loadLoans().size());
    }

    @Test
    void borrowBookWithInvalidType_showsFriendlyMessage() throws IOException {
        FileStorage storage = new FileStorage(tempDir.toString());

        String script = String.join(System.lineSeparator(),
                "3",          // sign up
                "Borrower",
                "user@ex.com",
                "pwd",
                "4",          // login
                "user@ex.com",
                "pwd",
                "8",          // borrow menu
                "3",          // invalid media type
                "13"          // exit
        ) + System.lineSeparator();

        String output = runMenuWithInput(script, storage, null);

        assertTrue(output.contains("Invalid media type choice."));
    }

    @Test
    void payFineFlow_reducesOutstandingBalanceAndPrintsMessages() throws IOException {
        FileStorage storage = new FileStorage(tempDir.toString());
        storage.saveUsers(List.of(new User("U1", "Paying User", "pay@ex.com", "pwd")));
        storage.saveFines(List.of(
                new com.library.domain.Fine("F1", "U1", 30.0, false)
        ));

        String script = String.join(System.lineSeparator(),
                "4",             // login
                "pay@ex.com",
                "pwd",
                "10",            // pay fine
                "30",            // amount
                "13"             // exit
        ) + System.lineSeparator();

        String output = runMenuWithInput(script, storage, null);

        assertTrue(output.contains("Your current outstanding fines.txt = 30.0 NIS"));
        assertTrue(output.contains("Remaining balance = 0.0 NIS"));

        assertEquals(0.0, storage.loadFines().get(0).getAmount());
    }

    @Test
    void sendRemindersWithoutAdminLogin_showsGuardMessage() throws IOException {
        FileStorage storage = new FileStorage(tempDir.toString());

        String script = String.join(System.lineSeparator(),
                "11",   // attempt to send reminders
                "13"    // exit
        ) + System.lineSeparator();

        String output = runMenuWithInput(script, storage, null);

        assertTrue(output.contains("You must login as admin to send overdue reminders."));
    }


}
