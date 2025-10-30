package com.library.infrastructure.notification;

import com.library.domain.model.User;

/**
 * Email notification implementation
 * @author Your Name
 * @version 1.0
 */
public class EmailNotifier implements Observer {
    @Override
    public void notify(User user, String message) {
        // Mock implementation - in real app, this would send actual email
        System.out.println("📧 Sending email to: " + user.getEmail());
        System.out.println("📝 Message: " + message);
        // Record for testing
        NotificationLogger.logEmail(user.getEmail(), message);
    }
}