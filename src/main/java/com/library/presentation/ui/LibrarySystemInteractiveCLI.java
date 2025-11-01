package com.library.presentation.ui;

import com.library.app.AuthService;
import com.library.app.BorrowingService;
import com.library.app.CatalogService;
import com.library.app.FineService;
import com.library.domain.model.Admin;
import com.library.domain.model.Book;
import com.library.domain.model.Loan;
import com.library.domain.model.User;
import com.library.domain.service.OverdueService;
import com.library.infrastructure.InMemoryFineRepository;
import com.library.infrastructure.InMemoryLoanRepository;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Interactive CLI to manually test Sprint 1 & 2 features.
 * Commands: help, login, logout, add, search, list, borrow, return, loans, overdue, balance, pay, date, exit
 *
 * Tips:
 *   add Title|Author|ISBN
 *   search java
 *   borrow ISBN-1
 *   return ISBN-1
 *   date set 2025-02-10
 */
public class LibrarySystemInteractiveCLI {

    // ===== Data / Services =====
    private final List<Book> catalog = new ArrayList<>();
    private final InMemoryLoanRepository loanRepo = new InMemoryLoanRepository();
    private final InMemoryFineRepository fineRepo = new InMemoryFineRepository();

    private final AuthService authService = new AuthService();
    private final CatalogService catalogService = new CatalogService();
    private BorrowingService borrowingService;   // depends on clock
    private OverdueService overdueService;       // depends on clock
    private final FineService fineService = new FineService(fineRepo);

    // Admin + one demo user for Sprint 2
    private final Admin admin = new Admin("admin", "1234");
    private final User user = new User("U1", "Aseel");

    // Clock management (so you can change "today")
    private Clock clock = Clock.systemDefaultZone();

    // ======= Entry =======
    public static void main(String[] args) {
        new LibrarySystemInteractiveCLI().run();
    }

