package com.library.infrastructure.mock;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock class to log sent notifications for testing (US3.1)
 * Tracks all notifications sent through the system for verification and testing
 *
 * @author Your Name
 * @version 1.0
 */
public class NotificationLogger {
    private static List<String> sentNotifications = new ArrayList<>();

    /**
     * Log an email notification (US3.1)
     * @param email recipient email address
     * @param message notification message content
     */
    public static void logEmail(String email, String message) {
        String logEntry = "Email to: " + email + " - Message: " + message;
        sentNotifications.add(logEntry);
        System.out.println("📝 Notification logged: " + logEntry);
    }

    /**
     * Get all sent notifications (useful for testing)
     * @return copy of sent notifications list
     */
    public static List<String> getSentNotifications() {
        return new ArrayList<>(sentNotifications);
    }

    /**
     * Clear all logged notifications (useful for testing)
     */
    public static void clearLog() {
        sentNotifications.clear();
        System.out.println("🗑️ Notification log cleared");
    }

    /**
     * Get total count of sent notifications
     * @return number of logged notifications
     */
    public static int getNotificationCount() {
        return sentNotifications.size();
    }

    /**
     * Display all logged notifications to console
     */
    public static void displayAllNotifications() {
        if (sentNotifications.isEmpty()) {
            System.out.println("ℹ️ No notifications sent yet.");
        } else {
            System.out.println("\n📋 Sent Notifications:");
            System.out.println("═".repeat(60));
            for (int i = 0; i < sentNotifications.size(); i++) {
                System.out.println((i + 1) + ". " + sentNotifications.get(i));
            }
            System.out.println("═".repeat(60));
            System.out.println("Total: " + sentNotifications.size() + " notifications\n");
        }
    }

    /**
     * Check if a specific notification was sent (useful for testing)
     * @param email recipient email
     * @param message message content (partial match)
     * @return true if notification was logged
     */
    public static boolean wasNotificationSent(String email, String message) {
        for (String notification : sentNotifications) {
            if (notification.contains(email) && notification.contains(message)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get notification count for specific email
     * @param email recipient email address
     * @return count of notifications sent to this email
     */
    public static int getNotificationCountForEmail(String email) {
        int count = 0;
        for (String notification : sentNotifications) {
            if (notification.contains("Email to: " + email)) {
                count++;
            }
        }
        return count;
    }
}