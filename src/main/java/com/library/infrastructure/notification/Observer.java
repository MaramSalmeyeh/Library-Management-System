package com.library.infrastructure.notification;

import com.library.domain.model.User;

/**
 * Observer interface for notifications
 * @author Your Name
 * @version 1.0
 */
public interface Observer {
    /**
     * Send notification to user
     * @param user user to notify
     * @param message message to send
     */
    void notify(User user, String message);
}