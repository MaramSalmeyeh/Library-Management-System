package com.library.domain.model;

import org.junit.jupiter.api.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Loan entity - Sprint 2 (US2.1, US2.2, US2.3)
 */
class LoanTest {
    private static User testUser;
    private static Book testBook;
    private Loan loan;

    @BeforeAll
    static void setUpBeforeAll() {
        System.out.println("=== Setting up Loan tests ===");
        testUser = new User("U001", "Test User", "user@test.com");
        testBook = new Book("B001", "Test Book", "Test Author");
    }

    @AfterAll
    static void tearDownAfterAll() {
        System.out.println("=== Loan tests completed ===");
        testUser = null;
        testBook = null;
    }

    @BeforeEach
    void setUpBeforeEach() {
        loan = new Loan("L001", testUser, testBook, 28); // 28 days loan period
        System.out.println("Created new loan for: " + testBook.getTitle());
    }

    @AfterEach
    void tearDownAfterEach() {
        loan = null;
        System.out.println("Cleaned up loan instance");
    }

    @Test
    @DisplayName("US2.1 - Should create loan with correct properties and dates")
    void testLoanCreation() {
        // Then
        assertEquals("L001", loan.getId());
        assertEquals(testUser, loan.getUser());
        assertEquals(testBook, loan.getBook());
        assertNotNull(loan.getBorrowDate());
        assertNotNull(loan.getDueDate());
        assertNull(loan.getReturnDate());
        assertTrue(loan.isActive());
        assertFalse(loan.isOverdue());
    }

    @Test
    @DisplayName("US2.1 - Due date should be 28 days after borrow date")
    void testDueDateCalculation() {
        // Given
        LocalDate expectedDueDate = loan.getBorrowDate().plusDays(28);

        // Then
        assertEquals(expectedDueDate, loan.getDueDate(),
                "Due date should be exactly 28 days after borrow date");
    }

    @Test
    @DisplayName("US2.2 - Should calculate correct overdue days")
    void testOverdueDaysCalculation() {
        // Given - a loan that is not overdue
        assertFalse(loan.isOverdue());
        assertEquals(0, loan.getOverdueDays());

        // Note: Testing actual overdue would require date manipulation
        // This is tested in integration tests with mocking
    }

    @Test
    @DisplayName("US2.3 - Should calculate zero fine for non-overdue loan")
    void testFineCalculationForNonOverdue() {
        // Given - active, non-overdue loan
        assertTrue(loan.isActive());
        assertFalse(loan.isOverdue());

        // When
        double fine = loan.calculateFine();

        // Then
        assertEquals(0.0, fine, "Fine should be zero for non-overdue loan");
    }

    @Test
    @DisplayName("US2.1 - Should mark loan as returned")
    void testReturnLoan() {
        // Given - active loan
        assertTrue(loan.isActive());
        assertNull(loan.getReturnDate());

        // When
        loan.setReturnDate(LocalDate.now());

        // Then
        assertNotNull(loan.getReturnDate());
        assertFalse(loan.isActive());
        assertFalse(loan.isOverdue());
    }

    @Test
    @DisplayName("Should handle loan lifecycle correctly")
    void testLoanLifecycle() {
        // Phase 1: Active loan
        assertTrue(loan.isActive());
        assertFalse(loan.isOverdue());
        assertEquals(0.0, loan.calculateFine());

        // Phase 2: Returned loan
        loan.setReturnDate(LocalDate.now());
        assertFalse(loan.isActive());
        assertFalse(loan.isOverdue());
        assertEquals(0.0, loan.calculateFine());
    }

    @Nested
    @DisplayName("Loan Status Tests")
    class LoanStatusTests {

        @Test
        @DisplayName("Should correctly identify active status")
        void testActiveStatus() {
            assertTrue(loan.isActive());
            assertNull(loan.getReturnDate());
        }

        @Test
        @DisplayName("Should correctly identify returned status")
        void testReturnedStatus() {
            loan.setReturnDate(LocalDate.now());
            assertFalse(loan.isActive());
            assertNotNull(loan.getReturnDate());
        }
    }
}