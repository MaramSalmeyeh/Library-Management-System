package com.library.service;

import com.library.domain.Loan;
import com.library.domain.User;

import java.util.List;

/**
 * Service responsible for sending reminder emails to users
 * who have overdue loans.
 * <p>
 * This class collaborates with:
 * <ul>
 *     <li>{@link LoanService} – to retrieve overdue loans</li>
 *     <li>{@link UserService} – to look up user information</li>
 *     <li>{@link EmailService} – to send the email notifications</li>
 * </ul>

 *
 * <p>
 * Each overdue loan generates exactly one email notice.
 * </p>
 *
 * @author Maram
 * @version 1.0
 */
public class ReminderService {

    /**
     * Service used to retrieve overdue loans.
     */
    private final LoanService loanService;

    /**
     * Service used to find user information such as email and name.
     */
    private final UserService userService;

    /**
     * Email sender used to send reminder messages.
     */
    private final EmailService emailService;

    /**
     * Creates a new ReminderService using the given dependencies.
     *
     * @param loanService  loan management service
     * @param userService  user management service
     * @param emailService email sending service
     */
    public ReminderService(LoanService loanService, UserService userService, EmailService emailService) {
        this.loanService = loanService;
        this.userService = userService;
        this.emailService = emailService;
    }

    /**
     * Sends reminder emails for all overdue loans.
     * <p>
     * Steps:
     * <ol>
     *     <li>Fetch all overdue loans</li>
     *     <li>Find the user for each overdue loan</li>
     *     <li>Send a personalized reminder email</li>
     *     <li>Count how many reminders were sent</li>
     * </ol>

     *
     * @return number of reminder emails successfully sent
     */
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
