package com.library.app;

import com.library.domain.model.Book;
import com.library.domain.model.User;
import com.library.domain.service.BorrowingDomainService;

import java.lang.reflect.Field;
import java.time.LocalDate;

/**
 * Application service for borrowing operations
 * @author Your Name
 * @version 1.0
 */
public class BorrowingService {
    private final BorrowingDomainService borrowingDomainService;

    public BorrowingService(BorrowingDomainService borrowingDomainService) {
        this.borrowingDomainService = borrowingDomainService;
    }

    /**
     * Borrow a book for user (US2.1)
     * @param user user borrowing the book
     * @param book book to borrow
     * @return true if borrow successful
     */
    public boolean borrowBook(User user, Book book) {
        try {
            return borrowingDomainService.borrowBook(user, book);
        } catch (IllegalStateException e) {
            System.out.println("❌ Borrow failed: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Return a borrowed book
     * @param user user returning the book
     * @param book book to return
     * @return true if return successful
     */
    public boolean returnBook(User user, Book book) {
        try {
            return borrowingDomainService.returnBook(user, book);
        } catch (IllegalStateException e) {
            System.out.println("❌ Return failed: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Calculate total fines for user (US2.3)
     * @param user user to calculate fines for
     * @return total fine amount
     */
    public double calculateTotalFines(User user) {
        try {
            return user.calculateTotalFines();
        } catch (Exception e) {
            System.out.println("❌ Error calculating fines: " + e.getMessage());
            return user.getFineBalance(); // Return at least the balance
        }
    }

    /**
     * Pay fine for user (US2.3)
     * @param user user paying fine
     * @param amount amount to pay
     * @return remaining balance
     */
    public double payFine(User user, double amount) {
        try {
            return user.payFine(amount);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Payment error: " + e.getMessage());
            return user.getFineBalance();
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            return user.getFineBalance();
        }
    }

    /**
     * Check if user can borrow (US4.1)
     * @param user user to check
     * @return true if user can borrow
     */
    public boolean canUserBorrow(User user) {
        try {
            return user.canBorrow();
        } catch (Exception e) {
            System.out.println("❌ Error checking borrow eligibility: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if user has overdue items (US2.2)
     * @param user user to check
     * @return true if user has overdue items
     */
    public boolean hasOverdueItems(User user) {
        try {
            return user.hasOverdueItems();
        } catch (Exception e) {
            System.out.println("❌ Error checking overdue items: " + e.getMessage());
            return false;
        }
    }

    /**
     * FOR TESTING ONLY: Add a test fine to user
     * @param user user to add fine to
     * @param amount fine amount
     */
    public void addTestFine(User user, double amount) {
        try {
            if (amount < 0) {
                System.out.println("❌ Fine amount cannot be negative");
                return;
            }
            user.addFine(amount);
            System.out.println("✅ Test fine added: " + amount + " NIS");
            System.out.println("💰 Total fines now: " + user.getFineBalance() + " NIS");
        } catch (Exception e) {
            System.out.println("❌ Error adding test fine: " + e.getMessage());
        }
    }

    /**
     * FOR TESTING ONLY: Simulate overdue loan to test fines
     * @param user user to simulate overdue for
     * @param book book to make overdue
     * @param overdueDays number of days overdue
     */
    public void simulateOverdueForTesting(User user, Book book, int overdueDays) {
        try {
            System.out.println("🧪 Setting up test scenario...");

            // First, make sure the book is available and borrow it
            if (!book.isAvailable()) {
                System.out.println("❌ Book is not available for testing");
                return;
            }

            if (!borrowingDomainService.borrowBook(user, book)) {
                System.out.println("❌ Failed to borrow book for testing");
                return;
            }

            // Find the loan and manipulate dates using reflection
            user.getActiveLoans().stream()
                    .filter(loan -> loan.getBook().equals(book))
                    .findFirst()
                    .ifPresent(loan -> {
                        try {
                            // Use reflection to change private fields for testing
                            Field borrowDateField = loan.getClass().getDeclaredField("borrowDate");
                            Field dueDateField = loan.getClass().getDeclaredField("dueDate");

                            borrowDateField.setAccessible(true);
                            dueDateField.setAccessible(true);

                            // Set dates to simulate overdue
                            LocalDate oldBorrowDate = LocalDate.now().minusDays(28 + overdueDays);
                            LocalDate oldDueDate = oldBorrowDate.plusDays(28);

                            borrowDateField.set(loan, oldBorrowDate);
                            dueDateField.set(loan, oldDueDate);

                            System.out.println("📅 Simulated overdue scenario:");
                            System.out.println("   - Borrow date: " + oldBorrowDate);
                            System.out.println("   - Due date: " + oldDueDate);
                            System.out.println("   - Overdue by: " + overdueDays + " days");
                            System.out.println("   - Estimated fine: " + (overdueDays * 10) + " NIS");

                        } catch (Exception e) {
                            System.out.println("❌ Error setting up test dates: " + e.getMessage());
                        }
                    });

        } catch (Exception e) {
            System.out.println("❌ Test setup failed: " + e.getMessage());
        }
    }

    /**
     * FOR TESTING ONLY: Get detailed loan information
     * @param user user to check
     */
    public void displayLoanDetails(User user) {
        try {
            System.out.println("\n--- Loan Details ---");
            System.out.println("User: " + user.getName());
            System.out.println("Total fines: " + user.calculateTotalFines() + " NIS");
            System.out.println("Fine balance: " + user.getFineBalance() + " NIS");
            System.out.println("Active loans: " + user.getActiveLoans().size());

            if (!user.getActiveLoans().isEmpty()) {
                System.out.println("\nActive Loans:");
                for (int i = 0; i < user.getActiveLoans().size(); i++) {
                    var loan = user.getActiveLoans().get(i);
                    System.out.println((i + 1) + ". " + loan.getBook().getTitle());
                    System.out.println("   - Borrowed: " + loan.getBorrowDate());
                    System.out.println("   - Due: " + loan.getDueDate());
                    System.out.println("   - Overdue: " + loan.isOverdue());
                    System.out.println("   - Overdue days: " + loan.getOverdueDays());
                    System.out.println("   - Fine: " + loan.calculateFine() + " NIS");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error displaying loan details: " + e.getMessage());
        }
    }

    /**
     * FOR TESTING ONLY: Reset user fines and loans
     * @param user user to reset
     */
    public void resetUserForTesting(User user) {
        try {
            user.setFineBalance(0.0);
            user.getActiveLoans().clear();
            System.out.println("✅ User reset for testing - fines cleared, loans cleared");
        } catch (Exception e) {
            System.out.println("❌ Error resetting user: " + e.getMessage());
        }
    }
}