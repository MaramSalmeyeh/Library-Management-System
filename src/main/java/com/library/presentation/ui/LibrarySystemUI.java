package com.library.presentation.ui;

import com.library.app.AuthService;
import com.library.app.BorrowingService;
import com.library.app.CatalogService;
import com.library.domain.model.Book;
import com.library.domain.model.Loan;
import com.library.domain.model.User;
import com.library.domain.service.OverdueService;
import com.library.infrastructure.notification.NotificationLogger;

import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface for library system
 * @author Your Name
 * @version 1.0
 */
public class LibrarySystemUI {
    private AuthService authService;
    private CatalogService catalogService;
    private BorrowingService borrowingService;
    private OverdueService overdueService;
    private Scanner scanner;
    private User currentUser;

    public LibrarySystemUI(AuthService authService, CatalogService catalogService,
                           BorrowingService borrowingService, OverdueService overdueService) {
        this.authService = authService;
        this.catalogService = catalogService;
        this.borrowingService = borrowingService;
        this.overdueService = overdueService;
        this.scanner = new Scanner(System.in);

        // Create test user
        this.currentUser = new User("user1", "Test User", "test@library.com");
    }

    // Constructor overload for backward compatibility
    public LibrarySystemUI(AuthService authService, CatalogService catalogService,
                           BorrowingService borrowingService) {
        this(authService, catalogService, borrowingService, null);
    }

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

