package com.library.presentation.ui;

import com.library.app.AuthService;
import com.library.app.BorrowingService;
import com.library.app.CatalogService;
import com.library.domain.model.Book;
import com.library.domain.model.Loan;
import com.library.domain.model.User;
import com.library.domain.service.OverdueService;
import com.library.domain.service.ReminderService;
import com.library.infrastructure.mock.NotificationLogger;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * User Interface for Library Management System
 * Provides console-based interaction for all system features
 *
 * @author Your Name
 * @version 3.0
 */
public class LibrarySystemUI {
    private AuthService authService;
    private CatalogService catalogService;
    private BorrowingService borrowingService;
    private OverdueService overdueService;
    private ReminderService reminderService; // Sprint 3
    private Scanner scanner;
    private User currentUser;

    /**
     * Constructor with all services including ReminderService (Sprint 3)
     */
    public LibrarySystemUI(AuthService authService,
                           CatalogService catalogService,
                           BorrowingService borrowingService,
                           OverdueService overdueService,
                           ReminderService reminderService) {
        this.authService = authService;
        this.catalogService = catalogService;
        this.borrowingService = borrowingService;
        this.overdueService = overdueService;
        this.reminderService = reminderService;
        this.scanner = new Scanner(System.in);

        // Create test user
        this.currentUser = new User("user1", "Ahmad Hassan", "ahmad@library.com");
    }

    /**
     * Constructor for backward compatibility (without ReminderService)
     */
    public LibrarySystemUI(AuthService authService,
                           CatalogService catalogService,
                           BorrowingService borrowingService,
                           OverdueService overdueService) {
        this(authService, catalogService, borrowingService, overdueService, null);
    }

    /**
     * Start the user interface
     */
    public void start() {
        System.out.println("=== Library Management System ===");

        while (true) {
            if (!authService.isAdminLoggedIn()) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    /**
     * Show login menu
     */
    private void showLoginMenu() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (authService.login(username, password)) {
            System.out.println("✅ Login successful!");
        } else {
            System.out.println("❌ Invalid credentials!");
        }
    }

    /**
     * Show main menu with all options
     */
    private void showMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("MAIN MENU");
        System.out.println("=".repeat(50));

        System.out.println("\n📚 CATALOG MANAGEMENT:");
        System.out.println("  1.  Add Book");
        System.out.println("  2.  Search Books");
        System.out.println("  3.  View All Books");

        System.out.println("\n📖 BORROWING:");
        System.out.println("  4.  Borrow Book");
        System.out.println("  5.  Return Book");
        System.out.println("  6.  Check Fines & Loans");
        System.out.println("  7.  Pay Fine");

        System.out.println("\n⏰ OVERDUE MANAGEMENT (Sprint 2):");
        System.out.println("  8.  Check Overdue Items");
        System.out.println("  9.  Emergency Overdue Scan");
        System.out.println("  10. Stop Overdue Scanner");
        System.out.println("  11. Start Overdue Scanner");

        System.out.println("\n📬 NOTIFICATIONS (Sprint 3):");
        System.out.println("  12. Send Overdue Reminders (US3.1)");
        System.out.println("  13. Send Reminder to Current User");
        System.out.println("  14. View All Notifications");
        System.out.println("  15. View Reminder Statistics");

        System.out.println("\n🛠️  TESTING & DEBUG:");
        System.out.println("  16. Add Test Fine");
        System.out.println("  17. Simulate Overdue");
        System.out.println("  18. Show Loan Details");
        System.out.println("  19. Reset User");
        System.out.println("  20. System Status");

        System.out.println("\n🔐 SYSTEM:");
        System.out.println("  21. Logout");
        System.out.println("  22. Exit");

        System.out.println("\n" + "=".repeat(50));
        System.out.print("Choose option: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            handleMenuChoice(choice);
        } catch (Exception e) {
            scanner.nextLine(); // clear buffer
            System.out.println("❌ Invalid input! Please enter a number.");
        }
    }

