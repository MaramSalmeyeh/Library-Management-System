
package com.library.service;

import io.github.cdimascio.dotenv.Dotenv;
import com.library.service.EmailService;

public class EmailTestMain {
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