    private void showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. 📚 Add Book");
        System.out.println("2. 🔍 Search Books");
        System.out.println("3. 📖 View All Books");
        System.out.println("4. 📥 Borrow Book");
        System.out.println("5. 📤 Return Book");
        System.out.println("6. 💰 Check Fines & Loans");
        System.out.println("7. 💳 Pay Fine");
        System.out.println("8. ⏰ Check Overdue Items");
        System.out.println("9. 📧 View Notifications");
        System.out.println("10. 🚨 Emergency Overdue Scan");
        System.out.println("11. ⏸️ Stop Overdue Scanner");
        System.out.println("12. ▶️ Start Overdue Scanner");
        System.out.println("13. 🧪 Add Test Fine");
        System.out.println("14. 🧪 Simulate Overdue");
        System.out.println("15. 🧪 Show Loan Details");
        System.out.println("16. 🧪 Reset User");
        System.out.println("17. 🔒 Logout");
        System.out.println("18. 🚪 Exit");
        System.out.print("Choose option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                addBook();
                break;
            case 2:
                searchBooks();
                break;
            case 3:
                viewAllBooks();
                break;
            case 4:
                borrowBook();
                break;
            case 5:
                returnBook();
                break;
            case 6:
                checkFinesAndLoans();
                break;
            case 7:
                payFine();
                break;
            case 8:
                checkOverdueItems();
                break;
            case 9:
                viewNotifications();
                break;
            case 10:
                emergencyOverdueScan();
                break;
            case 11:
                stopOverdueScanner();
                break;
            case 12:
                startOverdueScanner();
                break;
            case 13:
                addTestFine();
                break;
            case 14:
                simulateOverdue();
                break;
            case 15:
                showLoanDetails();
                break;
            case 16:
                resetUser();
                break;
            case 17:
                authService.logout();
                System.out.println("✅ Logged out successfully!");
                break;
            case 18:
                System.out.println("👋 Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("❌ Invalid option!");
        }
    }

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
            for (Book book : results) {
                String status = book.isAvailable() ? "✅ Available" : "❌ Borrowed";
                System.out.println("- " + book.getTitle() + " by " + book.getAuthor() +
                        " (ISBN: " + book.getIsbn() + ") - " + status);
            }
        }
    }

    private void viewAllBooks() {
        System.out.println("\n--- All Books ---");
        List<Book> books = catalogService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("📚 No books in catalog.");
        } else {
            System.out.println("📚 Total books: " + books.size());
            for (Book book : books) {
                String status = book.isAvailable() ? "✅ Available" : "❌ Borrowed";
                System.out.println("- " + book.getTitle() + " by " + book.getAuthor() +
                        " (ISBN: " + book.getIsbn() + ") - " + status);
            }
        }
    }

    private void borrowBook() {
        System.out.println("\n--- Borrow Book ---");

        // Check if user can borrow
        if (!borrowingService.canUserBorrow(currentUser)) {
            System.out.println("❌ Cannot borrow. Reason:");
            if (currentUser.getFineBalance() > 0) {
                System.out.println("   - You have unpaid fines: " + currentUser.getFineBalance() + " NIS");
            }
            if (borrowingService.hasOverdueItems(currentUser)) {
                System.out.println("   - You have overdue items");
            }
            return;
        }

        System.out.print("Enter book ISBN to borrow: ");
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
            System.out.println("✅ Book borrowed successfully: " + book.getTitle());
            System.out.println("📅 Due date: " + java.time.LocalDate.now().plusDays(28));
            System.out.println("📅 Return by: " + java.time.LocalDate.now().plusDays(28));
        } else {
            System.out.println("❌ Failed to borrow book.");
        }
    }

    private void returnBook() {
        System.out.println("\n--- Return Book ---");
        System.out.print("Enter book ISBN to return: ");
        String isbn = scanner.nextLine();

        Book book = catalogService.findBookByIsbn(isbn);
        if (book == null) {
            System.out.println("❌ Book not found.");
            return;
        }

        if (borrowingService.returnBook(currentUser, book)) {
            System.out.println("✅ Book returned successfully: " + book.getTitle());

            // Check if there were any fines for this book
            if (overdueService != null) {
                double fines = overdueService.calculateOverdueFines(currentUser);
                if (fines > 0) {
                    System.out.println("💰 Overdue fines for returned book: " + fines + " NIS");
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
        System.out.println("📚 Active loans: " + activeLoansCount);

        if (activeLoansCount > 0) {
            System.out.println("\nYour borrowed books:");
            for (int i = 0; i < currentUser.getActiveLoans().size(); i++) {
                Loan loan = currentUser.getActiveLoans().get(i);
                String status = loan.isOverdue() ? "❌ OVERDUE" : "✅ On time";
                System.out.println((i + 1) + ". " + loan.getBook().getTitle());
                System.out.println("   - Due: " + loan.getDueDate());
                System.out.println("   - Status: " + status);
                if (loan.isOverdue()) {
                    System.out.println("   - Overdue days: " + loan.getOverdueDays());
                    System.out.println("   - Fine: " + loan.calculateFine() + " NIS");
                }
            }
        }

        // Check borrowing eligibility
        System.out.println("\n📋 Borrowing status:");
        if (borrowingService.canUserBorrow(currentUser)) {
            System.out.println("✅ You can borrow books");
        } else {
            System.out.println("❌ Cannot borrow books");
            if (currentUser.getFineBalance() > 0) {
                System.out.println("   - Unpaid fines: " + currentUser.getFineBalance() + " NIS");
            }
            if (borrowingService.hasOverdueItems(currentUser)) {
                System.out.println("   - You have overdue items");
            }
        }
    }

    private void payFine() {
        System.out.println("\n--- Pay Fine ---");
        double currentFines = borrowingService.calculateTotalFines(currentUser);
        System.out.println("💰 Current fines: " + currentFines + " NIS");
        System.out.println("💳 Fine balance: " + currentUser.getFineBalance() + " NIS");

        if (currentFines == 0) {
            System.out.println("✅ No fines to pay.");
            return;
        }

        System.out.print("Enter amount to pay: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // consume newline

        if (amount <= 0) {
            System.out.println("❌ Amount must be positive.");
            return;
        }

        if (amount > currentFines) {
            System.out.println("⚠️ Amount exceeds current fines. Paying " + currentFines + " NIS instead.");
            amount = currentFines;
        }

        double remaining = borrowingService.payFine(currentUser, amount);
        System.out.println("✅ Payment successful!");
        System.out.println("💰 Paid: " + amount + " NIS");
        System.out.println("💳 Remaining balance: " + remaining + " NIS");

        // Check if user can now borrow
        if (remaining == 0 && borrowingService.canUserBorrow(currentUser)) {
            System.out.println("🎉 All fines cleared! You can now borrow books.");
        }
    }

    private void checkOverdueItems() {
        System.out.println("\n--- Overdue Items Check ---");

        if (overdueService == null) {
            System.out.println("❌ Overdue service is not available.");
            return;
        }

        System.out.println("🔍 Scanning for overdue items...");
        overdueService.checkAndProcessOverdueItems();

        // Show results
        List<Loan> overdueLoans = overdueService.getOverdueLoans();
        if (overdueLoans.isEmpty()) {
            System.out.println("✅ No overdue items in the system");
        } else {
            System.out.println("⚠️ " + overdueLoans.size() + " overdue item(s) found in system:");
            for (int i = 0; i < overdueLoans.size(); i++) {
                Loan loan = overdueLoans.get(i);
                System.out.println((i + 1) + ". " + loan.getBook().getTitle());
                System.out.println("   - User: " + loan.getUser().getName());
                System.out.println("   - Overdue days: " + loan.getOverdueDays());
                System.out.println("   - Fine: " + loan.calculateFine() + " NIS");
                System.out.println("   - Due date: " + loan.getDueDate());
            }
        }

        // Check user's personal overdue items
        List<Loan> userOverdue = overdueService.getOverdueLoansForUser(currentUser);
        if (!userOverdue.isEmpty()) {
            System.out.println("\n📋 Your overdue items:");
            for (Loan loan : userOverdue) {
                System.out.println("- " + loan.getBook().getTitle() + " (" + loan.getOverdueDays() + " days overdue)");
            }
        }
    }

    private void viewNotifications() {
        System.out.println("\n--- Notifications ---");
        NotificationLogger.displayAllNotifications();
    }

    private void emergencyOverdueScan() {
        System.out.println("\n--- Emergency Overdue Scan ---");

        if (overdueService == null) {
            System.out.println("❌ Overdue service is not available.");
            return;
        }

        overdueService.performEmergencyScan();
    }

    private void stopOverdueScanner() {
        System.out.println("\n--- Stop Overdue Scanner ---");

        if (overdueService == null) {
            System.out.println("❌ Overdue service is not available.");
            return;
        }

        overdueService.stopAutomaticOverdueScanning();
    }

    private void startOverdueScanner() {
        System.out.println("\n--- Start Overdue Scanner ---");

        if (overdueService == null) {
            System.out.println("❌ Overdue service is not available.");
            return;
        }

        overdueService.startAutomaticOverdueScanning();
    }

    private void addTestFine() {
        System.out.println("\n--- Add Test Fine ---");
        System.out.print("Enter fine amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        borrowingService.addTestFine(currentUser, amount);
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
    }

    private void showLoanDetails() {
        System.out.println("\n--- Loan Details ---");
        borrowingService.displayLoanDetails(currentUser);
    }

    private void resetUser() {
        System.out.println("\n--- Reset User ---");
        System.out.print("Are you sure you want to reset all fines and loans? (y/n): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("y")) {
            borrowingService.resetUserForTesting(currentUser);
            NotificationLogger.clearLog();
            System.out.println("✅ User reset complete - all fines and loans cleared");
        } else {
            System.out.println("Reset cancelled.");
        }
    }

    /**
     * Display system status information
     */
    public void displaySystemStatus() {
        System.out.println("\n--- System Status ---");
        System.out.println("📚 Books in catalog: " + catalogService.getBookCount());
        System.out.println("👤 Current user: " + currentUser.getName());
        System.out.println("💰 User fines: " + currentUser.getFineBalance() + " NIS");
        System.out.println("📖 User active loans: " + currentUser.getActiveLoans().size());

        if (overdueService != null) {
            System.out.println("⏰ Overdue scanner: " +
                    (overdueService.isScannerRunning() ? "✅ RUNNING" : "❌ STOPPED"));
        }

        System.out.println("📧 Notifications sent: " + NotificationLogger.getNotificationCount());
    }
}