    /**
     * Handle menu choice
     */
    private void handleMenuChoice(int choice) {
        switch (choice) {
            // Catalog Management
            case 1: addBook(); break;
            case 2: searchBooks(); break;
            case 3: viewAllBooks(); break;

            // Borrowing
            case 4: borrowBook(); break;
            case 5: returnBook(); break;
            case 6: checkFinesAndLoans(); break;
            case 7: payFine(); break;

            // Overdue Management
            case 8: checkOverdueItems(); break;
            case 9: emergencyOverdueScan(); break;
            case 10: stopOverdueScanner(); break;
            case 11: startOverdueScanner(); break;

            // Notifications (Sprint 3)
            case 12: sendOverdueReminders(); break;
            case 13: sendReminderToCurrentUser(); break;
            case 14: viewNotifications(); break;
            case 15: viewReminderStatistics(); break;

            // Testing & Debug
            case 16: addTestFine(); break;
            case 17: simulateOverdue(); break;
            case 18: showLoanDetails(); break;
            case 19: resetUser(); break;
            case 20: displaySystemStatus(); break;

            // System
            case 21: logout(); break;
            case 22: exitSystem(); break;

            default: System.out.println("❌ Invalid option!");
        }
    }

    // ===== CATALOG MANAGEMENT =====

