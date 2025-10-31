package com.library.infrastructure.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock class to log sent notifications for testing (US3.1)
 * @author Your Name
 * @version 1.0
 */
public class NotificationLogger {
    private static List<String> sentNotifications = new ArrayList<>();

    public static void logEmail(String email, String message) {
        String logEntry = "Email to: " + email + " - Message: " + message;
        sentNotifications.add(logEntry);
        System.out.println(" Notification logged: " + logEntry);
    }

    public static List<String> getSentNotifications() {
        return new ArrayList<>(sentNotifications);
    }

    public static void clearLog() {
        sentNotifications.clear();
    }

    public static int getNotificationCount() {
        return sentNotifications.size();
    }

    public static void displayAllNotifications() {
        if (sentNotifications.isEmpty()) {
            System.out.println("No notifications sent yet.");
        } else {
            System.out.println(" Sent Notifications:");
            for (int i = 0; i < sentNotifications.size(); i++) {
                System.out.println((i + 1) + ". " + sentNotifications.get(i));
            }
        }
    }
}