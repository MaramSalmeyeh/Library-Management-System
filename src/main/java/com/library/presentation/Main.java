package com.library.presentation;

import com.library.domain.FileStorage;
import com.library.service.*;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {

    public static void main(String[] args) {


        FileStorage storage = new FileStorage("src/main/resources/DB");

        AuthService authService     = new AuthService(storage);
        BookService bookService     = new BookService(storage);
        LoanService loanService     = new LoanService(storage);
        FineService fineService     = new FineService(storage);
        UserService userService     = new UserService(storage);




        Dotenv dotenv = Dotenv.load();
        String email = dotenv.get("EMAIL_USERNAME");
        String appPassword = dotenv.get("EMAIL_PASSWORD");

        EmailService emailService = new EmailService(email, appPassword);

        // لازم يكون عندك ReminderService(LoanService, UserService, EmailService)
        ReminderService reminderService = new ReminderService(
                loanService,
                userService,
                emailService
        );

        // ConsoleMenu لازم يكون كونستركتوره:
        // (AuthService, UserService, BookService, LoanService, FineService, BorrowingService, ReminderService)
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
