package com.library.domain.service;

import com.library.domain.model.Book;
import com.library.domain.model.Loan;
import com.library.domain.model.User;
import com.library.infrastructure.mock.NotificationException;
import com.library.infrastructure.notification.Observer;
import com.library.repository.LoanRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for ReminderService - Sprint 3 (US3.1)
 * Tests notification functionality with Observer Pattern
 *
 * @author Your Name
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReminderServiceTest {

    @Mock
    private LoanRepository mockLoanRepository;

    @Mock
    private Observer mockEmailNotifier;

    @Mock
    private Observer mockSmsNotifier;

    private ReminderService reminderService;
    private User testUser;
    private User testUser2;
    private Book testBook;
    private Book testBook2;

    @BeforeAll
    void setUpBeforeAll() {
        System.out.println("=== Starting ReminderService tests (US3.1) ===");
    }

    @AfterAll
    void tearDownAfterAll() {
        System.out.println("=== ReminderService tests completed ===");
    }

    @BeforeEach
    void setUp() {
        reminderService = new ReminderService(mockLoanRepository);

        // Setup test users
        testUser = new User("U001", "Ahmad Hassan", "ahmad@library.com");
        testUser2 = new User("U002", "Sara Ibrahim", "sara@library.com");

        // Setup test books
        testBook = new Book("B001", "Java Programming", "John Doe");
        testBook2 = new Book("B002", "Design Patterns", "Gang of Four");

        // Setup mock observers
        when(mockEmailNotifier.getType()).thenReturn("EMAIL");
        when(mockEmailNotifier.isActive()).thenReturn(true);

        when(mockSmsNotifier.getType()).thenReturn("SMS");
        when(mockSmsNotifier.isActive()).thenReturn(true);

        System.out.println("Test setup completed");
    }

    // ===== US3.1 - Core Reminder Tests =====

    @Test
    @DisplayName("US3.1 - Should send reminder with correct message format")
    void testSendReminder_CorrectMessageFormat() {
        // Given
        Loan overdueLoan = createOverdueLoan(5, testUser, testBook);
        when(mockLoanRepository.findActiveLoans()).thenReturn(Collections.singletonList(overdueLoan));

        reminderService.addObserver(mockEmailNotifier);

        // When
        reminderService.sendOverdueReminders();

        // Then - Verify message format: "You have n overdue book(s)."
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockEmailNotifier).notify(eq(testUser), messageCaptor.capture());

        String sentMessage = messageCaptor.getValue();
        assertEquals("You have 1 overdue book(s).", sentMessage);
    }

    @Test
    @DisplayName("US3.1 - Should send reminder for multiple overdue books")
    void testSendReminder_MultipleOverdueBooks() {
        // Given - User has 3 overdue books
        Loan loan1 = createOverdueLoan(3, testUser, testBook);
        Loan loan2 = createOverdueLoan(5, testUser, testBook2);
        Book book3 = new Book("B003", "Another Book", "Author");
        Loan loan3 = createOverdueLoan(7, testUser, book3);

        when(mockLoanRepository.findActiveLoans()).thenReturn(Arrays.asList(loan1, loan2, loan3));

        reminderService.addObserver(mockEmailNotifier);

        // When
        reminderService.sendOverdueReminders();

        // Then - Should send ONE message with count of 3
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockEmailNotifier, times(1)).notify(eq(testUser), messageCaptor.capture());

        String sentMessage = messageCaptor.getValue();
        assertEquals("You have 3 overdue book(s).", sentMessage);
    }

    @Test
    @DisplayName("US3.1 - Should send reminders to correct users")
    void testSendReminder_CorrectUsers() {
        // Given - Two users with overdue books
        Loan loan1 = createOverdueLoan(3, testUser, testBook);
        Loan loan2 = createOverdueLoan(5, testUser2, testBook2);

        when(mockLoanRepository.findActiveLoans()).thenReturn(Arrays.asList(loan1, loan2));

        reminderService.addObserver(mockEmailNotifier);

        // When
        reminderService.sendOverdueReminders();

        // Then - Should notify both users
        verify(mockEmailNotifier).notify(eq(testUser), eq("You have 1 overdue book(s)."));
        verify(mockEmailNotifier).notify(eq(testUser2), eq("You have 1 overdue book(s)."));
        verify(mockEmailNotifier, times(2)).notify(any(User.class), anyString());
    }

    @Test
    @DisplayName("US3.1 - Should not send reminders when no overdue books")
    void testSendReminder_NoOverdueBooks() {
        // Given - No overdue loans
        Loan activeLoan = new Loan("L001", testUser, testBook, 28); // Not overdue
        when(mockLoanRepository.findActiveLoans()).thenReturn(Collections.singletonList(activeLoan));

        reminderService.addObserver(mockEmailNotifier);

        // When
        reminderService.sendOverdueReminders();

        // Then - No notifications should be sent
        verify(mockEmailNotifier, never()).notify(any(User.class), anyString());
    }

    @Test
    @DisplayName("US3.1 - Should send reminder to specific user")
    void testSendReminderToUser_SingleUser() {
        // Given
        Loan overdueLoan = createOverdueLoan(5, testUser, testBook);
        testUser.addLoan(overdueLoan);

        reminderService.addObserver(mockEmailNotifier);

        // When
        reminderService.sendReminderToUser(testUser);

        // Then
        verify(mockEmailNotifier).notify(eq(testUser), eq("You have 1 overdue book(s)."));
    }

    @Test
    @DisplayName("US3.1 - Should not send reminder if user has no overdue items")
    void testSendReminderToUser_NoOverdue() {
        // Given - User with active loan but not overdue
        Loan activeLoan = new Loan("L001", testUser, testBook, 28);
        testUser.addLoan(activeLoan);

        reminderService.addObserver(mockEmailNotifier);

        // When
        reminderService.sendReminderToUser(testUser);

        // Then
        verify(mockEmailNotifier, never()).notify(any(User.class), anyString());
    }

    // ===== Observer Pattern Tests =====

    @Test
    @DisplayName("Observer Pattern - Should add observer successfully")
    void testAddObserver() {
        // When
        reminderService.addObserver(mockEmailNotifier);

        // Then
        assertEquals(1, reminderService.getObservers().size());
        assertTrue(reminderService.hasActiveObservers());
    }

    @Test
    @DisplayName("Observer Pattern - Should add multiple observers")
    void testAddMultipleObservers() {
        // When
        reminderService.addObserver(mockEmailNotifier);
        reminderService.addObserver(mockSmsNotifier);

        // Then
        assertEquals(2, reminderService.getObservers().size());
        assertEquals(2, reminderService.getActiveObserverCount());
    }

    @Test
    @DisplayName("Observer Pattern - Should not add duplicate observers")
    void testAddObserver_NoDuplicates() {
        // When
        reminderService.addObserver(mockEmailNotifier);
        reminderService.addObserver(mockEmailNotifier); // Add same observer twice

        // Then
        assertEquals(1, reminderService.getObservers().size());
    }

    @Test
    @DisplayName("Observer Pattern - Should remove observer")
    void testRemoveObserver() {
        // Given
        reminderService.addObserver(mockEmailNotifier);
        assertEquals(1, reminderService.getObservers().size());

        // When
        reminderService.removeObserver(mockEmailNotifier);

        // Then
        assertEquals(0, reminderService.getObservers().size());
    }

    @Test
    @DisplayName("Observer Pattern - Should notify all observers")
    void testNotifyAllObservers() {
        // Given
        Loan overdueLoan = createOverdueLoan(5, testUser, testBook);
        when(mockLoanRepository.findActiveLoans()).thenReturn(Collections.singletonList(overdueLoan));

        reminderService.addObserver(mockEmailNotifier);
        reminderService.addObserver(mockSmsNotifier);

        // When
        reminderService.sendOverdueReminders();

        // Then - Both observers should be notified
        verify(mockEmailNotifier).notify(eq(testUser), anyString());
        verify(mockSmsNotifier).notify(eq(testUser), anyString());
    }

    @Test
    @DisplayName("Observer Pattern - Should skip inactive observers")
    void testSkipInactiveObservers() {
        // Given
        when(mockEmailNotifier.isActive()).thenReturn(false); // Inactive

        Loan overdueLoan = createOverdueLoan(5, testUser, testBook);
        when(mockLoanRepository.findActiveLoans()).thenReturn(Collections.singletonList(overdueLoan));

        reminderService.addObserver(mockEmailNotifier);

        // When
        reminderService.sendOverdueReminders();

        // Then - Should not notify inactive observer
        verify(mockEmailNotifier, never()).notify(any(), anyString());
    }

    @Test
    @DisplayName("Observer Pattern - Should throw exception for null observer")
    void testAddObserver_NullThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            reminderService.addObserver(null);
        });
    }

    // ===== Error Handling Tests =====

    @Test
    @DisplayName("Should handle notification exception gracefully")
    void testHandleNotificationException() {
        // Given
        Loan overdueLoan = createOverdueLoan(5, testUser, testBook);
        when(mockLoanRepository.findActiveLoans()).thenReturn(Collections.singletonList(overdueLoan));

        reminderService.addObserver(mockEmailNotifier);

        // Simulate notification failure
        doThrow(new NotificationException("Email server down"))
                .when(mockEmailNotifier).notify(any(User.class), anyString());

        // When & Then
        assertThrows(NotificationException.class, () -> {
            reminderService.sendOverdueReminders();
        });
    }

    @Test
    @DisplayName("Should handle null user in sendReminderToUser")
    void testSendReminderToUser_NullUser() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            reminderService.sendReminderToUser(null);
        });
    }

    @Test
    @DisplayName("Should handle empty observer list")
    void testSendReminders_NoObservers() {
        // Given - No observers registered
        Loan overdueLoan = createOverdueLoan(5, testUser, testBook);
        when(mockLoanRepository.findActiveLoans()).thenReturn(Collections.singletonList(overdueLoan));

        // When
        reminderService.sendOverdueReminders();

        // Then - Should not throw exception
        assertFalse(reminderService.hasActiveObservers());
        assertEquals(0, reminderService.getTotalNotificationsSent());
    }

    // ===== Statistics and Monitoring Tests =====

    @Test
    @DisplayName("Should track notification count")
    void testTrackNotificationCount() {
        // Given
        Loan loan1 = createOverdueLoan(3, testUser, testBook);
        Loan loan2 = createOverdueLoan(5, testUser2, testBook2);
        when(mockLoanRepository.findActiveLoans()).thenReturn(Arrays.asList(loan1, loan2));

        reminderService.addObserver(mockEmailNotifier);

        // When
        reminderService.sendOverdueReminders();

        // Then
        assertEquals(2, reminderService.getTotalNotificationsSent());
    }

    @Test
    @DisplayName("Should reset notification count")
    void testResetNotificationCount() {
        // Given
        Loan overdueLoan = createOverdueLoan(5, testUser, testBook);
        when(mockLoanRepository.findActiveLoans()).thenReturn(Collections.singletonList(overdueLoan));

        reminderService.addObserver(mockEmailNotifier);
        reminderService.sendOverdueReminders();

        assertTrue(reminderService.getTotalNotificationsSent() > 0);

        // When
        reminderService.resetNotificationCount();

        // Then
        assertEquals(0, reminderService.getTotalNotificationsSent());
    }

    @Test
    @DisplayName("Should get overdue statistics")
    void testGetOverdueStatistics() {
        // Given
        Loan loan1 = createOverdueLoan(3, testUser, testBook);
        Loan loan2 = createOverdueLoan(5, testUser, testBook2);
        Loan loan3 = createOverdueLoan(7, testUser2, testBook);

        when(mockLoanRepository.findActiveLoans()).thenReturn(Arrays.asList(loan1, loan2, loan3));

        reminderService.addObserver(mockEmailNotifier);

        // When
        Map<String, Object> stats = reminderService.getOverdueStatistics();

        // Then
        assertEquals(2, stats.get("totalUsersWithOverdue")); // 2 users
        assertEquals(3, stats.get("totalOverdueItems"));     // 3 overdue loans
        assertEquals(1, stats.get("activeObservers"));       // 1 observer
    }

    // ===== Integration Scenarios =====

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Complete reminder workflow with multiple users and observers")
        void testCompleteReminderWorkflow() {
            // Setup - Multiple users with varying overdue counts
            Loan loan1 = createOverdueLoan(3, testUser, testBook);
            Loan loan2 = createOverdueLoan(5, testUser, testBook2);
            Loan loan3 = createOverdueLoan(7, testUser2, testBook);

            when(mockLoanRepository.findActiveLoans()).thenReturn(Arrays.asList(loan1, loan2, loan3));

            reminderService.addObserver(mockEmailNotifier);
            reminderService.addObserver(mockSmsNotifier);

            // Execute
            reminderService.sendOverdueReminders();

            // Verify - User 1 should receive notification for 2 overdue books
            verify(mockEmailNotifier).notify(eq(testUser), eq("You have 2 overdue book(s)."));
            verify(mockSmsNotifier).notify(eq(testUser), eq("You have 2 overdue book(s)."));

            // Verify - User 2 should receive notification for 1 overdue book
            verify(mockEmailNotifier).notify(eq(testUser2), eq("You have 1 overdue book(s)."));
            verify(mockSmsNotifier).notify(eq(testUser2), eq("You have 1 overdue book(s)."));

            // Verify total notifications (2 users × 2 observers = 4)
            assertEquals(4, reminderService.getTotalNotificationsSent());
        }

        @Test
        @DisplayName("Should handle mix of overdue and non-overdue loans")
        void testMixOfOverdueAndNonOverdue() {
            // Given
            Loan overdueLoan = createOverdueLoan(5, testUser, testBook);
            Loan activeLoan = new Loan("L002", testUser, testBook2, 28); // Not overdue

            when(mockLoanRepository.findActiveLoans()).thenReturn(Arrays.asList(overdueLoan, activeLoan));

            reminderService.addObserver(mockEmailNotifier);

            // When
            reminderService.sendOverdueReminders();

            // Then - Should only count overdue loan
            verify(mockEmailNotifier).notify(eq(testUser), eq("You have 1 overdue book(s)."));
        }
    }

    // ===== Helper Methods =====

    /**
     * Create an overdue loan for testing
     */
    private Loan createOverdueLoan(int overdueDays, User user, Book book) {
        try {
            Loan loan = new Loan("L" + System.currentTimeMillis() + overdueDays, user, book, 28);

            // Use reflection to set dates
            Field borrowDateField = Loan.class.getDeclaredField("borrowDate");
            Field dueDateField = Loan.class.getDeclaredField("dueDate");

            borrowDateField.setAccessible(true);
            dueDateField.setAccessible(true);

            LocalDate oldBorrowDate = LocalDate.now().minusDays(28 + overdueDays);
            LocalDate oldDueDate = oldBorrowDate.plusDays(28);

            borrowDateField.set(loan, oldBorrowDate);
            dueDateField.set(loan, oldDueDate);

            return loan;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create overdue loan for testing", e);
        }
    }
}