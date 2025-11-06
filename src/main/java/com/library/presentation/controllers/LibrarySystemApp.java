package com.library.presentation.controllers;

import com.library.app.AuthService;
import com.library.app.BorrowingService;
import com.library.app.CatalogService;
import com.library.domain.service.BorrowingDomainService;
import com.library.domain.service.OverdueService;
import com.library.domain.service.ReminderService;
import com.library.domain.model.EmailNotifier;
import com.library.presentation.ui.LibrarySystemUI;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;

/**
 * Main application entry point for Library Management System
 * Initializes all services and starts the user interface
 *
 * Sprint 1: Admin & Book Management
 * Sprint 2: Borrowing & Overdue Logic
 * Sprint 3: Communication & Mocking (Notifications)
 *
 * @author Your Name
 * @version 3.0
 */
public class LibrarySystemApp {

    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("🚀 Starting Library Management System...");
        System.out.println("=".repeat(50));

        try {
            // ===== 1. Initialize Repositories =====
            System.out.println("\n📦 Initializing repositories...");
            BookRepository bookRepository = new BookRepository();
            LoanRepository loanRepository = new LoanRepository();
            System.out.println("✅ Repositories initialized");

            // ===== 2. Initialize Core Services =====
            System.out.println("\n⚙️  Initializing core services...");
            AuthService authService = new AuthService();
            CatalogService catalogService = new CatalogService(bookRepository, authService);
            BorrowingDomainService borrowingDomainService = new BorrowingDomainService(loanRepository);
            BorrowingService borrowingService = new BorrowingService(borrowingDomainService);
            System.out.println("✅ Core services initialized");

            // ===== 3. Initialize Notification System (Sprint 3) =====
            System.out.println("\n📧 Initializing notification system (Sprint 3)...");
            EmailNotifier emailNotifier = new EmailNotifier();
            System.out.println("✅ Email notifier initialized: " + emailNotifier.getNotifierId());

            // ===== 4. Initialize Overdue Service (Sprint 2 + 3) =====
            System.out.println("\n⏰ Initializing overdue detection service...");
            OverdueService overdueService = new OverdueService(loanRepository, emailNotifier);
            System.out.println("✅ Overdue service initialized");

            // ===== 5. Initialize Reminder Service (Sprint 3 - NEW) =====
            System.out.println("\n📬 Initializing reminder service (Sprint 3)...");
            ReminderService reminderService = new ReminderService(loanRepository);
            reminderService.addObserver(emailNotifier); // Add email notifier as observer
            System.out.println("✅ Reminder service initialized");
            System.out.println("   📡 Active observers: " + reminderService.getActiveObserverCount());

            // ===== 6. Load Sample Books =====
            System.out.println("\n📚 Loading sample books...");
            addSampleBooks(bookRepository);

            // ===== 7. Initialize User Interface =====
            System.out.println("\n🖥️  Initializing user interface...");
            LibrarySystemUI ui = new LibrarySystemUI(
                    authService,
                    catalogService,
                    borrowingService,
                    overdueService,
                    reminderService  // Pass reminder service to UI
            );
            System.out.println("✅ User interface initialized");

            // ===== 8. Start Automatic Overdue Detection =====
            System.out.println("\n🔄 Starting automatic overdue detection...");
            overdueService.startAutomaticOverdueScanning();
            System.out.println("✅ Automatic overdue scanning started");

            // ===== 9. Setup Shutdown Hook =====
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n🛑 Shutting down library system...");
                try {
                    overdueService.shutdown();
                    System.out.println("✅ Overdue service stopped");
                    System.out.println("✅ Library system shutdown completed");
                } catch (Exception e) {
                    System.out.println("⚠️  Error during shutdown: " + e.getMessage());
                }
            }));

            // ===== 10. Display System Overview =====
            displaySystemOverview(catalogService, overdueService, reminderService);

            // ===== 11. Start Application =====
            System.out.println("🎯 Starting user interface...\n");
            ui.start();

        } catch (Exception e) {
            System.out.println("\n❌ ERROR: Failed to start application");
            System.out.println("   Reason: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Add sample books to the repository for testing
     */
    private static void addSampleBooks(BookRepository bookRepository) {
        try {
            String[][] sampleBooks = {
                    {"1", "Effective Java", "Joshua Bloch"},
                    {"2", "Head First Design Patterns", "Eric Freeman"},
                    {"3", "Clean Code", "Robert C. Martin"},
                    {"4", "Design Patterns", "Gang of Four"},
                    {"5", "The Clean Coder", "Robert C. Martin"},
                    {"6", "Effective C++", "Scott Meyers"},
                    {"7", "Head First Java", "Kathy Sierra"},
                    {"8", "Spring in Action", "Craig Walls"},
                    {"9", "Growing Object-Oriented Software", "Steve Freeman"},
                    {"10", "Java Concurrency in Practice", "Brian Goetz"}
            };

            int addedCount = 0;
            for (String[] bookData : sampleBooks) {
                try {
                    com.library.domain.model.Book book = new com.library.domain.model.Book(
                            bookData[0], // ISBN
                            bookData[1], // Title
                            bookData[2]  // Author
                    );
                    bookRepository.save(book);
                    addedCount++;
                } catch (Exception e) {
                    System.out.println("⚠️  Could not add book: " + bookData[1]);
                }
            }

            System.out.println("✅ " + addedCount + " sample books loaded successfully");

        } catch (Exception e) {
            System.out.println("❌ Error loading sample books: " + e.getMessage());
        }
    }

    /**
     * Display comprehensive system overview
     */
    private static void displaySystemOverview(CatalogService catalogService,
                                              OverdueService overdueService,
                                              ReminderService reminderService) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 SYSTEM OVERVIEW");
        System.out.println("=".repeat(50));

        // Catalog Information
        System.out.println("\n📚 CATALOG:");
        System.out.println("   • Books in catalog: " + catalogService.getBookCount());
        System.out.println("   • Status: READY");

        // Overdue Detection System
        System.out.println("\n⏰ OVERDUE DETECTION (Sprint 2):");
        System.out.println("   • Scanner status: " +
                (overdueService.isScannerRunning() ? "✅ RUNNING" : "⚠️  STOPPED"));
        System.out.println("   • Notifier type: " + overdueService.getNotifierType());

        // Notification System (Sprint 3)
        System.out.println("\n📬 REMINDER SYSTEM (Sprint 3):");
        System.out.println("   • Active observers: " + reminderService.getActiveObserverCount());
        System.out.println("   • Service status: ✅ READY");
        System.out.println("   • Message format: \"You have n overdue book(s).\"");

        // Borrowing System
        System.out.println("\n📖 BORROWING SYSTEM:");
        System.out.println("   • Loan period: 28 days");
        System.out.println("   • Fine rate: 10 NIS/day (books)");
        System.out.println("   • Status: ✅ READY");

        // Authentication
        System.out.println("\n🔐 AUTHENTICATION:");
        System.out.println("   • Default admin: admin / password123");
        System.out.println("   • Status: ✅ READY");

        System.out.println("\n" + "=".repeat(50));
        System.out.println("✅ ALL SYSTEMS OPERATIONAL");
        System.out.println("=".repeat(50) + "\n");
    }

    /**
     * Display system statistics (utility method)
     */
    private static void displaySystemStats(BookRepository bookRepository,
                                           LoanRepository loanRepository) {
        try {
            System.out.println("\n📊 System Statistics:");
            System.out.println("   • Total books: " + bookRepository.findAll().size());
            System.out.println("   • Active loans: " + loanRepository.findActiveLoans().size());
        } catch (Exception e) {
            System.out.println("⚠️  Could not display system stats: " + e.getMessage());
        }
    }
}