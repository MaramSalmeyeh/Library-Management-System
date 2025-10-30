package com.library.presentation.ui;

import com.library.app.AuthService;
import com.library.app.BorrowingService;
import com.library.app.CatalogService;
import com.library.domain.model.Book;
import com.library.domain.model.User;

import java.util.List;
import java.util.Scanner;

public class LibrarySystemUI {
    private AuthService authService;
    private CatalogService catalogService;
    private BorrowingService borrowingService;
    private Scanner scanner;
    private User currentUser;

    public LibrarySystemUI(AuthService authService, CatalogService catalogService,
                           BorrowingService borrowingService) {
        this.authService = authService;
        this.catalogService = catalogService;
        this.borrowingService = borrowingService;
        this.scanner = new Scanner(System.in);
        this.currentUser = new User("user1", "Test User", "test@library.com");
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
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid credentials!");
        }
    }

    private void showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Add Book");
        System.out.println("2. Search Books");
        System.out.println("3. View All Books");
        System.out.println("4. Logout");
        System.out.println("5. Exit");
        System.out.print("Choose option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1: addBook(); break;
            case 2: searchBooks(); break;
            case 3: viewAllBooks(); break;
            case 4:
                authService.logout();
                System.out.println("Logged out successfully!");
                break;
            case 5:
                System.out.println("Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option!");
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
            System.out.println("Book added: " + book.getTitle());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void searchBooks() {
        System.out.println("\n--- Search Books ---");
        System.out.print("Enter search query: ");
        String query = scanner.nextLine();

        List<Book> results = catalogService.searchBooks(query);
        if (results.isEmpty()) {
            System.out.println("No books found.");
        } else {
            System.out.println("Found " + results.size() + " book(s):");
            for (Book book : results) {
                System.out.println("- " + book.getTitle() + " by " + book.getAuthor());
            }
        }
    }

    private void viewAllBooks() {
        System.out.println("\n--- All Books ---");
        List<Book> books = catalogService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in catalog.");
        } else {
            for (Book book : books) {
                System.out.println("- " + book.getTitle() + " by " + book.getAuthor()+ " (ISBN: " + book.getIsbn() + ")");
            }
        }
    }
}