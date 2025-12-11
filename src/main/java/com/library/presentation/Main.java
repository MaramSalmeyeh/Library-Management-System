package com.library.presentation;

import com.library.domain.FileStorage;
import com.library.service.*;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Entry point of the Library Management System application.
 * <p>
 * This class initializes all services, loads environment variables
 * for email configuration, and starts the {@link ConsoleMenu}.
 * </p>
 *
 * <p>
 * The main responsibilities of this class:
 * <ul>
 *     <li>Create the FileStorage instance</li>
 *     <li>Initialize all service classes</li>
 *     <li>Load email credentials from environment variables</li>
 *     <li>Set up the reminder system</li>
 *     <li>Launch the console-based menu interface</li>
 * </ul>

 *
 * @author Maram
 * @version 1.0
 */

public class Main {

    /**
     * Application starting point.
     * <p>
     * Sets up services, environment variables, and launches
     * the interactive console menu.
     * </p>
     *
     * @param args command-line arguments (unused)
     */

    public static void main(String[] args) {

        FileStorage storage = new FileStorage("src/main/resources/DB");

        AuthService authService     = new AuthService(storage);
        BookService bookService     = new BookService(storage);
        LoanService loanService     = new LoanService(storage);
        FineService fineService     = new FineService(storage);
        UserService userService     = new UserService(storage);

        // Load email credentials from .env
        Dotenv dotenv = Dotenv.load();
        String email = dotenv.get("EMAIL_USERNAME");
        String appPassword = dotenv.get("EMAIL_PASSWORD");

        EmailService emailService = new EmailService(email, appPassword);

        // Create reminder service
        ReminderService reminderService = new ReminderService(
                loanService,
                userService,
                emailService
        );

        // Initialize console menu and start application
        ConsoleMenu menu = new ConsoleMenu(
                authService,
                userService,
                bookService,
                loanService,
                fineService,
                reminderService
        );

        menu.run();
    }
}