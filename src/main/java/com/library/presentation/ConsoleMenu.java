package com.library.presentation;

import com.library.domain.Admin;
import com.library.domain.Book;
import com.library.domain.Librarian;
import com.library.domain.Loan;
import com.library.service.*;

import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    private final AuthService authService;
    private final BookService bookService;
    private final LoanService loanService;
    private final FineService fineService;
    private final BorrowingService borrowingService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(AuthService authService,
                       BookService bookService,
                       LoanService loanService,
                       FineService fineService) {
        this.authService = authService;
        this.bookService = bookService;
        this.loanService = loanService;
        this.fineService = fineService;
        this.borrowingService = new BorrowingService(loanService, fineService);
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
                    handleLibrarianLogin();
                    break;
                case "3":
                    handleLogout();
                    break;
                case "4":
                    handleAddBook();
                    break;
                case "5":
                    handleSearchBook();
                    break;
                case "6":
                    handleBorrowBook();
                    break;
                case "7":
                    handleViewOverdueLoans();
                    break;
                case "8":
                    handlePayFine();
                    break;
                case "9":
                    System.out.println("Exiting... Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }

        }
    }

    private void printMainMenu() {
        System.out.println("\n=== Library System ===");
        System.out.println("1. Admin login");
        System.out.println("2. Librarian login");
        System.out.println("3. Logout");
        System.out.println("4. Add book (admin only)");
        System.out.println("5. Search book");
        System.out.println("6. Borrow book");
        System.out.println("7. View overdue loans (librarian only)");
        System.out.println("8. Pay fine");
        System.out.println("9. Exit");
        System.out.print("Choose option: ");
    }


    // ===== Admin login =====

    private void handleAdminLogin() {
        if (authService.isAdminLoggedIn()) {
            System.out.println("An admin is already logged in: "
                    + authService.getCurrentAdmin().getName());
            return;
        }

        System.out.print("Enter admin email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        Admin admin = authService.login(email, password);
        if (admin != null) {
            System.out.println("Admin login successful. Welcome, " + admin.getName() + "!");
        } else {
            System.out.println("Invalid admin credentials. Login failed.");
        }
    }

    // ===== Librarian login =====

    private void handleLibrarianLogin() {
        if (authService.isLibrarianLoggedIn()) {
            System.out.println("A librarian is already logged in: "
                    + authService.getCurrentLibrarian().getName());
            return;
        }

        System.out.print("Enter librarian email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        Librarian librarian = authService.loginLibrarian(email, password);
        if (librarian != null) {
            System.out.println("Librarian login successful. Welcome, " + librarian.getName() + "!");
        } else {
            System.out.println("Invalid librarian credentials. Login failed.");
        }
    }

    // ===== Logout =====

    private void handleLogout() {
        if (!authService.isAdminLoggedIn() && !authService.isLibrarianLoggedIn()) {
            System.out.println("No admin or librarian is currently logged in.");
            return;
        }
        authService.logout();
        System.out.println("Logout successful.");
    }

    // ===== Add book (admin only, Sprint 1) =====

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

    // ===== Search book (Sprint 1) =====

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

    // ===== Borrow book (user) – Sprint 2: US2.1 =====

    // ===== Borrow book (user) – Sprint 2: US2.1 + Rule from US2.3 =====

    private void handleBorrowBook() {
        System.out.println("\n=== Borrow Book ===");
        System.out.print("Enter your user ID: ");
        String userId = scanner.nextLine().trim();

        System.out.print("Enter book ID to borrow (e.g., B1): ");
        String bookId = scanner.nextLine().trim();

        try {
            // الآن نستخدم BorrowingService الذي يفحص الغرامات قبل الاستعارة
            Loan loan = borrowingService.borrowBook(userId, bookId);
            System.out.println("Book borrowed successfully with loan ID: " + loan.getId());
            System.out.println("Borrow date: " + loan.getBorrowDate()
                    + ", Due date: " + loan.getDueDate());
        } catch (IllegalStateException e) {
            // مثلاً: "User has unpaid fines (...) ..."
            System.out.println("Could not borrow book: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // مثل كتاب غير موجود
            System.out.println("Error: " + e.getMessage());
        }
    }


    // ===== View overdue loans (librarian only) – Sprint 2: US2.2 =====

    private void handleViewOverdueLoans() {
        if (!authService.isLibrarianLoggedIn()) {
            System.out.println("You must login as librarian to view overdue loans.");
            return;
        }

        List<Loan> overdue = loanService.getOverdueLoans();
        if (overdue.isEmpty()) {
            System.out.println("No overdue loans.");
            return;
        }

        System.out.println("\n=== Overdue Loans ===");
        for (Loan loan : overdue) {
            System.out.println("- Loan ID: " + loan.getId()
                    + " | User ID: " + loan.getUserId()
                    + " | Book ID: " + loan.getBookId()
                    + " | Borrow date: " + loan.getBorrowDate()
                    + " | Due date: " + loan.getDueDate());
        }
    }




    private void handlePayFine() {
        System.out.println("\n=== Pay Fine ===");
        System.out.print("Enter your user ID: ");
        String userId = scanner.nextLine().trim();

        double balance = fineService.getUserOutstandingBalance(userId);
        if (balance <= 0) {
            System.out.println("You have no outstanding fines.");
            return;
        }

        System.out.println("Your current outstanding fines = " + balance + " NIS");
        System.out.print("Enter amount to pay: ");

        String input = scanner.nextLine().trim();
        double amount;
        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }

        double newBalance = fineService.payFine(userId, amount);
        System.out.println("Payment processed. Remaining balance = " + newBalance + " NIS");

        if (newBalance == 0) {
            System.out.println("All fines are fully paid. You have regained borrowing rights (rule enforced in Sprint 4).");
        }
    }

}