    private void run() {
        updateClock(clock); // init services with system clock

        System.out.println("===== Library System — Interactive CLI (Sprint 1 & 2) =====");
        System.out.println("type 'help' to see commands.\n");

        // a couple of sample books (optional)
        catalog.add(new Book("Clean Code", "Robert C. Martin", "9780132350884"));
        catalog.add(new Book("Engineering Math", "Mike Brown", "ISBN-1"));

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String cmd = parts[0].toLowerCase(Locale.ROOT);
            String arg = parts.length > 1 ? parts[1] : "";

            try {
                switch (cmd) {
                    case "help" -> printHelp();
                    case "login" -> doLogin(arg);
                    case "logout" -> doLogout();
                    case "add" -> doAdd(arg);
                    case "search" -> doSearch(arg);
                    case "list" -> doList();
                    case "borrow" -> doBorrow(arg);
                    case "return" -> doReturn(arg);
                    case "loans" -> doLoans();
                    case "overdue" -> doOverdue();
                    case "balance" -> doBalance();
                    case "pay" -> doPay(arg);
                    case "date" -> doDate(arg);
                    case "exit", "quit" -> { System.out.println("bye!"); return; }
                    default -> System.out.println("Unknown command. type 'help'.");
                }
            } catch (Exception ex) {
                System.out.println("⚠️ " + ex.getMessage());
            }
        }
    }

    // ===== Commands =====

    private void printHelp() {
        System.out.println("""
                Commands:
                  help                         - show this help
                  login <user> <pass>          - Admin login  (ex: login admin 1234)
                  logout                       - Admin logout
                  add Title|Author|ISBN        - Add a book (admin must be logged in)
                  list                         - List all books
                  search <query>               - Search by title/author/isbn
                  borrow <isbn>                - Borrow ISBN for user 'Aseel'
                  return <isbn>                - Return ISBN for user 'Aseel'
                  loans                        - Show current loans
                  overdue                      - List overdue loans (based on 'today')
                  balance                      - Show fine balance for 'Aseel'
                  pay <amount>                 - Pay part/all fine (e.g., pay 10)
                  date show                    - Show current 'today' used by services
                  date set YYYY-MM-DD          - Change 'today' (useful to simulate overdue)
                  exit                         - quit
                """);
    }

    // Sprint 1
    private void doLogin(String arg) {
        String[] p = arg.split("\\s+");
        if (p.length != 2) { System.out.println("usage: login <username> <password>"); return; }
        boolean ok = authService.login(admin, p[0], p[1]);
        System.out.println(ok ? "✅ login success" : "❌ invalid credentials");
    }

    private void doLogout() {
        authService.logout(admin);
        System.out.println("👋 logged out.");
    }

    private void doAdd(String arg) {
        if (!admin.isLoggedIn()) { System.out.println("⚠️ please login as admin first."); return; }
        String[] p = arg.split("\\|");
        if (p.length != 3) { System.out.println("usage: add Title|Author|ISBN"); return; }
        Book b = new Book(p[0].trim(), p[1].trim(), p[2].trim());
        catalogService.addBook(admin, catalog, b);
        System.out.println("✅ added: " + b.getTitle());
    }

    private void doSearch(String arg) {
        String q = arg.trim();
        if (q.isEmpty()) { System.out.println("usage: search <query>"); return; }
        var res = catalog.stream().filter(b -> b.matches(q)).toList();
        if (res.isEmpty()) { System.out.println("(no matches)"); return; }
        res.forEach(b -> System.out.printf(" - %s — %s (ISBN: %s)%n", b.getTitle(), b.getAuthor(), b.getIsbn()));
    }

    private void doList() {
        if (catalog.isEmpty()) { System.out.println("(empty catalog)"); return; }
        for (Book b : catalog) {
            System.out.printf(" - %-30s | %-18s | %-12s | %s%n",
                    b.getTitle(), b.getAuthor(), b.getIsbn(), b.isAvailable() ? "AVAILABLE" : "BORROWED");
        }
    }

    // Sprint 2
    private void doBorrow(String arg) {
        String isbn = arg.trim();
        if (isbn.isEmpty()) { System.out.println("usage: borrow <isbn>"); return; }
        Book b = findByIsbn(isbn);
        if (b == null) { System.out.println("❌ book not found: " + isbn); return; }
        Loan l = borrowingService.borrow(user, b);
        System.out.println("✅ borrowed on " + l.getBorrowDate() + ", due " + l.getDueDate());
    }

    private void doReturn(String arg) {
        String isbn = arg.trim();
        if (isbn.isEmpty()) { System.out.println("usage: return <isbn>"); return; }
        Book b = findByIsbn(isbn);
        if (b == null) { System.out.println("❌ book not found: " + isbn); return; }
        borrowingService.returnBook(user, b);
        System.out.println("✅ returned.");
    }

    private void doLoans() {
        var all = loanRepo.findAll();
        if (all.isEmpty()) { System.out.println("(no loans)"); return; }
        for (Loan l : all) {
            System.out.printf(" - %s | user=%s | borrowed=%s | due=%s | returned=%s%n",
                    l.getBook().getIsbn(), l.getUser().getName(),
                    l.getBorrowDate(), l.getDueDate(), l.isReturned());
        }
    }

    private void doOverdue() {
        var ods = overdueService.findOverdues(loanRepo.findAll());
        if (ods.isEmpty()) { System.out.println("(no overdue loans)"); return; }
        for (Loan l : ods) {
            long days = Duration.between(l.getDueDate().atStartOfDay(ZoneId.systemDefault()).toInstant(),
                    LocalDate.now(clock).atStartOfDay(ZoneId.systemDefault()).toInstant()).toDays();
            System.out.printf(" - %s overdue by %d day(s)%n", l.getBook().getIsbn(), Math.max(days, 0));
        }
    }

    private void doBalance() {
        System.out.println("Balance = " + fineService.balance(user) + " NIS");
    }

    private void doPay(String arg) {
        try {
            double amt = Double.parseDouble(arg.trim());
            fineService.pay(user, amt);
            System.out.println("Paid " + amt + " → balance = " + fineService.balance(user));
        } catch (NumberFormatException e) {
            System.out.println("usage: pay <amount>");
        }
    }

    // Date management (so you can simulate overdue easily)
    private void doDate(String arg) {
        String[] p = arg.split("\\s+");
        if (p.length == 0 || p[0].isBlank() || p[0].equals("show")) {
            System.out.println("today = " + LocalDate.now(clock));
            System.out.println("commands: date show | date set YYYY-MM-DD");
            return;
        }
        if (p[0].equals("set")) {
            if (p.length != 2) { System.out.println("usage: date set YYYY-MM-DD"); return; }
            try {
                LocalDate d = LocalDate.parse(p[1], DateTimeFormatter.ISO_LOCAL_DATE);
                Clock fixed = Clock.fixed(d.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
                updateClock(fixed);
                System.out.println("✅ date set to " + d);
            } catch (Exception e) {
                System.out.println("invalid date. use YYYY-MM-DD");
            }
            return;
        }
        System.out.println("usage: date show | date set YYYY-MM-DD");
    }

    // ===== Helpers =====
    private void updateClock(Clock newClock) {
        this.clock = newClock;
        this.borrowingService = new BorrowingService(loanRepo, this.clock);
        this.overdueService = new OverdueService(this.clock);
    }

    private Book findByIsbn(String isbn) {
        for (Book b : catalog) if (b.getIsbn().equals(isbn)) return b;
        return null;
    }
}
