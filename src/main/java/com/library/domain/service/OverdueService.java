package com.library.domain.service;

import com.library.domain.model.Loan;
import com.library.domain.model.User;
import com.library.infrastructure.notification.Observer;
import com.library.repository.LoanRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Service for automatic overdue detection and processing (US2.2)
 * @author Your Name
 * @version 1.0
 */
public class OverdueService {
    private final LoanRepository loanRepository;
    private final Observer notifier;
    private final ScheduledExecutorService scheduler;
    private boolean isScannerRunning = false;

    public OverdueService(LoanRepository loanRepository, Observer notifier) {
        this.loanRepository = loanRepository;
        this.notifier = notifier;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    /**
     * Start automatic overdue scanning (runs daily at specified time)
     */
    public void startAutomaticOverdueScanning() {
        if (isScannerRunning) {
            System.out.println("⚠ Overdue scanner is already running");
            return;
        }

        System.out.println(" Starting automatic overdue scanner...");
        isScannerRunning = true;


        long initialDelay = calculateInitialDelay();
        long period = TimeUnit.DAYS.toMillis(1); // 24 hours

        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println(" Scheduled overdue scan started at: " + LocalDate.now());
                checkAndProcessOverdueItems();
                System.out.println(" Scheduled overdue scan completed");
            } catch (Exception e) {
                System.out.println(" Error in scheduled overdue scan: " + e.getMessage());
            }
        }, initialDelay, period, TimeUnit.MILLISECONDS);

        System.out.println(" Automatic overdue scanner started successfully");
        System.out.println(" Next scan in: " + (initialDelay / (1000 * 60 * 60)) + " hours");
    }

    /**
     * Stop automatic overdue scanning
     */
    public void stopAutomaticOverdueScanning() {
        if (!isScannerRunning) {
            System.out.println(" Overdue scanner is not running");
            return;
        }

        System.out.println(" Stopping automatic overdue scanner...");
        scheduler.shutdown();
        isScannerRunning = false;
        System.out.println(" Automatic overdue scanner stopped");
    }

    /**
     * Manual trigger for overdue detection (US2.2)
     */
    public void checkAndProcessOverdueItems() {
        System.out.println(" Scanning for overdue items...");

        List<Loan> activeLoans = loanRepository.findActiveLoans();
        int totalProcessed = 0;
        int overdueFound = 0;

        for (Loan loan : activeLoans) {
            if (loan.isOverdue()) {
                processOverdueLoan(loan);
                totalProcessed++;
                overdueFound++;
            }
            totalProcessed++;
        }

        System.out.println(" Scan results:");
        System.out.println("   - Total loans checked: " + totalProcessed);
        System.out.println("   - Overdue items found: " + overdueFound);
        System.out.println("   - Notifications sent: " + overdueFound);

        if (overdueFound > 0) {
            System.out.println(" Overdue processing completed");
        } else {
            System.out.println(" No overdue items found");
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

            // Apply fine to user (only if not already applied today)
            if (!isFineAppliedToday(loan)) {
                user.addFine(fineAmount);
                markFineApplied(loan);
            }

            System.out.println(" Overdue detected: " + loan.getBook().getTitle());
            System.out.println("   - User: " + user.getName());
            System.out.println("   - Overdue days: " + overdueDays);
            System.out.println("   - Fine: " + fineAmount + " NIS");
            System.out.println("   - Total user fines: " + user.getFineBalance() + " NIS");

            // Send notification (US3.1)
            sendOverdueNotification(user, loan);

        } catch (Exception e) {
            System.out.println(" Error processing overdue loan: " + e.getMessage());
        }
    }

    /**
     * Send overdue notification to user (US3.1)
     */
    private void sendOverdueNotification(User user, Loan loan) {
        try {
            String message = String.format(
                    "OVERDUE NOTICE: Book '%s' is %d days overdue. " +
                            "Fine accrued: %.2f NIS. Please return it immediately to avoid additional charges.",
                    loan.getBook().getTitle(),
                    loan.getOverdueDays(),
                    loan.calculateFine()
            );

            notifier.notify(user, message);
            System.out.println(" Notification sent to: " + user.getEmail());

        } catch (Exception e) {
            System.out.println(" Error sending notification: " + e.getMessage());
        }
    }

    /**
     * Check if fine was already applied today (prevent duplicate fines)
     */
    private boolean isFineAppliedToday(Loan loan) {
        // Simple implementation - in real system, you'd track application dates
        // For now, we'll assume fines are applied daily for overdue items
        return false;
    }

    /**
     * Mark fine as applied (placeholder for more complex tracking)
     */
    private void markFineApplied(Loan loan) {
        // In real system, you'd store the last fine application date
    }

    /**
     * Calculate initial delay for scheduler (until next 8:00 AM)
     */
    private long calculateInitialDelay() {
        LocalDate now = LocalDate.now();
        LocalDate tomorrow = now.plusDays(1);

        // Target time: 8:00 AM
        long targetTime = TimeUnit.HOURS.toMillis(8);

        // Calculate delay until next 8:00 AM
        long currentTime = System.currentTimeMillis();
        long nextScanTime = currentTime + targetTime;

        // If it's past 8:00 AM today, schedule for tomorrow
        if (currentTime > targetTime) {
            nextScanTime += TimeUnit.DAYS.toMillis(1);
        }

        return nextScanTime - currentTime;
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
        return user.getActiveLoans().stream()
                .filter(Loan::isOverdue)
                .collect(Collectors.toList());
    }

    /**
     * Check if user has any overdue items
     */
    public boolean hasOverdueItems(User user) {
        return user.getActiveLoans().stream()
                .anyMatch(Loan::isOverdue);
    }

    /**
     * Get total overdue fines for user
     */
    public double calculateOverdueFines(User user) {
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
     * Emergency manual scan with detailed report
     */
    public void performEmergencyScan() {
        System.out.println(" EMERGENCY OVERDUE SCAN INITIATED");
        System.out.println("=====================================");

        List<Loan> allLoans = loanRepository.findActiveLoans();
        List<Loan> overdueLoans = getOverdueLoans();

        System.out.println(" SYSTEM OVERDUE REPORT:");
        System.out.println("   - Total active loans: " + allLoans.size());
        System.out.println("   - Overdue loans: " + overdueLoans.size());
        System.out.println("   - Overdue rate: " +
                (allLoans.isEmpty() ? 0 : (overdueLoans.size() * 100 / allLoans.size())) + "%");

        if (!overdueLoans.isEmpty()) {
            System.out.println("\n OVERDUE ITEMS DETAILS:");
            for (int i = 0; i < overdueLoans.size(); i++) {
                Loan loan = overdueLoans.get(i);
                System.out.println((i + 1) + ". " + loan.getBook().getTitle());
                System.out.println("   - User: " + loan.getUser().getName());
                System.out.println("   - Due date: " + loan.getDueDate());
                System.out.println("   - Overdue days: " + loan.getOverdueDays());
                System.out.println("   - Current fine: " + loan.calculateFine() + " NIS");
            }
        }

        System.out.println("=====================================");
        System.out.println("EMERGENCY SCAN COMPLETED");
    }

    /**
     * Cleanup resources
     */
    public void shutdown() {
        stopAutomaticOverdueScanning();
    }
}