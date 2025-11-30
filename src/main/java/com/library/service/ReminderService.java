package com.library.service;

import com.library.domain.Loan;
import com.library.domain.User;

import java.util.List;

public class ReminderService {

    private final LoanService loanService;
    private final UserService userService;
    private final EmailService emailService;

    public ReminderService(LoanService loanService, UserService userService, EmailService emailService) {
        this.loanService = loanService;
        this.userService = userService;
        this.emailService = emailService;
    }

    public int sendOverdueReminders() {

        List<Loan> overdue = loanService.getOverdueLoans();
        int count = 0;

        for (Loan loan : overdue) {

            User user = userService.findById(loan.getUserId());
            if (user == null) continue;

            String subject = "Library Overdue Book Reminder";
            String body =
                    "Dear " + user.getName() + ",\n\n" +
                            "This is a reminder that your loan (Book ID: " + loan.getBookId() + ") is overdue.\n" +
                            "Borrowed on: " + loan.getBorrowDate() + "\n" +
                            "Due on: " + loan.getDueDate() + "\n\n" +
                            "Please return the book as soon as possible.\n\n" +
                            "Best regards,\nLibrary System";

            emailService.sendEmail(user.getEmail(), subject, body);
            count++;
        }

        return count;
    }
}
