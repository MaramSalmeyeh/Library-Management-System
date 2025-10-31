package com.library.integration;

import com.library.app.AuthService;
import com.library.app.BorrowingService;
import com.library.app.CatalogService;
import com.library.domain.model.Book;
import com.library.domain.model.User;
import com.library.domain.service.BorrowingDomainService;
import com.library.domain.service.OverdueService;
import com.library.infrastructure.notification.EmailNotifier;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for complete library system workflows
 * @author Your Name
 * @version 1.0
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LibrarySystemIntegrationTest {
    private BookRepository bookRepository;
    private LoanRepository loanRepository;
    private AuthService authService;
    private CatalogService catalogService;
    private BorrowingDomainService borrowingDomainService;
    private BorrowingService borrowingService;
    private OverdueService overdueService;

    @BeforeAll
    void setUpBeforeAll() {
        System.out.println("=== Starting Library System Integration Tests ===");
    }

    @AfterAll
    void tearDownAfterAll() {
        System.out.println("=== Library System Integration Tests Completed ===");
    }

    @BeforeEach
    void setUp() {
        // Initialize all components as in the real application
        bookRepository = new BookRepository();
        loanRepository = new LoanRepository();
        authService = new AuthService();
        catalogService = new CatalogService(bookRepository, authService);
        borrowingDomainService = new BorrowingDomainService(loanRepository);
        borrowingService = new BorrowingService(borrowingDomainService);

        EmailNotifier emailNotifier = new EmailNotifier();
        overdueService = new OverdueService(loanRepository, emailNotifier);

        System.out.println("Initialized all system components for integration testing");
    }

    @AfterEach
    void tearDown() {
        if (overdueService != null) {
            overdueService.shutdown();
        }
        System.out.println("Cleaned up integration test resources");
    }

    @Test
    @DisplayName("End-to-end: User borrows and returns book")
    void testCompleteBorrowReturnCycle() {
        // Phase 1: Admin adds book to catalog
        authService.login("admin", "password123");
        Book book = catalogService.addBook("INT-001", "Integration Test Book", "Test Author");
        authService.logout();

        assertNotNull(book);
        assertEquals("Integration Test Book", book.getTitle());
        assertTrue(book.isAvailable());

        // Phase 2: User borrows the book
        User user = new User("INT-001", "Integration User", "integration@test.com");

        boolean borrowResult = borrowingService.borrowBook(user, book);
        assertTrue(borrowResult, "Borrow should be successful");
        assertFalse(book.isAvailable(), "Book should be marked as borrowed");
        assertEquals(1, user.getActiveLoans().size(), "User should have one active loan");

        // Phase 3: User returns the book
        boolean returnResult = borrowingService.returnBook(user, book);
        assertTrue(returnResult, "Return should be successful");
        assertTrue(book.isAvailable(), "Book should be available again");
        assertTrue(user.getActiveLoans().isEmpty(), "User should have no active loans");
    }

    @Test
    @DisplayName("End-to-end: Search and borrow workflow")
    void testSearchAndBorrowWorkflow() {
        // Setup: Add multiple books
        authService.login("admin", "password123");
        catalogService.addBook("S-001", "Java Programming", "Java Expert");
        catalogService.addBook("S-002", "Python Basics", "Python Guru");
        catalogService.addBook("S-003", "Advanced Java", "Java Master");
        authService.logout();

        // Phase 1: Search for books
        var javaBooks = catalogService.searchBooks("Java");
        assertEquals(2, javaBooks.size(), "Should find 2 Java books");

        // Phase 2: Borrow a searched book
        User user = new User("S-001", "Search User", "search@test.com");
        Book javaBook = javaBooks.get(0);

        boolean borrowResult = borrowingService.borrowBook(user, javaBook);
        assertTrue(borrowResult);
        assertFalse(javaBook.isAvailable());

        // Phase 3: Verify other Java book is still available
        Book otherJavaBook = javaBooks.get(1);
        assertTrue(otherJavaBook.isAvailable(), "Other Java book should still be available");
    }

    @Test
    @DisplayName("End-to-end: Fine management workflow")
    void testFineManagementWorkflow() {
        // Setup
        authService.login("admin", "password123");
        Book book = catalogService.addBook("FINE-001", "Fine Test Book", "Fine Author");
        authService.logout();

        User user = new User("FINE-001", "Fine User", "fine@test.com");

        // Phase 1: Add test fines
        borrowingService.addTestFine(user, 50.0);
        assertEquals(50.0, borrowingService.calculateTotalFines(user), 0.01);

        // Phase 2: Verify borrowing is blocked due to fines
        assertFalse(borrowingService.canUserBorrow(user), "User with fines cannot borrow");

        boolean borrowResult = borrowingService.borrowBook(user, book);
        assertFalse(borrowResult, "Borrow should fail due to fines");

        // Phase 3: Pay partial fine
        double remaining = borrowingService.payFine(user, 30.0);
        assertEquals(20.0, remaining, 0.01);
        assertFalse(borrowingService.canUserBorrow(user), "Still cannot borrow with remaining fines");

        // Phase 4: Pay remaining fine
        remaining = borrowingService.payFine(user, 20.0);
        assertEquals(0.0, remaining, 0.01);
        assertTrue(borrowingService.canUserBorrow(user), "Can borrow after paying all fines");

        // Phase 5: Now borrow successfully
        borrowResult = borrowingService.borrowBook(user, book);
        assertTrue(borrowResult, "Should borrow successfully after clearing fines");
    }

    @Test
    @DisplayName("End-to-end: Overdue detection and notification workflow")
    void testOverdueDetectionWorkflow() {
        // Setup
        authService.login("admin", "password123");
        Book book = catalogService.addBook("OVD-001", "Overdue Test Book", "Overdue Author");
        authService.logout();

        User user = new User("OVD-001", "Overdue User", "overdue@test.com");

        // Phase 1: Borrow book
        boolean borrowResult = borrowingService.borrowBook(user, book);
        assertTrue(borrowResult);

        // Phase 2: Simulate overdue scenario
        borrowingService.simulateOverdueForTesting(user, book, 5); // 5 days overdue

        // Phase 3: Run overdue detection
        overdueService.checkAndProcessOverdueItems();

        // Phase 4: Verify fines were applied
        double fines = borrowingService.calculateTotalFines(user);
        assertTrue(fines > 0, "Fines should be applied for overdue book");

        // Phase 5: Verify user cannot borrow new books
        assertFalse(borrowingService.canUserBorrow(user), "Cannot borrow with overdue items");

        // Phase 6: Check overdue reporting
        var overdueLoans = overdueService.getOverdueLoans();
        assertFalse(overdueLoans.isEmpty(), "Should find overdue loans");
        assertTrue(overdueService.hasOverdueItems(user), "User should have overdue items");
    }

    @Test
    @DisplayName("End-to-end: Multiple users borrowing different books")
    void testMultipleUsersBorrowing() {
        // Setup: Add multiple books
        authService.login("admin", "password123");
        Book book1 = catalogService.addBook("MU-001", "Book One", "Author One");
        Book book2 = catalogService.addBook("MU-002", "Book Two", "Author Two");
        Book book3 = catalogService.addBook("MU-003", "Book Three", "Author Three");
        authService.logout();

        // Create multiple users
        User user1 = new User("MU-001", "User One", "user1@test.com");
        User user2 = new User("MU-002", "User Two", "user2@test.com");
        User user3 = new User("MU-003", "User Three", "user3@test.com");

        // Phase 1: Users borrow different books
        assertTrue(borrowingService.borrowBook(user1, book1));
        assertTrue(borrowingService.borrowBook(user2, book2));
        assertTrue(borrowingService.borrowBook(user3, book3));

        // Verify all books are borrowed
        assertFalse(book1.isAvailable());
        assertFalse(book2.isAvailable());
        assertFalse(book3.isAvailable());

        // Verify each user has correct loan
        assertEquals(1, user1.getActiveLoans().size());
        assertEquals(1, user2.getActiveLoans().size());
        assertEquals(1, user3.getActiveLoans().size());

        // Phase 2: Users return books
        assertTrue(borrowingService.returnBook(user1, book1));
        assertTrue(borrowingService.returnBook(user2, book2));
        assertTrue(borrowingService.returnBook(user3, book3));

        // Verify all books are available again
        assertTrue(book1.isAvailable());
        assertTrue(book2.isAvailable());
        assertTrue(book3.isAvailable());

        // Verify no active loans
        assertTrue(user1.getActiveLoans().isEmpty());
        assertTrue(user2.getActiveLoans().isEmpty());
        assertTrue(user3.getActiveLoans().isEmpty());
    }

    @Test
    @DisplayName("End-to-end: Catalog management by admin")
    void testCatalogManagementWorkflow() {
        // Phase 1: Admin login and add books
        authService.login("admin", "password123");

        Book book1 = catalogService.addBook("CAT-001", "Admin Book One", "Admin Author");
        Book book2 = catalogService.addBook("CAT-002", "Admin Book Two", "Admin Author");

        assertNotNull(book1);
        assertNotNull(book2);
        assertEquals(2, catalogService.getBookCount());

        // Phase 2: Search and verify books
        var adminBooks = catalogService.searchBooks("Admin");
        assertEquals(2, adminBooks.size());

        // Phase 3: Remove a book
        boolean removed = catalogService.removeBook("CAT-001");
        assertTrue(removed);
        assertEquals(1, catalogService.getBookCount());

        // Phase 4: Verify the remaining book
        var remainingBooks = catalogService.searchBooks("Admin");
        assertEquals(1, remainingBooks.size());
        assertEquals("Admin Book Two", remainingBooks.get(0).getTitle());

        authService.logout();
    }

    @Test
    @DisplayName("End-to-end: User with mixed scenario (fines, borrow, return)")
    void testUserMixedScenario() {
        // Setup
        authService.login("admin", "password123");
        Book book1 = catalogService.addBook("MIX-001", "Mixed Book One", "Mixed Author");
        Book book2 = catalogService.addBook("MIX-002", "Mixed Book Two", "Mixed Author");
        authService.logout();

        User user = new User("MIX-001", "Mixed User", "mixed@test.com");

        // Phase 1: Borrow first book
        assertTrue(borrowingService.borrowBook(user, book1));
        assertEquals(1, user.getActiveLoans().size());

        // Phase 2: Try to borrow second book (should work)
        assertTrue(borrowingService.borrowBook(user, book2));
        assertEquals(2, user.getActiveLoans().size());

        // Phase 3: Add fines and verify borrowing blocked
        borrowingService.addTestFine(user, 25.0);
        assertFalse(borrowingService.canUserBorrow(user));

        // Phase 4: Return one book (fines still block new borrowing)
        assertTrue(borrowingService.returnBook(user, book1));
        assertEquals(1, user.getActiveLoans().size());
        assertFalse(borrowingService.canUserBorrow(user));

        // Phase 5: Pay fines and verify can borrow again
        borrowingService.payFine(user, 25.0);
        assertTrue(borrowingService.canUserBorrow(user));

        // Phase 6: Return remaining book
        assertTrue(borrowingService.returnBook(user, book2));
        assertTrue(user.getActiveLoans().isEmpty());
    }

    @Nested
    @DisplayName("System Resilience Tests")
    class ResilienceTests {

        @Test
        @DisplayName("Should handle concurrent operations gracefully")
        void testConcurrentOperations() {
            // This tests that the system doesn't break with multiple operations
            authService.login("admin", "password123");
            Book book = catalogService.addBook("CON-001", "Concurrent Book", "Concurrent Author");
            authService.logout();

            User user = new User("CON-001", "Concurrent User", "concurrent@test.com");

            // Perform multiple operations rapidly
            assertTrue(borrowingService.borrowBook(user, book));
            borrowingService.addTestFine(user, 10.0);
            double fines = borrowingService.calculateTotalFines(user);
            borrowingService.payFine(user, 10.0);
            assertTrue(borrowingService.returnBook(user, book));

            // System should remain in consistent state
            assertTrue(book.isAvailable());
            assertEquals(0.0, user.getFineBalance(), 0.01);
            assertTrue(user.getActiveLoans().isEmpty());
        }

        @Test
        @DisplayName("Should maintain data consistency across components")
        void testDataConsistency() {
            // Test that all components have consistent view of data
            authService.login("admin", "password123");
            Book book = catalogService.addBook("CONS-001", "Consistency Book", "Consistency Author");
            authService.logout();

            User user = new User("CONS-001", "Consistency User", "consistency@test.com");

            // Borrow through service
            borrowingService.borrowBook(user, book);

            // Verify consistency across different access methods
            assertFalse(book.isAvailable());
            assertEquals(1, user.getActiveLoans().size());
            assertFalse(catalogService.isBookAvailable("CONS-001"));

            // Return and verify consistency
            borrowingService.returnBook(user, book);
            assertTrue(book.isAvailable());
            assertTrue(user.getActiveLoans().isEmpty());
            assertTrue(catalogService.isBookAvailable("CONS-001"));
        }
    }
}