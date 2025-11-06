package com.library.domain.service;

import com.library.domain.model.Loan;
import com.library.domain.model.User;
import com.library.infrastructure.mock.NotificationException;
import com.library.infrastructure.notification.Observer;
import com.library.repository.LoanRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for sending reminder notifications to users (US3.1)
 * Implements Observer Pattern for flexible notification channels
 *
 * @author Your Name
 * @version 1.0
 */
public class ReminderService {
    private final LoanRepository loanRepository;
    private final List<Observer> observers;
    private int totalNotificationsSent;

    /**
     * Constructor with loan repository
     */
    public ReminderService(LoanRepository loanRepository) {
        this.loanRepository = Objects.requireNonNull(loanRepository, "LoanRepository cannot be null");
        this.observers = new ArrayList<>();
        this.totalNotificationsSent = 0;
    }

    /**
     * Add observer for notifications (Observer Pattern)
     * @param observer the notification observer to add
     */
    public void addObserver(Observer observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("✅ Observer added: " + observer.getType());
        }
    }

    /**
     * Remove observer from notifications
     * @param observer the observer to remove
     */
    public void removeObserver(Observer observer) {
        if (observer != null) {
            observers.remove(observer);
            System.out.println("🗑️ Observer removed: " + observer.getType());
        }
    }

    /**
     * Send overdue reminders to all users with overdue books (US3.1)
     * Message format: "You have n overdue book(s)."
     */
    public void sendOverdueReminders() {
        System.out.println("📧 Starting overdue reminder process...");

        if (observers.isEmpty()) {
            System.out.println("⚠️ No observers registered - notifications will not be sent");
            return;
        }

        // Group overdue loans by user
        Map<User, List<Loan>> overdueByUser = getOverdueLoansGroupedByUser();

        if (overdueByUser.isEmpty()) {
            System.out.println("✅ No overdue items found - no reminders needed");
            return;
        }

        int usersNotified = 0;
        int totalOverdueItems = 0;

        // Send reminder to each user
        for (Map.Entry<User, List<Loan>> entry : overdueByUser.entrySet()) {
            User user = entry.getKey();
            List<Loan> overdueLoans = entry.getValue();

            try {
                sendReminderToUser(user, overdueLoans);
                usersNotified++;
                totalOverdueItems += overdueLoans.size();
            } catch (Exception e) {
                System.out.println("❌ Failed to send reminder to " + user.getName() + ": " + e.getMessage());
            }
        }

        printReminderSummary(usersNotified, totalOverdueItems, overdueByUser.size());
    }

    /**
     * Send reminder to specific user with overdue items (US3.1)
     * @param user the user to notify
     */
    public void sendReminderToUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        List<Loan> overdueLoans = user.getActiveLoans().stream()
                .filter(Loan::isOverdue)
                .collect(Collectors.toList());

        if (overdueLoans.isEmpty()) {
            System.out.println("ℹ️ User " + user.getName() + " has no overdue items");
            return;
        }

        sendReminderToUser(user, overdueLoans);
    }

    /**
     * Internal method to send reminder with loan list
     */
    private void sendReminderToUser(User user, List<Loan> overdueLoans) {
        // Build message according to US3.1: "You have n overdue book(s)."
        String message = buildReminderMessage(overdueLoans.size());

        System.out.println("📤 Sending reminder to: " + user.getName());
        System.out.println("   📧 Email: " + user.getEmail());
        System.out.println("   📚 Overdue items: " + overdueLoans.size());

        // Notify through all registered observers
        for (Observer observer : observers) {
            try {
                if (observer.isActive()) {
                    observer.notify(user, message);
                    totalNotificationsSent++;
                } else {
                    System.out.println("⚠️ Observer " + observer.getType() + " is not active");
                }
            } catch (NotificationException e) {
                System.out.println("❌ Notification failed via " + observer.getType() + ": " + e.getMessage());
                throw e;
            }
        }
    }

    /**
     * Build reminder message according to US3.1 acceptance criteria
     * Format: "You have n overdue book(s)."
     */
    private String buildReminderMessage(int overdueCount) {
        return String.format("You have %d overdue book(s).", overdueCount);
    }

    /**
     * Group overdue loans by user
     */
    private Map<User, List<Loan>> getOverdueLoansGroupedByUser() {
        List<Loan> allActiveLoans = loanRepository.findActiveLoans();

        return allActiveLoans.stream()
                .filter(Loan::isOverdue)
                .collect(Collectors.groupingBy(Loan::getUser));
    }

    /**
     * Print summary of reminder operation
     */
    private void printReminderSummary(int usersNotified, int totalOverdueItems, int totalUsersWithOverdue) {
        System.out.println("\n📊 Reminder Summary:");
        System.out.println("   👥 Users notified: " + usersNotified + "/" + totalUsersWithOverdue);
        System.out.println("   📚 Total overdue items: " + totalOverdueItems);
        System.out.println("   📧 Notifications sent: " + totalNotificationsSent);
        System.out.println("   📡 Active observers: " + getActiveObserverCount());
        System.out.println("✅ Reminder process completed\n");
    }

    /**
     * Get count of active observers
     */
    public int getActiveObserverCount() {
        return (int) observers.stream()
                .filter(Observer::isActive)
                .count();
    }

    /**
     * Get total notifications sent
     */
    public int getTotalNotificationsSent() {
        return totalNotificationsSent;
    }

    /**
     * Reset notification counter (useful for testing)
     */
    public void resetNotificationCount() {
        totalNotificationsSent = 0;
    }

    /**
     * Get list of registered observers
     */
    public List<Observer> getObservers() {
        return new ArrayList<>(observers);
    }

    /**
     * Check if service has any active observers
     */
    public boolean hasActiveObservers() {
        return observers.stream().anyMatch(Observer::isActive);
    }

    /**
     * Get overdue statistics
     */
    public Map<String, Object> getOverdueStatistics() {
        Map<User, List<Loan>> overdueByUser = getOverdueLoansGroupedByUser();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsersWithOverdue", overdueByUser.size());
        stats.put("totalOverdueItems", overdueByUser.values().stream()
                .mapToInt(List::size)
                .sum());
        stats.put("activeObservers", getActiveObserverCount());
        stats.put("totalNotificationsSent", totalNotificationsSent);

        return stats;
    }
}