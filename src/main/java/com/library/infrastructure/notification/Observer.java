package com.library.infrastructure.notification;

import com.library.domain.model.User;
import com.library.infrastructure.mock.NotificationException;

/**
 * Observer interface for notifications (US3.1)
 * Defines contract for all notification observers in the system
 *
 * @author Your Name
 * @version 1.0
 */
public interface Observer {

    /**
     * Send notification to user with a specific message
     *
     * @param user the user to receive the notification (cannot be null)
     * @param message the message content to send (cannot be null or empty)
     * @throws IllegalArgumentException if user is null or message is invalid
     * @throws NotificationException if notification delivery fails
     */
    void notify(User user, String message);

    /**
     * Get the type of this observer (e.g., "EMAIL", "SMS", "PUSH")
     * Useful for identifying and managing different notification channels
     *
     * @return the observer type as string
     */
    String getType();

    /**
     * Check if this observer is currently active and able to send notifications
     *
     * @return true if observer is active and ready to send notifications
     */
    boolean isActive();

    /**
     * Optional: Initialize the observer with any required setup
     * Can be used for connection pooling, configuration loading, etc.
     */
    default void initialize() {
        // Default implementation - can be overridden by concrete classes
    }

    /**
     * Optional: Cleanup resources when observer is no longer needed
     */
    default void shutdown() {
        // Default implementation - can be overridden by concrete classes
    }
}

