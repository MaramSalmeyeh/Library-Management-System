package com.library.app;

import com.library.domain.service.BorrowingDomainService;
import com.library.presentation.ui.LibrarySystemUI;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;

public class LibrarySystemApp {

    public static void main(String[] args) {
        System.out.println("🚀 Starting Library Management System...");

        // 1. Initialize repositories
        BookRepository bookRepository = new BookRepository();
        LoanRepository loanRepository = new LoanRepository();

        // 2. Initialize services
        AuthService authService = new AuthService();
        CatalogService catalogService = new CatalogService(bookRepository, authService);

        BorrowingDomainService borrowingDomainService = new BorrowingDomainService(loanRepository);
        BorrowingService borrowingService = new BorrowingService(borrowingDomainService);

        // 3. Add sample books directly
        System.out.println("📚 Loading sample books...");
        addSampleBooks(bookRepository);

        // 4. Initialize and start UI
        LibrarySystemUI ui = new LibrarySystemUI(authService, catalogService, borrowingService);

        System.out.println("✅ System initialized successfully!");
        System.out.println("📚 Books available: " + catalogService.getBookCount());
        System.out.println("🔐 Login: admin / password123");
        ui.start();
    }

    private static void addSampleBooks(BookRepository bookRepository) {
        // Add books directly to repository
        bookRepository.save(new com.library.domain.model.Book("001", "Java Programming", "John Doe"));
        bookRepository.save(new com.library.domain.model.Book("002", "Design Patterns", "Jane Smith"));
        bookRepository.save(new com.library.domain.model.Book("003", "Clean Code", "Robert Martin"));
        System.out.println("✅ 3 sample books added");
    }
}