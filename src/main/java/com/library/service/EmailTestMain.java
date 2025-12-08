/*package com.library.service;

import io.github.cdimascio.dotenv.Dotenv;
import com.library.service.EmailService;
*/
/*
/**
 * A simple test class used to verify that the {@link EmailService}
 * is working correctly by sending a real email.
 * <p>
 * This class loads email credentials from the <b>.env</b> file using
 * {@link Dotenv}, constructs an EmailService instance, and sends
 * a test message to the specified email address.
 * </p>
 *
 * <p>
 * This class is intended ONLY for manual testing and is not used
 * by the main application.
 * </p>
 *
 * Required Environment Variables:

 * 1. EMAIL_USERNAME – your Gmail address
 * 2. EMAIL_PASSWORD – your app password


 * <ul>
 *     <li><b>EMAIL_USERNAME</b> — the Gmail address</li>
 *     <li><b>EMAIL_PASSWORD</b> — the App Password (16-digit code)</li>
 * </ul>
 *
 * <p><b>Warning:</b> Never use your real Gmail password.
 * Google requires an App Password for SMTP.</p>
 *
 * @author Maram
 * @version 1.0
 */
/*
public class EmailTestMain {

    /**
     * Sends a test email to verify SMTP connectivity.
     *
     * @param args command line arguments (unused)
     */
/*
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String username = dotenv.get("EMAIL_USERNAME");
        String password = dotenv.get("EMAIL_PASSWORD");

        EmailService emailService = new EmailService(username, password);

        String to = "aseelqedan@gmail.com";
        String subject = "LMS Email Test";
        String body = "Hello from Library Management System.";

        emailService.sendEmail(to, subject, body);
    }
}
*/