package com.library.app;

import com.library.domain.service.BorrowingDomainService;
import com.library.domain.service.OverdueService;
import com.library.infrastructure.notification.EmailNotifier;
import com.library.presentation.ui.LibrarySystemUI;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;

/**
 * Main application class - نقطة بداية التطبيق
 * @author Your Name
 * @version 1.0
 */
public class LibrarySystemApp {

    /**
     * Main method - يدير تهيئة النظام كامل
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println("🚀 Starting Library Management System...");

        try {
            // 1. ✅ Initialize repositories
            BookRepository bookRepository = new BookRepository();
            LoanRepository loanRepository = new LoanRepository();

            // 2. ✅ Initialize services
            AuthService authService = new AuthService();
            CatalogService catalogService = new CatalogService(bookRepository, authService);

            BorrowingDomainService borrowingDomainService = new BorrowingDomainService(loanRepository);
            BorrowingService borrowingService = new BorrowingService(borrowingDomainService);

            // 3. ✅ Initialize Overdue Service with Email Notifier (Sprint 2 + 3)
            EmailNotifier emailNotifier = new EmailNotifier();
            OverdueService overdueService = new OverdueService(loanRepository, emailNotifier);

            // 4. ✅ Add sample books for testing
            System.out.println("📚 Loading sample books...");
            addSampleBooks(bookRepository);

            // 5. ✅ Initialize UI
            LibrarySystemUI ui = new LibrarySystemUI(authService, catalogService, borrowingService, overdueService);

            // 6. ✅ Start automatic overdue detection system
            System.out.println("🕐 Initializing overdue detection system...");
            overdueService.startAutomaticOverdueScanning();

            // 7. ✅ Add shutdown hook for graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("🛑 Shutting down library system...");
                overdueService.shutdown();
                System.out.println("✅ Library system shutdown completed");
            }));

            // 8. ✅ Display startup information and start application
            System.out.println("✅ System initialized successfully!");
            System.out.println("📊 System Overview:");
            System.out.println("   - Books in catalog: " + catalogService.getBookCount());
            System.out.println("   - Overdue scanner: " + (overdueService.isScannerRunning() ? "✅ RUNNING" : "❌ STOPPED"));
            System.out.println("   - Notification service: ✅ READY");
            System.out.println("   - Borrowing service: ✅ READY");
            System.out.println("🔐 Login credentials: admin / password123");
            System.out.println("==========================================");

            // 9. ✅ Start the user interface
            ui.start();

        } catch (Exception e) {
            System.out.println("❌ Error starting application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * إضافة بيانات تجريبية للاختبار
     * @param bookRepository مستودع الكتب
     */
    private static void addSampleBooks(BookRepository bookRepository) {
        try {
            // إضافة كتب تجريبية متنوعة
            String[][] sampleBooks = {
                    {"001", "Java Programming", "John Doe"},
                    {"002", "Design Patterns", "Jane Smith"},
                    {"003", "Clean Code", "Robert Martin"},
                    {"004", "Effective Java", "Joshua Bloch"},
                    {"005", "Head First Java", "Kathy Sierra"},
                    {"006", "The Great Gatsby", "F. Scott Fitzgerald"},
                    {"007", "Spring in Action", "Craig Walls"},
                    {"008", "Test Driven Development", "Kent Beck"}
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
                    System.out.println("⚠️ Could not add book: " + bookData[1] + " - " + e.getMessage());
                }
            }

            System.out.println("✅ " + addedCount + " sample books loaded successfully");

        } catch (Exception e) {
            System.out.println("❌ Error loading sample books: " + e.getMessage());
        }
    }

    /**
     * طريقة بديلة لإضافة بيانات مع التحقق من الـ Admin
     */
    private static void addSampleBooksWithAdmin(CatalogService catalogService, AuthService authService) {
        try {
            // Login as admin first
            if (authService.login("admin", "password123")) {
                System.out.println("🔐 Admin logged in for sample data...");

                // Add books using CatalogService (requires admin)
                catalogService.addBook("100", "Advanced Java", "Expert Author");
                catalogService.addBook("101", "Database Design", "DB Expert");

                System.out.println("✅ Sample books added with admin privileges");

                // Logout after adding
                authService.logout();
            } else {
                System.out.println("⚠️ Could not login as admin for sample data");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not add books with admin: " + e.getMessage());
        }
    }

    /**
     * عرض إحصائيات النظام
     */
    private static void displaySystemStats(BookRepository bookRepository, LoanRepository loanRepository) {
        try {
            System.out.println("\n📈 System Statistics:");
            System.out.println("   - Total books: " + bookRepository.findAll().size());
            System.out.println("   - Active loans: " + loanRepository.findActiveLoans().size());
            System.out.println("   - Total loans: " + loanRepository.findActiveLoans().size());
        } catch (Exception e) {
            System.out.println("⚠️ Could not display system stats: " + e.getMessage());
        }
    }
}