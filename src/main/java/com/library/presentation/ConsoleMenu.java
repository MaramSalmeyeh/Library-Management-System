package com.library.presentation;

import com.library.domain.Admin;
import com.library.domain.Book;
import com.library.service.AuthService;
import com.library.service.BookService;

import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    private final AuthService authService;
    private final BookService bookService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(AuthService authService, BookService bookService) {
        this.authService = authService;
        this.bookService = bookService;
    }

    public void run() {
        boolean running = true;

        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleAdminLogin();
                    break;
                case "2":
                    handleAdminLogout();
                    break;
                case "3":
                    handleAddBook();
                    break;
                case "4":
                    handleSearchBook();
                    break;
                case "5":
                    System.out.println("Exiting... Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n Welcome to our Library System ");
        System.out.println("1. Admin login");
        System.out.println("2. Admin logout");
        System.out.println("3. Add book (admin only)");
        System.out.println("4. Search book");
        System.out.println("5. Exit");
        System.out.print("Choose option: ");
    }



    private void handleAdminLogin() {
        if (authService.isAdminLoggedIn()) {
            System.out.println("An admin is already logged in.");
            return;
        }

        System.out.print("Enter admin email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        Admin admin = authService.login(email, password);
        if (admin != null) {
            System.out.println("Login successful. Welcome, " + admin.getName() + "!");
        } else {
            System.out.println("Invalid credentials. Login failed.");
        }
    }

    private void handleAdminLogout() {
        if (!authService.isAdminLoggedIn()) {
            System.out.println("No admin is currently logged in.");
            return;
        }
        authService.logout();
        System.out.println("Admin logged out successfully.");
    }



    private void handleAddBook() {
        if (!authService.isAdminLoggedIn()) {
            System.out.println("You must login as admin to add books.");
            return;
        }

        System.out.print("Enter book title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter book author: ");
        String author = scanner.nextLine().trim();

        System.out.print("Enter book ISBN: ");
        String isbn = scanner.nextLine().trim();

        Book book = bookService.addBook(title, author, isbn);
        if (book == null) {
            System.out.println("A book with this ISBN already exists. No book added.");
        } else {
            System.out.println("Book added successfully with ID: " + book.getId());
        }
    }




    private void handleSearchBook() {
        System.out.println("\nSearch by:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. ISBN");
        System.out.print("Choose option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.print("Enter part of title: ");
                searchAndPrintBooks(bookService.searchByTitle(scanner.nextLine().trim()));
                break;
            case "2":
                System.out.print("Enter part of author name: ");
                searchAndPrintBooks(bookService.searchByAuthor(scanner.nextLine().trim()));
                break;
            case "3":
                System.out.print("Enter ISBN: ");
                Book book = bookService.searchByIsbn(scanner.nextLine().trim());
                if (book != null) {
                    printBook(book);
                } else {
                    System.out.println("No book found with that ISBN.");
                }
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void searchAndPrintBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("No matching books found.");
            return;
        }
        System.out.println("\n=== Search Results ===");
        for (Book b : books) {
            printBook(b);
        }
    }

    private void printBook(Book b) {
        System.out.println("- ID: " + b.getId()
                + " | Title: " + b.getTitle()
                + " | Author: " + b.getAuthor()
                + " | ISBN: " + b.getIsbn()
                + " | Borrowed: " + (b.isBorrowed() ? "Yes" : "No"));
    }
}
