package com.library.domain.service;

import com.library.domain.model.Book;
import com.library.domain.model.Loan;
import com.library.domain.model.User;
import com.library.infrastructure.notification.Observer;
import com.library.repository.LoanRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for OverdueService - Sprint 2 (US2.2) and Sprint 3 (US3.1)
 * @author Your Name
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OverdueServiceTest {

    @Mock
    private LoanRepository mockLoanRepository;

    @Mock
    private Observer mockNotifier;

    private OverdueService overdueService;
    private User testUser;
    private Book testBook;
    private Loan testLoan;

    @BeforeAll
    void setUpBeforeAll() {
        System.out.println("=== Starting OverdueService tests ===");
    }

    @AfterAll
    void tearDownAfterAll() {
        System.out.println("=== OverdueService tests completed ===");
    }

    @BeforeEach
    void setUpBeforeEach() {
        overdueService = new OverdueService(mockLoanRepository, mockNotifier);
        testUser = new User("U001", "Test User", "test@library.com");
        testBook = new Book("B001", "Test Book", "Test Author");
        testLoan = new Loan("L001", testUser, testBook, 28);

        System.out.println("Initialized OverdueService for testing");
    }

    @AfterEach
    void tearDownAfterEach() {
        if (overdueService != null) {
            overdueService.shutdown();
        }
        System.out.println("Cleaned up test resources");
    }

    // ===== US2.2 - Overdue Detection Tests =====

    @Test
    @DisplayName("US2.2 - Should detect and process overdue loans")
    void testCheckAndProcessOverdueItems_WithOverdueLoans() {
        // Given
        Loan overdueLoan = createOverdueLoan(5); // 5 days overdue
        List<Loan> activeLoans = Arrays.asList(overdueLoan, testLoan);

        when(mockLoanRepository.findActiveLoans()).thenReturn(activeLoans);

        // When
        overdueService.checkAndProcessOverdueItems();

        // Then
        verify(mockNotifier, times(1)).notify(eq(testUser), contains("OVERDUE NOTICE"));
        verify(mockLoanRepository, atLeastOnce()).findActiveLoans();
    }

    @Test
    @DisplayName("US2.2 - Should handle no overdue loans gracefully")
    void testCheckAndProcessOverdueItems_NoOverdueLoans() {
        // Given - only non-overdue loans
        List<Loan> activeLoans = Collections.singletonList(testLoan);
        when(mockLoanRepository.findActiveLoans()).thenReturn(activeLoans);

        // When
        overdueService.checkAndProcessOverdueItems();

        // Then
        verify(mockNotifier, never()).notify(any(), anyString());
        verify(mockLoanRepository).findActiveLoans();
    }

    @Test
    @DisplayName("US2.2 - Should handle empty loan list")
    void testCheckAndProcessOverdueItems_EmptyLoanList() {
        // Given
        when(mockLoanRepository.findActiveLoans()).thenReturn(Collections.emptyList());

        // When
        overdueService.checkAndProcessOverdueItems();

        // Then
        verify(mockNotifier, never()).notify(any(), anyString());
        verify(mockLoanRepository).findActiveLoans();
    }

    @Test
    @DisplayName("US2.2 - Should apply correct fine calculation for overdue days")
    void testProcessOverdueLoan_FineCalculation() {
        // Given
        int overdueDays = 10;
        Loan overdueLoan = createOverdueLoan(overdueDays);
        List<Loan> activeLoans = Collections.singletonList(overdueLoan);

        when(mockLoanRepository.findActiveLoans()).thenReturn(activeLoans);

        // When
        overdueService.checkAndProcessOverdueItems();

        // Then
        double expectedFine = overdueDays * 10.0; // 10 NIS per day for books
        assertEquals(expectedFine, testUser.getFineBalance(), 0.01);
        verify(mockNotifier).notify(eq(testUser), contains(String.valueOf(overdueDays)));
    }

    @Test
    @DisplayName("US2.2 - Should process multiple overdue loans for same user")
    void testProcessMultipleOverdueLoans_SameUser() {
        // Given
        Book book2 = new Book("B002", "Another Book", "Another Author");
        Loan overdueLoan1 = createOverdueLoan(5);
        Loan overdueLoan2 = createOverdueLoan(8, book2);

        List<Loan> activeLoans = Arrays.asList(overdueLoan1, overdueLoan2);
        when(mockLoanRepository.findActiveLoans()).thenReturn(activeLoans);

        // When
        overdueService.checkAndProcessOverdueItems();

        // Then
        double expectedTotalFine = (5 * 10.0) + (8 * 10.0);
        assertEquals(expectedTotalFine, testUser.getFineBalance(), 0.01);
        verify(mockNotifier, times(2)).notify(eq(testUser), anyString());
    }

    @Test
    @DisplayName("US2.2 - Should process overdue loans for different users")
    void testProcessOverdueLoans_DifferentUsers() {
        // Given
        User user2 = new User("U002", "Another User", "user2@library.com");
        Book book2 = new Book("B002", "Another Book", "Another Author");
        Loan overdueLoan1 = createOverdueLoan(5);
        Loan overdueLoan2 = createOverdueLoan(8, user2, book2);

        List<Loan> activeLoans = Arrays.asList(overdueLoan1, overdueLoan2);
        when(mockLoanRepository.findActiveLoans()).thenReturn(activeLoans);

        // When
        overdueService.checkAndProcessOverdueItems();

        // Then
        assertEquals(50.0, testUser.getFineBalance(), 0.01); // 5 days * 10 NIS
        assertEquals(80.0, user2.getFineBalance(), 0.01);    // 8 days * 10 NIS

        verify(mockNotifier).notify(eq(testUser), anyString());
        verify(mockNotifier).notify(eq(user2), anyString());
    }

    // ===== US3.1 - Notification Tests =====

    @Test
    @DisplayName("US3.1 - Should send notification for overdue items")
    void testSendOverdueNotification() {
        // Given
        Loan overdueLoan = createOverdueLoan(7);
        List<Loan> activeLoans = Collections.singletonList(overdueLoan);
        when(mockLoanRepository.findActiveLoans()).thenReturn(activeLoans);

        // When
        overdueService.checkAndProcessOverdueItems();

        // Then
        verify(mockNotifier).notify(eq(testUser), contains("OVERDUE NOTICE"));
        verify(mockNotifier).notify(eq(testUser), contains("Test Book"));
        verify(mockNotifier).notify(eq(testUser), contains("7 days"));
    }

    @Test
    @DisplayName("US3.1 - Should include correct book details in notification")
    void testNotificationContainsCorrectBookDetails() {
        // Given
        Book specialBook = new Book("SPECIAL123", "Advanced Java Programming", "Expert Author");
        Loan overdueLoan = createOverdueLoan(3, specialBook);
        List<Loan> activeLoans = Collections.singletonList(overdueLoan);
        when(mockLoanRepository.findActiveLoans()).thenReturn(activeLoans);

        // When
        overdueService.checkAndProcessOverdueItems();

        // Then
        verify(mockNotifier).notify(eq(testUser), contains("Advanced Java Programming"));
        verify(mockNotifier).notify(eq(testUser), contains("3 days"));
    }

    // ===== Automatic Scanning Tests =====

    @Test
    @DisplayName("Should start automatic overdue scanning")
    void testStartAutomaticOverdueScanning() {
        // When
        overdueService.startAutomaticOverdueScanning();

        // Then
        assertTrue(overdueService.isScannerRunning());

        // Clean up
        overdueService.stopAutomaticOverdueScanning();
    }

    @Test
    @DisplayName("Should stop automatic overdue scanning")
    void testStopAutomaticOverdueScanning() {
        // Given
        overdueService.startAutomaticOverdueScanning();
        assertTrue(overdueService.isScannerRunning());

        // When
        overdueService.stopAutomaticOverdueScanning();

        // Then
        assertFalse(overdueService.isScannerRunning());
    }

    @Test
    @DisplayName("Should not start scanner if already running")
    void testStartScanner_WhenAlreadyRunning() {
        // Given
        overdueService.startAutomaticOverdueScanning();
        assertTrue(overdueService.isScannerRunning());

        // When
        overdueService.startAutomaticOverdueScanning(); // Second call

        // Then
        assertTrue(overdueService.isScannerRunning());
        // Should not throw exception or create multiple scanners

        // Clean up
        overdueService.stopAutomaticOverdueScanning();
    }

    @Test
    @DisplayName("Should not stop scanner if not running")
    void testStopScanner_WhenNotRunning() {
        // Given
        assertFalse(overdueService.isScannerRunning());

        // When
        overdueService.stopAutomaticOverdueScanning(); // Stop when not running

        // Then
        assertFalse(overdueService.isScannerRunning());
        // Should not throw exception
    }

    // ===== Overdue Reporting Tests =====

    @Test
    @DisplayName("Should get all overdue loans")
    void testGetOverdueLoans() {
        // Given
        Loan overdueLoan = createOverdueLoan(5);
        List<Loan> activeLoans = Arrays.asList(overdueLoan, testLoan);
        when(mockLoanRepository.findActiveLoans()).thenReturn(activeLoans);

        // When
        List<Loan> overdueLoans = overdueService.getOverdueLoans();

        // Then
        assertEquals(1, overdueLoans.size());
        assertEquals(overdueLoan.getId(), overdueLoans.get(0).getId());
        verify(mockLoanRepository).findActiveLoans();
    }

    @Test
    @DisplayName("Should get overdue loans for specific user")
    void testGetOverdueLoansForUser() {
        // Given
        Loan overdueLoan = createOverdueLoan(5);
        testUser.addLoan(overdueLoan);
        testUser.addLoan(testLoan); // Non-overdue loan

        // When
        List<Loan> userOverdueLoans = overdueService.getOverdueLoansForUser(testUser);

        // Then
        assertEquals(1, userOverdueLoans.size());
        assertEquals(overdueLoan.getId(), userOverdueLoans.get(0).getId());
    }

    @Test
    @DisplayName("Should check if user has overdue items")
    void testHasOverdueItems() {
        // Given
        Loan overdueLoan = createOverdueLoan(5);
        testUser.addLoan(overdueLoan);

        // When
        boolean hasOverdue = overdueService.hasOverdueItems(testUser);

        // Then
        assertTrue(hasOverdue);
    }

    @Test
    @DisplayName("Should return false when user has no overdue items")
    void testHasOverdueItems_ReturnsFalse_WhenNoOverdue() {
        // Given - user only has non-overdue loans
        testUser.addLoan(testLoan);

        // When
        boolean hasOverdue = overdueService.hasOverdueItems(testUser);

        // Then
        assertFalse(hasOverdue);
    }

    @Test
    @DisplayName("Should calculate overdue fines for user")
    void testCalculateOverdueFines() {
        // Given
        Loan overdueLoan1 = createOverdueLoan(5);  // 50 NIS
        Loan overdueLoan2 = createOverdueLoan(3);  // 30 NIS
        testUser.addLoan(overdueLoan1);
        testUser.addLoan(overdueLoan2);

        // When
        double totalFines = overdueService.calculateOverdueFines(testUser);

        // Then
        assertEquals(80.0, totalFines, 0.01); // 50 + 30
    }

    @Test
    @DisplayName("Should return zero fines when user has no overdue items")
    void testCalculateOverdueFines_ReturnsZero_WhenNoOverdue() {
        // Given
        testUser.addLoan(testLoan); // Non-overdue loan

        // When
        double totalFines = overdueService.calculateOverdueFines(testUser);

        // Then
        assertEquals(0.0, totalFines, 0.01);
    }

    // ===== Emergency Scan Tests =====

    @Test
    @DisplayName("Should perform emergency scan with detailed report")
    void testPerformEmergencyScan() {
        // Given
        Loan overdueLoan = createOverdueLoan(5);
        List<Loan> activeLoans = Arrays.asList(overdueLoan, testLoan);
        when(mockLoanRepository.findActiveLoans()).thenReturn(activeLoans);

        // When
        assertDoesNotThrow(() -> {
            overdueService.performEmergencyScan();
        });

        // Then
        verify(mockLoanRepository, atLeastOnce()).findActiveLoans();
    }

    @Test
    @DisplayName("Emergency scan should handle empty loan list")
    void testPerformEmergencyScan_WithEmptyLoans() {
        // Given
        when(mockLoanRepository.findActiveLoans()).thenReturn(Collections.emptyList());

        // When
        assertDoesNotThrow(() -> {
            overdueService.performEmergencyScan();
        });

        // Then
        verify(mockLoanRepository).findActiveLoans();
    }

    // ===== Error Handling Tests =====

    @Test
    @DisplayName("Should handle repository exceptions gracefully")
    void testHandleRepositoryExceptions() {
        // Given
        when(mockLoanRepository.findActiveLoans()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertDoesNotThrow(() -> {
            overdueService.checkAndProcessOverdueItems();
        });
    }

    @Test
    @DisplayName("Should handle notification exceptions gracefully")
    void testHandleNotificationExceptions() {
        // Given
        Loan overdueLoan = createOverdueLoan(5);
        List<Loan> activeLoans = Collections.singletonList(overdueLoan);
        when(mockLoanRepository.findActiveLoans()).thenReturn(activeLoans);
        doThrow(new RuntimeException("Email server down")).when(mockNotifier).notify(any(), anyString());

        // When & Then
        assertDoesNotThrow(() -> {
            overdueService.checkAndProcessOverdueItems();
        });

        // Fine should still be applied even if notification fails
        assertEquals(50.0, testUser.getFineBalance(), 0.01);
    }

    // ===== Helper Methods =====

    /**
     * Create an overdue loan for testing
     */
    private Loan createOverdueLoan(int overdueDays) {
        return createOverdueLoan(overdueDays, testBook);
    }

    /**
     * Create an overdue loan for testing with specific book
     */
    private Loan createOverdueLoan(int overdueDays, Book book) {
        return createOverdueLoan(overdueDays, testUser, book);
    }

    /**
     * Create an overdue loan for testing with specific user and book
     */
    private Loan createOverdueLoan(int overdueDays, User user, Book book) {
        try {
            // Create loan
            Loan loan = new Loan("L" + System.currentTimeMillis() + overdueDays, user, book, 28);

            // Use reflection to set private dates to simulate overdue
            Field borrowDateField = Loan.class.getDeclaredField("borrowDate");
            Field dueDateField = Loan.class.getDeclaredField("dueDate");

            borrowDateField.setAccessible(true);
            dueDateField.setAccessible(true);

            // Set dates to make loan overdue
            LocalDate oldBorrowDate = LocalDate.now().minusDays(28 + overdueDays);
            LocalDate oldDueDate = oldBorrowDate.plusDays(28);

            borrowDateField.set(loan, oldBorrowDate);
            dueDateField.set(loan, oldDueDate);

            // Add loan to user
            user.addLoan(loan);
            return loan;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create overdue loan for testing", e);
        }
    }

    @Nested
    @DisplayName("Overdue Service Integration Scenarios")
    class IntegrationScenariosTests {

        @Test
        @DisplayName("Should handle complete overdue lifecycle")
        void testCompleteOverdueLifecycle() {
            // Setup - Create overdue loan
            Loan overdueLoan = createOverdueLoan(7);
            List<Loan> activeLoans = Collections.singletonList(overdueLoan);
            when(mockLoanRepository.findActiveLoans()).thenReturn(activeLoans);

            // Phase 1: Detect and process overdue
            overdueService.checkAndProcessOverdueItems();

            assertEquals(70.0, testUser.getFineBalance(), 0.01);
            verify(mockNotifier).notify(eq(testUser), anyString());

            // Phase 2: Check overdue reporting
            List<Loan> overdueLoans = overdueService.getOverdueLoans();
            assertEquals(1, overdueLoans.size());
            assertTrue(overdueService.hasOverdueItems(testUser));
            assertEquals(70.0, overdueService.calculateOverdueFines(testUser), 0.01);

            // Phase 3: Emergency scan
            assertDoesNotThrow(() -> overdueService.performEmergencyScan());
        }

        @Test
        @DisplayName("Should handle mixed overdue and non-overdue loans")
        void testMixedOverdueAndNonOverdueLoans() {
            // Given
            Book book2 = new Book("B002", "On Time Book", "Good Author");
            Loan overdueLoan = createOverdueLoan(5);
            Loan onTimeLoan = new Loan("L002", testUser, book2, 28);

            List<Loan> activeLoans = Arrays.asList(overdueLoan, onTimeLoan);
            when(mockLoanRepository.findActiveLoans()).thenReturn(activeLoans);

            // When
            overdueService.checkAndProcessOverdueItems();

            // Then
            assertEquals(50.0, testUser.getFineBalance(), 0.01); // Only overdue loan fined
            verify(mockNotifier, times(1)).notify(any(), anyString()); // Only one notification
        }
    }
}