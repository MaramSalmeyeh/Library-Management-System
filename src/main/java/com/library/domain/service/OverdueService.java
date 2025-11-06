package com.library.domain.service;

import com.library.domain.model.Loan;
import com.library.domain.model.User;
import com.library.infrastructure.notification.Observer;
import com.library.infrastructure.mock.NotificationException;
import com.library.repository.LoanRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Service for automatic overdue detection and processing (US2.2) and notifications (US3.1)
 * Handles overdue loan detection, fine application, and user notifications
 *
 * @author Your Name
 * @version 2.0
 */
public class OverdueService {
    private final LoanRepository loanRepository;
    private final Observer notifier;
    private final ScheduledExecutorService scheduler;
    private boolean isScannerRunning = false;
    private final Map<String, LocalDate> lastFineApplication; // Track fine application dates

    // Configuration constants
    private static final int SCANNER_THREAD_POOL_SIZE = 1;
    private static final long SCAN_INTERVAL_HOURS = 24;
    private static final int TARGET_SCAN_HOUR = 8; // 8:00 AM

    public OverdueService(LoanRepository loanRepository, Observer notifier) {
        this.loanRepository = Objects.requireNonNull(loanRepository, "LoanRepository cannot be null");
        this.notifier = Objects.requireNonNull(notifier, "Observer cannot be null");
        this.scheduler = Executors.newScheduledThreadPool(SCANNER_THREAD_POOL_SIZE);
        this.lastFineApplication = new HashMap<>();

        initializeNotifier();
    }

    /**
     * Initialize the notification observer
     */
    private void initializeNotifier() {
        try {
            notifier.initialize();
            System.out.println("✅ Notifier initialized: " + notifier.getType());
        } catch (Exception e) {
            System.out.println("⚠️  Failed to initialize notifier: " + e.getMessage());
        }
    }

    /**
     * Start automatic overdue scanning (runs daily at specified time)
     */
    public void startAutomaticOverdueScanning() {
        if (isScannerRunning) {
            System.out.println("⚠️ Overdue scanner is already running");
            return;
        }

        if (!notifier.isActive()) {
            System.out.println("❌ Cannot start scanner - notifier is not active");
            return;
        }

        System.out.println("🚀 Starting automatic overdue scanner...");
        isScannerRunning = true;

        long initialDelay = calculateInitialDelay();
        long period = TimeUnit.HOURS.toMillis(SCAN_INTERVAL_HOURS);

        scheduler.scheduleAtFixedRate(this::scheduledScan, initialDelay, period, TimeUnit.MILLISECONDS);

        System.out.println("✅ Automatic overdue scanner started successfully");
        System.out.println("📅 Next scan in: " + (initialDelay / (1000 * 60 * 60)) + " hours");
        System.out.println("🔔 Notifier type: " + notifier.getType());
    }

