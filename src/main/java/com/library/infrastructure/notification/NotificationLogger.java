package com.library.infrastructure.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock class to log sent notifications for testing
 * @author Your Name
 * @version 1.0
 */
public class NotificationLogger {
    private static List<String> sentEmails = new ArrayList<>();

    public static void logEmail(String email, String message) {
        String logEntry = "To: " + email + " - Message: " + message;
        sentEmails.add(logEntry);
        System.out.println("📋 Logged: " + logEntry);
    }

    public static List<String> getSentEmails() {
        return new ArrayList<>(sentEmails);
    }

    public static void clearLog() {
        sentEmails.clear();
    }

    public static int getEmailCount() {
        return sentEmails.size();
    }
}