    private void addBook() {
        System.out.println("\n--- Add New Book ---");
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();

        try {
            Book book = catalogService.addBook(isbn, title, author);
            System.out.println("✅ Book added successfully: " + book.getTitle());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void searchBooks() {
        System.out.println("\n--- Search Books ---");
        System.out.print("Enter search query: ");
        String query = scanner.nextLine();

        List<Book> results = catalogService.searchBooks(query);
        if (results.isEmpty()) {
            System.out.println("❌ No books found.");
        } else {
            System.out.println("✅ Found " + results.size() + " book(s):");
            for (int i = 0; i < results.size(); i++) {
                Book book = results.get(i);
                String status = book.isAvailable() ? "✅ Available" : "📖 Borrowed";
                System.out.println((i + 1) + ". " + book.getTitle() + " by " + book.getAuthor());
                System.out.println("   ISBN: " + book.getIsbn() + " - " + status);
            }
        }
    }

    private void viewAllBooks() {
        System.out.println("\n--- All Books ---");
        List<Book> books = catalogService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("❌ No books in catalog.");
        } else {
            System.out.println("📚 Total books: " + books.size());
            System.out.println();
            for (int i = 0; i < books.size(); i++) {
                Book book = books.get(i);
                String status = book.isAvailable() ? "✅ Available" : "📖 Borrowed";
                System.out.println((i + 1) + ". " + book.getTitle() + " by " + book.getAuthor());
                System.out.println("   ISBN: " + book.getIsbn() + " - " + status);
            }
        }
    }

    // ===== BORROWING =====

    private void borrowBook() {
        System.out.println("\n--- Borrow Book ---");

        // Check if user can borrow
        if (!borrowingService.canUserBorrow(currentUser)) {
            System.out.println("❌ Cannot borrow. Reasons:");
            if (currentUser.getFineBalance() > 0) {
                System.out.println("   • Unpaid fines: " + currentUser.getFineBalance() + " NIS");
            }
            if (borrowingService.hasOverdueItems(currentUser)) {
                System.out.println("   • You have overdue items");
            }
            return;
        }

        System.out.print("Enter book ISBN: ");
        String isbn = scanner.nextLine();

        Book book = catalogService.findBookByIsbn(isbn);
        if (book == null) {
            System.out.println("❌ Book not found.");
            return;
        }

        if (!book.isAvailable()) {
            System.out.println("❌ Book is already borrowed.");
            return;
        }

        if (borrowingService.borrowBook(currentUser, book)) {
            System.out.println("✅ Book borrowed successfully!");
            System.out.println("   📖 Book: " + book.getTitle());
            System.out.println("   📅 Due date: " + java.time.LocalDate.now().plusDays(28));
            System.out.println("   ⚠️  Return by due date to avoid fines (10 NIS/day)");
        } else {
            System.out.println("❌ Failed to borrow book.");
        }
    }

    private void returnBook() {
        System.out.println("\n--- Return Book ---");
        System.out.print("Enter book ISBN: ");
        String isbn = scanner.nextLine();

        Book book = catalogService.findBookByIsbn(isbn);
        if (book == null) {
            System.out.println("❌ Book not found.");
            return;
        }

        if (borrowingService.returnBook(currentUser, book)) {
            System.out.println("✅ Book returned successfully: " + book.getTitle());

            // Check if there were any fines
            if (overdueService != null) {
                double fines = currentUser.getFineBalance();
                if (fines > 0) {
                    System.out.println("⚠️  Outstanding fines: " + fines + " NIS");
                    System.out.println("   Please pay fines to borrow new books.");
                }
            }
        } else {
            System.out.println("❌ Failed to return book. You may not have borrowed it.");
        }
    }

    private void checkFinesAndLoans() {
        System.out.println("\n--- Fines & Loans ---");

        double totalFines = borrowingService.calculateTotalFines(currentUser);
        System.out.println("💰 Total fines: " + totalFines + " NIS");
        System.out.println("💳 Fine balance: " + currentUser.getFineBalance() + " NIS");

        int activeLoansCount = currentUser.getActiveLoans().size();
        System.out.println("📖 Active loans: " + activeLoansCount);

        if (activeLoansCount > 0) {
            System.out.println("\n📚 Your borrowed books:");
            for (int i = 0; i < currentUser.getActiveLoans().size(); i++) {
                Loan loan = currentUser.getActiveLoans().get(i);
                String status = loan.isOverdue() ? "⚠️  OVERDUE" : "✅ On time";
                System.out.println((i + 1) + ". " + loan.getBook().getTitle());
                System.out.println("   📅 Due: " + loan.getDueDate());
                System.out.println("   " + status);
                if (loan.isOverdue()) {
                    System.out.println("   ⏰ Overdue days: " + loan.getOverdueDays());
                    System.out.println("   💰 Fine: " + loan.calculateFine() + " NIS");
                }
            }
        }

        // Borrowing eligibility
        System.out.println("\n🎯 Borrowing status:");
        if (borrowingService.canUserBorrow(currentUser)) {
            System.out.println("✅ You can borrow books");
        } else {
            System.out.println("❌ Cannot borrow books:");
            if (currentUser.getFineBalance() > 0) {
                System.out.println("   • Unpaid fines: " + currentUser.getFineBalance() + " NIS");
            }
            if (borrowingService.hasOverdueItems(currentUser)) {
                System.out.println("   • You have overdue items");
            }
        }
    }

    private void payFine() {
        System.out.println("\n--- Pay Fine ---");
        double currentFines = currentUser.getFineBalance();
        System.out.println("💰 Current fine balance: " + currentFines + " NIS");

        if (currentFines == 0) {
            System.out.println("✅ No fines to pay.");
            return;
        }

        System.out.print("Enter amount to pay: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        if (amount <= 0) {
            System.out.println("❌ Amount must be positive.");
            return;
        }

        if (amount > currentFines) {
            System.out.println("⚠️  Amount exceeds fines. Paying " + currentFines + " NIS instead.");
            amount = currentFines;
        }

        double remaining = borrowingService.payFine(currentUser, amount);
        System.out.println("✅ Payment successful!");
        System.out.println("   💵 Paid: " + amount + " NIS");
        System.out.println("   💳 Remaining: " + remaining + " NIS");

        if (remaining == 0 && borrowingService.canUserBorrow(currentUser)) {
            System.out.println("🎉 All fines cleared! You can now borrow books.");
        }
    }

    // ===== OVERDUE MANAGEMENT (Sprint 2) =====

    private void checkOverdueItems() {
        System.out.println("\n--- Overdue Items Check ---");

        if (overdueService == null) {
            System.out.println("❌ Overdue service is not available.");
            return;
        }

        System.out.println("🔍 Scanning for overdue items...");
        overdueService.checkAndProcessOverdueItems();

        List<Loan> overdueLoans = overdueService.getOverdueLoans();
        if (overdueLoans.isEmpty()) {
            System.out.println("✅ No overdue items in the system");
        } else {
            System.out.println("⚠️  " + overdueLoans.size() + " overdue item(s) found:");
            for (int i = 0; i < overdueLoans.size(); i++) {
                Loan loan = overdueLoans.get(i);
                System.out.println((i + 1) + ". " + loan.getBook().getTitle());
                System.out.println("   👤 User: " + loan.getUser().getName());
                System.out.println("   ⏰ Overdue: " + loan.getOverdueDays() + " days");
                System.out.println("   💰 Fine: " + loan.calculateFine() + " NIS");
            }
        }

        // User's personal overdue items
        List<Loan> userOverdue = overdueService.getOverdueLoansForUser(currentUser);
        if (!userOverdue.isEmpty()) {
            System.out.println("\n⚠️  Your overdue items:");
            for (Loan loan : userOverdue) {
                System.out.println("• " + loan.getBook().getTitle() +
                        " (" + loan.getOverdueDays() + " days overdue)");
            }
        }
    }

    private void emergencyOverdueScan() {
        System.out.println("\n--- Emergency Overdue Scan ---");
        if (overdueService == null) {
            System.out.println("❌ Overdue service not available.");
            return;
        }
        overdueService.performEmergencyScan();
    }

    private void stopOverdueScanner() {
        System.out.println("\n--- Stop Overdue Scanner ---");
        if (overdueService == null) {
            System.out.println("❌ Overdue service not available.");
            return;
        }
        overdueService.stopAutomaticOverdueScanning();
    }

    private void startOverdueScanner() {
        System.out.println("\n--- Start Overdue Scanner ---");
        if (overdueService == null) {
            System.out.println("❌ Overdue service not available.");
            return;
        }
        overdueService.startAutomaticOverdueScanning();
    }

    // ===== NOTIFICATIONS (Sprint 3) =====

    /**
     * Send overdue reminders to all users (US3.1)
     */
    private void sendOverdueReminders() {
        System.out.println("\n--- Send Overdue Reminders (US3.1) ---");

        if (reminderService == null) {
            System.out.println("❌ Reminder service is not available.");
            return;
        }

        System.out.println("📧 Sending reminders to all users with overdue books...");
        System.out.println("   Message format: \"You have n overdue book(s).\"");
        System.out.println();

        reminderService.sendOverdueReminders();

        System.out.println("\n✅ Reminder process completed!");
        System.out.println("   📊 Total notifications sent: " + reminderService.getTotalNotificationsSent());
    }

    /**
     * Send reminder to current user only
     */
    private void sendReminderToCurrentUser() {
        System.out.println("\n--- Send Reminder to Current User ---");

        if (reminderService == null) {
            System.out.println("❌ Reminder service is not available.");
            return;
        }

        System.out.println("📧 Checking overdue items for: " + currentUser.getName());
        reminderService.sendReminderToUser(currentUser);
        System.out.println("✅ Done!");
    }

    /**
     * View all sent notifications
     */
    private void viewNotifications() {
        System.out.println("\n--- Notifications Log ---");
        NotificationLogger.displayAllNotifications();
    }

    /**
     * View reminder service statistics
     */
    private void viewReminderStatistics() {
        System.out.println("\n--- Reminder Statistics (Sprint 3) ---");

        if (reminderService == null) {
            System.out.println("❌ Reminder service is not available.");
            return;
        }

        Map<String, Object> stats = reminderService.getOverdueStatistics();

        System.out.println("📊 Reminder Service Statistics:");
        System.out.println("   👥 Users with overdue: " + stats.get("totalUsersWithOverdue"));
        System.out.println("   📚 Total overdue items: " + stats.get("totalOverdueItems"));
        System.out.println("   📡 Active observers: " + stats.get("activeObservers"));
        System.out.println("   📧 Notifications sent: " + stats.get("totalNotificationsSent"));

        System.out.println("\n📋 Notification Log:");
        System.out.println("   📝 Total logged: " + NotificationLogger.getNotificationCount());
    }

    // ===== TESTING & DEBUG =====

    private void addTestFine() {
        System.out.println("\n--- Add Test Fine ---");
        System.out.print("Enter fine amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        borrowingService.addTestFine(currentUser, amount);
        System.out.println("✅ Test fine added: " + amount + " NIS");
    }

    private void simulateOverdue() {
        System.out.println("\n--- Simulate Overdue ---");
        System.out.print("Enter book ISBN: ");
        String isbn = scanner.nextLine();

        Book book = catalogService.findBookByIsbn(isbn);
        if (book == null) {
            System.out.println("❌ Book not found");
            return;
        }

        System.out.print("Enter overdue days: ");
        int days = scanner.nextInt();
        scanner.nextLine();

        borrowingService.simulateOverdueForTesting(currentUser, book, days);
        System.out.println("✅ Overdue simulation created");
    }

    private void showLoanDetails() {
        System.out.println("\n--- Loan Details ---");
        borrowingService.displayLoanDetails(currentUser);
    }

    private void resetUser() {
        System.out.println("\n--- Reset User ---");
        System.out.print("⚠️  Reset all fines and loans? (y/n): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("y")) {
            borrowingService.resetUserForTesting(currentUser);
            NotificationLogger.clearLog();
            if (reminderService != null) {
                reminderService.resetNotificationCount();
            }
            System.out.println("✅ User reset complete");
        } else {
            System.out.println("❌ Reset cancelled");
        }
    }

    // ===== SYSTEM =====

    private void logout() {
        authService.logout();
        System.out.println("✅ Logged out successfully!");
    }

    private void exitSystem() {
        System.out.println("\n👋 Thank you for using Library Management System!");
        System.out.println("🛑 Shutting down...");
        System.exit(0);
    }

    /**
     * Display comprehensive system status
     */
    public void displaySystemStatus() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 SYSTEM STATUS");
        System.out.println("=".repeat(50));

        // User Info
        System.out.println("\n👤 CURRENT USER:");
        System.out.println("   • Name: " + currentUser.getName());
        System.out.println("   • Email: " + currentUser.getEmail());
        System.out.println("   • Fine balance: " + currentUser.getFineBalance() + " NIS");
        System.out.println("   • Active loans: " + currentUser.getActiveLoans().size());

        // Catalog
        System.out.println("\n📚 CATALOG:");
        System.out.println("   • Total books: " + catalogService.getBookCount());

        // Overdue Service
        if (overdueService != null) {
            System.out.println("\n⏰ OVERDUE SERVICE:");
            System.out.println("   • Scanner: " +
                    (overdueService.isScannerRunning() ? "✅ RUNNING" : "⚠️  STOPPED"));
            System.out.println("   • Notifier: " + overdueService.getNotifierType());
        }

        // Reminder Service (Sprint 3)
        if (reminderService != null) {
            System.out.println("\n📬 REMINDER SERVICE (Sprint 3):");
            System.out.println("   • Active observers: " + reminderService.getActiveObserverCount());
            System.out.println("   • Total sent: " + reminderService.getTotalNotificationsSent());
        }

        // Notifications
        System.out.println("\n📧 NOTIFICATIONS:");
        System.out.println("   • Logged: " + NotificationLogger.getNotificationCount());

        System.out.println("\n" + "=".repeat(50));
    }
}