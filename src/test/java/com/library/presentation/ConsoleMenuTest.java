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
}
