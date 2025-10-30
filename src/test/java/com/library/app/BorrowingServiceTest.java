package com.library.app;

import com.library.domain.model.Book;
import com.library.domain.model.User;
import com.library.domain.service.BorrowingDomainService;
import com.library.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BorrowingServiceTest {
    private BorrowingService borrowingService;
    private BorrowingDomainService mockDomainService;
    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        mockDomainService = mock(BorrowingDomainService.class);
        borrowingService = new BorrowingService(mockDomainService);
        user = new User("1", "Test User", "test@email.com");
        book = new Book("123", "Test Book", "Test Author");
    }

    @Test void testBorrowBookSuccess() {
        when(mockDomainService.borrowBook(user, book)).thenReturn(true);

        boolean result = borrowingService.borrowBook(user, book);

        assertTrue(result);
        verify(mockDomainService).borrowBook(user, book);
    }

    @Test void testBorrowBookFailure() {
        when(mockDomainService.borrowBook(user, book))
                .thenThrow(new IllegalStateException("Cannot borrow"));

        boolean result = borrowingService.borrowBook(user, book);

        assertFalse(result);
    }

    @Test void testReturnBookSuccess() {
        when(mockDomainService.returnBook(user, book)).thenReturn(true);

        boolean result = borrowingService.returnBook(user, book);

        assertTrue(result);
    }

    @Test void testCalculateFines() {
        user.addFine(25.0);
        double fines = borrowingService.calculateTotalFines(user);
        assertEquals(25.0, fines);
    }

    @Test void testPayFine() {
        user.addFine(50.0);
        double remaining = borrowingService.payFine(user, 30.0);
        assertEquals(20.0, remaining);
    }

    @Test void testCanUserBorrow() {
        assertTrue(borrowingService.canUserBorrow(user));

        user.addFine(1.0);
        assertFalse(borrowingService.canUserBorrow(user));
    }
}