    /**
     * Scheduled scan method with proper error handling
     */
    private void scheduledScan() {
        try {
            System.out.println("🕐 Scheduled overdue scan started at: " + LocalDateTime.now());
            checkAndProcessOverdueItems();
            System.out.println("✅ Scheduled overdue scan completed");
        } catch (Exception e) {
            System.out.println("❌ Error in scheduled overdue scan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Stop automatic overdue scanning
     */
    public void stopAutomaticOverdueScanning() {
        if (!isScannerRunning) {
            System.out.println("⚠️ Overdue scanner is not running");
            return;
        }

        System.out.println("🛑 Stopping automatic overdue scanner...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        isScannerRunning = false;
        System.out.println("✅ Automatic overdue scanner stopped");
    }

    /**
     * Manual trigger for overdue detection (US2.2)
     */
    public void checkAndProcessOverdueItems() {
        System.out.println("🔍 Scanning for overdue items...");

        List<Loan> activeLoans = loanRepository.findActiveLoans();
        int totalProcessed = 0;
        int overdueFound = 0;
        int notificationsSent = 0;

        for (Loan loan : activeLoans) {
            try {
                if (loan.isOverdue()) {
                    processOverdueLoan(loan);
                    overdueFound++;
                    notificationsSent++;
                }
                totalProcessed++;
            } catch (Exception e) {
                System.out.println("❌ Error processing loan " + loan.getId() + ": " + e.getMessage());
            }
        }

        printScanResults(totalProcessed, overdueFound, notificationsSent);
    }

    /**
     * Print detailed scan results
     */
    private void printScanResults(int totalProcessed, int overdueFound, int notificationsSent) {
        System.out.println("📊 Scan Results:");
        System.out.println("   📋 Total loans checked: " + totalProcessed);
        System.out.println("   ⚠️  Overdue items found: " + overdueFound);
        System.out.println("   📧 Notifications sent: " + notificationsSent);
        System.out.println("   💰 Fines applied: " + overdueFound);

        if (overdueFound > 0) {
            System.out.println("✅ Overdue processing completed");
        } else {
            System.out.println("✅ No overdue items found");
        }
    }

    /**
     * Process a single overdue loan - apply fines and send notifications
     */
    private void processOverdueLoan(Loan loan) {
        try {
            User user = loan.getUser();
            int overdueDays = loan.getOverdueDays();
            double fineAmount = loan.calculateFine();

            // Validate loan and user
            if (user == null) {
                throw new IllegalStateException("Loan has no associated user");
            }

            // Apply fine to user (only if not already applied today)
            if (!isFineAppliedToday(loan)) {
                user.addFine(fineAmount);
                markFineApplied(loan);
            }

            logOverdueDetection(loan, user, overdueDays, fineAmount);

            // Send notification (US3.1)
            sendOverdueNotification(user, loan);

        } catch (Exception e) {
            System.out.println("❌ Error processing overdue loan " + loan.getId() + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Log overdue detection details
     */
    private void logOverdueDetection(Loan loan, User user, int overdueDays, double fineAmount) {
        System.out.println("⚠️  Overdue detected: " + loan.getBook().getTitle());
        System.out.println("   👤 User: " + user.getName());
        System.out.println("   📅 Overdue days: " + overdueDays);
        System.out.println("   💰 Fine: " + fineAmount + " NIS");
        System.out.println("   🏦 Total user fines: " + user.getFineBalance() + " NIS");
    }

    /**
     * Send overdue notification to user (US3.1)
     */
    private void sendOverdueNotification(User user, Loan loan) {
        try {
            String message = buildNotificationMessage(loan);

            // Validate before sending
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                System.out.println("⚠️  Cannot send notification - user has no email: " + user.getName());
                return;
            }

            notifier.notify(user, message);
            System.out.println("📧 Notification sent to: " + user.getEmail());

        } catch (NotificationException e) {
            System.out.println("❌ Notification failed for user " + user.getName() + ": " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Unexpected error sending notification: " + e.getMessage());
        }
    }

    /**
     * Build notification message for overdue loan
     */
    private String buildNotificationMessage(Loan loan) {
        return String.format(
                "OVERDUE NOTICE: Book '%s' is %d days overdue. " +
                        "Fine accrued: %.2f NIS. Please return it immediately to avoid additional charges.",
                loan.getBook().getTitle(),
                loan.getOverdueDays(),
                loan.calculateFine()
        );
    }

    /**
     * Check if fine was already applied today (prevent duplicate fines)
     */
    private boolean isFineAppliedToday(Loan loan) {
        String loanKey = loan.getId();
        LocalDate lastApplied = lastFineApplication.get(loanKey);
        return lastApplied != null && lastApplied.equals(LocalDate.now());
    }

    /**
     * Mark fine as applied with today's date
     */
    private void markFineApplied(Loan loan) {
        lastFineApplication.put(loan.getId(), LocalDate.now());
    }

    /**
     * Calculate initial delay for scheduler (until next target time)
     */
    private long calculateInitialDelay() {
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();

        // Set target time (8:00 AM)
        calendar.set(Calendar.HOUR_OF_DAY, TARGET_SCAN_HOUR);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long targetTime = calendar.getTimeInMillis();

        // If it's past target time today, schedule for tomorrow
        if (currentTime > targetTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            targetTime = calendar.getTimeInMillis();
        }

        return targetTime - currentTime;
    }

    /**
     * Get all overdue loans for reporting
     */
    public List<Loan> getOverdueLoans() {
        return loanRepository.findActiveLoans().stream()
                .filter(Loan::isOverdue)
                .collect(Collectors.toList());
    }

    /**
     * Get overdue loans for specific user
     */
    public List<Loan> getOverdueLoansForUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return user.getActiveLoans().stream()
                .filter(Loan::isOverdue)
                .collect(Collectors.toList());
    }

    /**
     * Check if user has any overdue items
     */
    public boolean hasOverdueItems(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return user.getActiveLoans().stream()
                .anyMatch(Loan::isOverdue);
    }

    /**
     * Get total overdue fines for user
     */
    public double calculateOverdueFines(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return user.getActiveLoans().stream()
                .filter(Loan::isOverdue)
                .mapToDouble(Loan::calculateFine)
                .sum();
    }

    /**
     * Get scanner status
     */
    public boolean isScannerRunning() {
        return isScannerRunning;
    }

    /**
     * Get notifier type
     */
    public String getNotifierType() {
        return notifier.getType();
    }

    /**
     * Emergency manual scan with detailed report
     */
    public void performEmergencyScan() {
        System.out.println("🚨 EMERGENCY OVERDUE SCAN INITIATED");
        System.out.println("===========================================");

        List<Loan> allLoans = loanRepository.findActiveLoans();
        List<Loan> overdueLoans = getOverdueLoans();

        System.out.println("📈 SYSTEM OVERDUE REPORT:");
        System.out.println("   📚 Total active loans: " + allLoans.size());
        System.out.println("   ⚠️  Overdue loans: " + overdueLoans.size());
        System.out.println("   📊 Overdue rate: " +
                (allLoans.isEmpty() ? 0 : (overdueLoans.size() * 100 / allLoans.size())) + "%");
        System.out.println("   🔔 Notifier: " + notifier.getType() + " (" +
                (notifier.isActive() ? "ACTIVE" : "INACTIVE") + ")");

        if (!overdueLoans.isEmpty()) {
            printOverdueDetails(overdueLoans);
        }

        System.out.println("===========================================");
        System.out.println("✅ EMERGENCY SCAN COMPLETED");
    }

    /**
     * Print detailed overdue items information
     */
    private void printOverdueDetails(List<Loan> overdueLoans) {
        System.out.println("\n📋 OVERDUE ITEMS DETAILS:");
        for (int i = 0; i < overdueLoans.size(); i++) {
            Loan loan = overdueLoans.get(i);
            System.out.println((i + 1) + ". " + loan.getBook().getTitle());
            System.out.println("   👤 User: " + loan.getUser().getName());
            System.out.println("   📅 Due date: " + loan.getDueDate());
            System.out.println("   📅 Overdue days: " + loan.getOverdueDays());
            System.out.println("   💰 Current fine: " + loan.calculateFine() + " NIS");
            System.out.println("   📧 User email: " + loan.getUser().getEmail());
        }
    }

    /**
     * Cleanup resources
     */
    public void shutdown() {
        stopAutomaticOverdueScanning();
        try {
            notifier.shutdown();
            System.out.println("✅ Notifier shutdown completed");
        } catch (Exception e) {
            System.out.println("⚠️  Error during notifier shutdown: " + e.getMessage());
        }
    }

    /**
     * Get fine application statistics for monitoring
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("scannerRunning", isScannerRunning);
        stats.put("notifierType", notifier.getType());
        stats.put("notifierActive", notifier.isActive());
        stats.put("trackedLoans", lastFineApplication.size());
        stats.put("lastScan", LocalDateTime.now().toString());
        return stats;
    }
}