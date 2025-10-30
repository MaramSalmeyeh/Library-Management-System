package com.library.infrastructure.notification;

import com.library.domain.model.User;

/**
 * Email notification implementation (US3.1)
 * @author Your Name
 * @version 1.0
 */
public class EmailNotifier implements Observer {
    @Override
    public void notify(User user, String message) {
        // In real implementation, this would send actual email
        System.out.println("📧 EMAIL NOTIFICATION:");
        System.out.println("   To: " + user.getEmail());
        System.out.println("   User: " + user.getName());
        System.out.println("   Message: " + message);
        System.out.println("   ---");

        // Record for testing
        NotificationLogger.logEmail(user.getEmail(), message);
    }
}