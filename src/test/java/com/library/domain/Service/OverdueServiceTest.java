package com.library.domain.service;

import com.library.domain.model.Book;
import com.library.domain.model.Loan;
import com.library.domain.model.User;
import com.library.infrastructure.notification.Observer;
import com.library.repository.LoanRepository;
import org.junit.jupiter.api.*;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OverdueServiceTest {
    private LoanRepository loanRepository;
    private Observer notifier;
    private OverdueService overdueService;
    private User testUser;
    private Book testBook;
    private Loan overdueLoan;
    private Loan currentLoan;

    @BeforeAll
    static void setUpBeforeAll() {
        System.out.println("=== Starting OverdueService tests ===");
    }

    @AfterAll
    static void tearDownAfterAll() {
        System.out.println("=== OverdueService tests completed ===");
    }

    @BeforeEach
    void setUpBeforeEach() {
        loanRepository = mock(LoanRepository.class);
        notifier = mock(Observer.class);
        overdueService = new OverdueService(loanRepository, notifier);

        testUser = new User("U001", "Test User", "test@email.com");
        testBook = new Book("B001", "Test Book", "Test Author");
        overdueLoan = mock(Loan.class);
        currentLoan = mock(Loan.class);

        // Mock overdue loan
        when(overdueLoan.isOverdue()).thenReturn(true);
        when(overdueLoan.getUser()).thenReturn(testUser);
        when(overdueLoan.getBook()).thenReturn(testBook);
        when(overdueLoan.getOverdueDays()).thenReturn(5);
        when(overdueLoan.calculateFine()).thenReturn(50.0);

        // Mock current loan
        when(currentLoan.isOverdue()).thenReturn(false);
    }

    @AfterEach
    void tearDownAfterEach() {
        overdueService.shutdown();
    }

    @Test
    @DisplayName("US2.2 - Should detect and process overdue loans")
    void testOverdueDetection() {
        // Given
        List<Loan> loans = Arrays.asList(overdueLoan, currentLoan);
        when(loanRepository.findActiveLoans()).thenReturn(loans);

        // When
        overdueService.checkAndProcessOverdueItems();

        // Then
        verify(notifier, times(1)).notify(eq(testUser), anyString());
        assertEquals(50.0, testUser.getFineBalance());
    }

    @Test
    @DisplayName("Should start and stop automatic scanner")
    void testScannerControl() {
        assertDoesNotThrow(() -> {
            overdueService.startAutomaticOverdueScanning();
            assertTrue(overdueService.isScannerRunning());

            overdueService.stopAutomaticOverdueScanning();
            assertFalse(overdueService.isScannerRunning());
        });
    }
}