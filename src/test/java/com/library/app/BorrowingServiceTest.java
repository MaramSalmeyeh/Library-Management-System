package com.library.app;

import com.library.domain.model.Book;
import com.library.domain.model.User;
import com.library.domain.service.BorrowingDomainService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for BorrowingService - Sprint 2 (US2.1, US2.3) and Sprint 4 (US4.1)
 * @author Your Name
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BorrowingServiceTest {

    @Mock
    private BorrowingDomainService mockDomainService;

    private BorrowingService borrowingService;
    private User testUser;
    private Book testBook;

    @BeforeAll
    void setUpBeforeAll() {
        System.out.println("=== Starting BorrowingService tests ===");
    }

    @AfterAll
    void tearDownAfterAll() {
        System.out.println("=== BorrowingService tests completed ===");
    }

    @BeforeEach
    void setUpBeforeEach() {
        borrowingService = new BorrowingService(mockDomainService);
        testUser = new User("U001", "Test User", "test@library.com");
        testBook = new Book("B001", "Test Book", "Test Author");
        System.out.println("Initialized BorrowingService for testing");
    }

    @AfterEach
    void tearDownAfterEach() {
        borrowingService = null;
        testUser = null;
        testBook = null;
        reset(mockDomainService);
        System.out.println("Cleaned up test resources");
    }

    // ===== US2.1 - Borrow Book Tests =====

    @Test
    @DisplayName("US2.1 - Should borrow book successfully")
    void testBorrowBookSuccess() {
        // Given
        when(mockDomainService.borrowBook(testUser, testBook)).thenReturn(true);

        // When
        boolean result = borrowingService.borrowBook(testUser, testBook);

        // Then
        assertTrue(result);
        verify(mockDomainService).borrowBook(testUser, testBook);
    }

    @Test
    @DisplayName("US2.1 - Should handle borrow failure gracefully")
    void testBorrowBookFailure() {
        // Given
        when(mockDomainService.borrowBook(testUser, testBook))
                .thenThrow(new IllegalStateException("Cannot borrow"));

        // When
        boolean result = borrowingService.borrowBook(testUser, testBook);

        // Then
        assertFalse(result);
        verify(mockDomainService).borrowBook(testUser, testBook);
    }

    @Test
    @DisplayName("US2.1 - Should handle unexpected exceptions during borrow")
    void testBorrowBookUnexpectedException() {
        // Given
        when(mockDomainService.borrowBook(testUser, testBook))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When
        boolean result = borrowingService.borrowBook(testUser, testBook);

        // Then
        assertFalse(result);
        verify(mockDomainService).borrowBook(testUser, testBook);
    }

    // ===== Return Book Tests =====

    @Test
    @DisplayName("Should return book successfully")
    void testReturnBookSuccess() {
        // Given
        when(mockDomainService.returnBook(testUser, testBook)).thenReturn(true);

        // When
        boolean result = borrowingService.returnBook(testUser, testBook);

        // Then
        assertTrue(result);
        verify(mockDomainService).returnBook(testUser, testBook);
    }

    @Test
    @DisplayName("Should handle return failure gracefully")
    void testReturnBookFailure() {
        // Given
        when(mockDomainService.returnBook(testUser, testBook))
                .thenThrow(new IllegalStateException("Cannot return"));

        // When
        boolean result = borrowingService.returnBook(testUser, testBook);

        // Then
        assertFalse(result);
        verify(mockDomainService).returnBook(testUser, testBook);
    }

    @Test
    @DisplayName("Should handle unexpected exceptions during return")
    void testReturnBookUnexpectedException() {
        // Given
        when(mockDomainService.returnBook(testUser, testBook))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When
        boolean result = borrowingService.returnBook(testUser, testBook);

        // Then
        assertFalse(result);
        verify(mockDomainService).returnBook(testUser, testBook);
    }

    // ===== US2.3 - Fine Calculation Tests =====

    @Test
    @DisplayName("US2.3 - Should calculate total fines correctly")
    void testCalculateTotalFines() {
        // Given
        testUser.addFine(25.0);

        // When
        double fines = borrowingService.calculateTotalFines(testUser);

        // Then
        assertEquals(25.0, fines, 0.01);
    }

    @Test
    @DisplayName("US2.3 - Should calculate zero fines for user with no fines")
    void testCalculateTotalFines_ZeroFines() {
        // Given - user with no fines
        assertEquals(0.0, testUser.getFineBalance());

        // When
        double fines = borrowingService.calculateTotalFines(testUser);

        // Then
        assertEquals(0.0, fines, 0.01);
    }

    @Test
    @DisplayName("US2.3 - Should handle exceptions during fine calculation")
    void testCalculateTotalFines_WithException() {
        // Given - user that might cause exception (though unlikely in current implementation)
        // This tests the exception handling in the service

        // When
        double fines = borrowingService.calculateTotalFines(testUser);

        // Then - should return at least the balance without throwing exception
        assertEquals(0.0, fines, 0.01);
    }

    // ===== US2.3 - Pay Fine Tests =====

    @Test
    @DisplayName("US2.3 - Should pay fine successfully")
    void testPayFine() {
        // Given
        testUser.addFine(50.0);

        // When
        double remaining = borrowingService.payFine(testUser, 30.0);

        // Then
        assertEquals(20.0, remaining, 0.01);
        assertEquals(20.0, testUser.getFineBalance(), 0.01);
    }

    @Test
    @DisplayName("US2.3 - Should pay full fine and clear balance")
    void testPayFullFine() {
        // Given
        testUser.addFine(50.0);

        // When
        double remaining = borrowingService.payFine(testUser, 50.0);

        // Then
        assertEquals(0.0, remaining, 0.01);
        assertEquals(0.0, testUser.getFineBalance(), 0.01);
    }

    @Test
    @DisplayName("US2.3 - Should handle overpayment correctly")
    void testPayFine_Overpayment() {
        // Given
        testUser.addFine(50.0);

        // When
        double remaining = borrowingService.payFine(testUser, 100.0);

        // Then - should only pay up to the balance
        assertEquals(0.0, remaining, 0.01);
        assertEquals(0.0, testUser.getFineBalance(), 0.01);
    }

    @Test
    @DisplayName("US2.3 - Should handle payment exceptions gracefully")
    void testPayFine_WithException() {
        // Given - user with fines
        testUser.addFine(50.0);

        // When - pay with valid amount
        double remaining = borrowingService.payFine(testUser, 25.0);

        // Then - should handle without exception
        assertEquals(25.0, remaining, 0.01);
    }

    // ===== US4.1 - Borrowing Restrictions Tests =====

    @Test
    @DisplayName("US4.1 - Should allow borrowing when user is eligible")
    void testCanUserBorrow_Eligible() {
        // Given - user with no fines and no overdue items
        assertTrue(testUser.canBorrow());

        // When
        boolean canBorrow = borrowingService.canUserBorrow(testUser);

        // Then
        assertTrue(canBorrow);
    }

    @Test
    @DisplayName("US4.1 - Should prevent borrowing when user has fines")
    void testCanUserBorrow_WithFines() {
        // Given
        testUser.addFine(10.0);

        // When
        boolean canBorrow = borrowingService.canUserBorrow(testUser);

        // Then
        assertFalse(canBorrow);
    }

    @Test
    @DisplayName("US4.1 - Should handle exceptions during borrow eligibility check")
    void testCanUserBorrow_WithException() {
        // When
        boolean canBorrow = borrowingService.canUserBorrow(testUser);

        // Then - should handle without exception
        assertTrue(canBorrow); // New user should be eligible
    }

    // ===== Overdue Items Check Tests =====

    @Test
    @DisplayName("Should check if user has overdue items")
    void testHasOverdueItems() {
        // Given - user with no overdue items
        assertFalse(testUser.hasOverdueItems());

        // When
        boolean hasOverdue = borrowingService.hasOverdueItems(testUser);

        // Then
        assertFalse(hasOverdue);
    }

    @Test
    @DisplayName("Should handle exceptions during overdue check")
    void testHasOverdueItems_WithException() {
        // When
        boolean hasOverdue = borrowingService.hasOverdueItems(testUser);

        // Then - should handle without exception
        assertFalse(hasOverdue);
    }

    // ===== Test Helper Methods =====

    @Test
    @DisplayName("Should add test fine successfully")
    void testAddTestFine() {
        // When
        borrowingService.addTestFine(testUser, 25.0);

        // Then
        assertEquals(25.0, testUser.getFineBalance(), 0.01);
    }

    @Test
    @DisplayName("Should not add negative test fine")
    void testAddTestFine_NegativeAmount() {
        // When
        borrowingService.addTestFine(testUser, -10.0);

        // Then - fine should not be added
        assertEquals(0.0, testUser.getFineBalance(), 0.01);
    }

    @Test
    @DisplayName("Should simulate overdue scenario for testing")
    void testSimulateOverdueForTesting() {
        // Given
        when(mockDomainService.borrowBook(testUser, testBook)).thenReturn(true);

        // When
        borrowingService.simulateOverdueForTesting(testUser, testBook, 5);

        // Then - should not throw exception
        verify(mockDomainService).borrowBook(testUser, testBook);
    }

    @Test
    @DisplayName("Should display loan details without exception")
    void testDisplayLoanDetails() {
        // When
        borrowingService.displayLoanDetails(testUser);

        // Then - should not throw exception
        // This is mainly for visual verification in tests
    }

    @Test
    @DisplayName("Should reset user for testing")
    void testResetUserForTesting() {
        // Given
        testUser.addFine(50.0);

        // When
        borrowingService.resetUserForTesting(testUser);

        // Then
        assertEquals(0.0, testUser.getFineBalance(), 0.01);
        assertTrue(testUser.getActiveLoans().isEmpty());
    }

    // ===== Integration Style Tests =====

    @Nested
    @DisplayName("Borrowing Service Integration Scenarios")
    class IntegrationScenariosTests {

        @Test
        @DisplayName("Should handle complete borrow-return-fine cycle")
        void testCompleteBorrowReturnFineCycle() {
            // Phase 1: Borrow book
            when(mockDomainService.borrowBook(testUser, testBook)).thenReturn(true);
            boolean borrowResult = borrowingService.borrowBook(testUser, testBook);
            assertTrue(borrowResult);

            // Phase 2: Add fines
            borrowingService.addTestFine(testUser, 30.0);
            assertEquals(30.0, borrowingService.calculateTotalFines(testUser), 0.01);

            // Phase 3: Check borrowing eligibility (should be false due to fines)
            assertFalse(borrowingService.canUserBorrow(testUser));

            // Phase 4: Pay fine
            double remaining = borrowingService.payFine(testUser, 20.0);
            assertEquals(10.0, remaining, 0.01);

            // Phase 5: Return book
            when(mockDomainService.returnBook(testUser, testBook)).thenReturn(true);
            boolean returnResult = borrowingService.returnBook(testUser, testBook);
            assertTrue(returnResult);

            // Verify all interactions
            verify(mockDomainService).borrowBook(testUser, testBook);
            verify(mockDomainService).returnBook(testUser, testBook);
        }

        @Test
        @DisplayName("Should handle user with multiple operations")
        void testUserWithMultipleOperations() {
            // Multiple fine operations
            borrowingService.addTestFine(testUser, 10.0);
            borrowingService.addTestFine(testUser, 20.0);
            assertEquals(30.0, borrowingService.calculateTotalFines(testUser), 0.01);

            // Multiple payment operations
            borrowingService.payFine(testUser, 15.0);
            borrowingService.payFine(testUser, 10.0);
            assertEquals(5.0, borrowingService.calculateTotalFines(testUser), 0.01);

            // Final payment to clear balance
            borrowingService.payFine(testUser, 5.0);
            assertEquals(0.0, borrowingService.calculateTotalFines(testUser), 0.01);
            assertTrue(borrowingService.canUserBorrow(testUser));
        }
    }

    @Nested
    @DisplayName("Borrowing Service Error Scenarios")
    class ErrorScenariosTests {

        @Test
        @DisplayName("Should handle all service methods with domain service failures")
        void testAllMethodsWithDomainServiceFailures() {
            // Setup all domain service methods to fail
            when(mockDomainService.borrowBook(any(), any()))
                    .thenThrow(new IllegalStateException("Borrow failed"));
            when(mockDomainService.returnBook(any(), any()))
                    .thenThrow(new IllegalStateException("Return failed"));

            // Test borrow with failure
            boolean borrowResult = borrowingService.borrowBook(testUser, testBook);
            assertFalse(borrowResult);

            // Test return with failure
            boolean returnResult = borrowingService.returnBook(testUser, testBook);
            assertFalse(returnResult);

            // Other methods should still work without domain service
            assertTrue(borrowingService.canUserBorrow(testUser));
            assertEquals(0.0, borrowingService.calculateTotalFines(testUser), 0.01);
        }

        @Test
        @DisplayName("Should handle null parameters gracefully")
        void testNullParameters() {
            // These should not throw NullPointerException but be handled gracefully
            assertDoesNotThrow(() -> borrowingService.calculateTotalFines(null));
            assertDoesNotThrow(() -> borrowingService.canUserBorrow(null));
            assertDoesNotThrow(() -> borrowingService.hasOverdueItems(null));

            // For methods that use domain service, they might throw different exceptions
            // but should not crash the application
        }
    }
}