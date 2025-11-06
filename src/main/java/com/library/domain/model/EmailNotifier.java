package com.library.domain.model;

import com.library.infrastructure.mock.NotificationException;
import com.library.infrastructure.mock.NotificationLogger;
import com.library.infrastructure.notification.Observer;

/**
 * Email notification implementation (US3.1)
 * Mock implementation that simulates email sending for testing and development
 *
 * @author Your Name
 * @version 2.0
 */
public class EmailNotifier implements Observer {
    private boolean isActive = true;
    private int notificationCount = 0;
    private final String notifierId;

    /**
     * Default constructor
     */
    public EmailNotifier() {
        this.notifierId = "EMAIL-" + System.currentTimeMillis();
        System.out.println("✅ EmailNotifier initialized: " + notifierId);
    }

    /**
     * Constructor with custom ID for testing
     */
    public EmailNotifier(String notifierId) {
        this.notifierId = notifierId;
        System.out.println("✅ EmailNotifier initialized: " + notifierId);
    }

    @Override
    public void notify(User user, String message) {
        // Input validation
        validateInputs(user, message);

        try {
            // Simulate email sending process
            simulateEmailSending(user, message);

            // Record for testing and analytics
            recordNotification(user, message);

            System.out.println("✅ Email notification processed successfully");

        } catch (NotificationException e) {
            System.out.println("❌ Email notification failed: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("❌ Unexpected error in email notification: " + e.getMessage());
            throw new NotificationException("Failed to send email notification", e);
        }
    }

    @Override
    public String getType() {
        return "EMAIL";
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void initialize() {
        System.out.println("🔄 Initializing EmailNotifier...");
        // Simulate initialization process (e.g., connecting to SMTP server)
        simulateConnectionSetup();
        isActive = true;
        System.out.println("✅ EmailNotifier initialization completed");
    }

    @Override
    public void shutdown() {
        System.out.println("🔄 Shutting down EmailNotifier...");
        // Simulate cleanup process
        simulateCleanup();
        isActive = false;
        notificationCount = 0;
        System.out.println("✅ EmailNotifier shutdown completed");
    }

    /**
     * Validate input parameters
     */
    private void validateInputs(User user, String message) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null for email notification");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty for email notification");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be null or empty");
        }

        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format: " + user.getEmail());
        }
    }

    /**
     * Basic email format validation
     */
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    /**
     * Simulate email sending process
     */
    private void simulateEmailSending(User user, String message) {
        System.out.println("📧 SIMULATING EMAIL DELIVERY:");
        System.out.println("   🔑 Notifier ID: " + notifierId);
        System.out.println("   📨 To: " + user.getEmail());
        System.out.println("   👤 User: " + user.getName());
        System.out.println("   🆔 User ID: " + user.getId());
        System.out.println("   📝 Subject: Library Overdue Notice");
        System.out.println("   💬 Message: " + message);
        System.out.println("   📊 Total notifications sent: " + (notificationCount + 1));
        System.out.println("   ⏰ Timestamp: " + java.time.LocalDateTime.now());
        System.out.println("   " + "─".repeat(50));

        // Simulate network delay
        simulateNetworkDelay();

        // Simulate random failures for testing (1% failure rate)
        if (Math.random() < 0.01) {
            throw new NotificationException("Simulated email server timeout");
        }
    }

    /**
     * Simulate network delay (0-100ms)
     */
    private void simulateNetworkDelay() {
        try {
            Thread.sleep((long) (Math.random() * 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NotificationException("Email sending interrupted", e);
        }
    }

    /**
     * Simulate connection setup process
     */
    private void simulateConnectionSetup() {
        try {
            System.out.println("🔗 Connecting to SMTP server...");
            Thread.sleep(200); // Simulate connection time
            System.out.println("✅ SMTP connection established");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NotificationException("Connection setup interrupted", e);
        }
    }

    /**
     * Simulate cleanup process
     */
    private void simulateCleanup() {
        try {
            System.out.println("🔌 Disconnecting from SMTP server...");
            Thread.sleep(100); // Simulate disconnection time
            System.out.println("✅ SMTP connection closed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NotificationException("Cleanup interrupted", e);
        }
    }

    /**
     * Record notification for testing and analytics
     */
    private void recordNotification(User user, String message) {
        notificationCount++;

        // Record for testing
        NotificationLogger.logEmail(user.getEmail(), message);

        // Additional logging for analytics
        System.out.println("📈 Notification Analytics:");
        System.out.println("   📧 Email: " + user.getEmail());
        System.out.println("   👤 User: " + user.getName());
        System.out.println("   🔢 Notification #: " + notificationCount);
        System.out.println("   📅 Date: " + java.time.LocalDate.now());
    }

    /**
     * Get notification count for monitoring
     */
    public int getNotificationCount() {
        return notificationCount;
    }

    /**
     * Get notifier ID for identification
     */
    public String getNotifierId() {
        return notifierId;
    }

    /**
     * Simulate temporary outage for testing
     */
    public void simulateOutage() {
        System.out.println("🚨 Simulating email notifier outage...");
        isActive = false;
    }

    /**
     * Restore from simulated outage
     */
    public void restoreService() {
        System.out.println("🔧 Restoring email notifier service...");
        isActive = true;
    }

    /**
     * Reset notification count (useful for testing)
     */
    public void resetCount() {
        System.out.println("🔄 Resetting notification count...");
        notificationCount = 0;
    }

    /**
     * Get notifier status summary
     */
    public String getStatus() {
        return String.format(
                "EmailNotifier[%s] - Active: %s, Notifications: %d",
                notifierId, isActive, notificationCount
        );
    }
}