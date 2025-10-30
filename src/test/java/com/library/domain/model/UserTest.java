package com.library.domain.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for User entity - Sprint 2 (US2.3, US4.1)
 */
class UserTest {
    private static final String USER_ID = "U001";
    private static final String USER_NAME = "Test User";
    private static final String USER_EMAIL = "test@library.com";

    private User user;

    @BeforeAll
    static void setUpBeforeAll() {
        System.out.println("=== Starting User tests ===");
    }

    @AfterAll
    static void tearDownAfterAll() {
        System.out.println("=== User tests completed ===");
    }

    @BeforeEach
    void setUpBeforeEach() {
        user = new User(USER_ID, USER_NAME, USER_EMAIL);
        System.out.println("Created user: " + USER_NAME);
    }

    @AfterEach
    void tearDownAfterEach() {
        user = null;
        System.out.println("Cleaned up user instance");
    }

    @Test
    @DisplayName("Should create user with correct properties")
    void testUserCreation() {
        assertEquals(USER_ID, user.getId());
        assertEquals(USER_NAME, user.getName());
        assertEquals(USER_EMAIL, user.getEmail());
        assertEquals(0.0, user.getFineBalance());
        assertTrue(user.getActiveLoans().isEmpty());
    }

    @Test
    @DisplayName("US4.1 - New user should be able to borrow")
    void testCanBorrowForNewUser() {
        assertTrue(user.canBorrow(),
                "New user with no fines and no loans should be able to borrow");
    }

    @Test
    @DisplayName("US4.1 - User with fines should not be able to borrow")
    void testCannotBorrowWithFines() {
        // Given
        user.addFine(50.0);

        // Then
        assertFalse(user.canBorrow(),
                "User with unpaid fines should not be able to borrow");
    }

    @Test
    @DisplayName("US2.3 - Should add fine correctly")
    void testAddFine() {
        // When
        user.addFine(25.0);

        // Then
        assertEquals(25.0, user.getFineBalance());
    }

    @Test
    @DisplayName("US2.3 - Should pay fine correctly")
    void testPayFine() {
        // Given
        user.addFine(100.0);
        assertEquals(100.0, user.getFineBalance());

        // When
        double remaining = user.payFine(30.0);

        // Then
        assertEquals(70.0, remaining);
        assertEquals(70.0, user.getFineBalance());
    }

    @Test
    @DisplayName("US2.3 - Should pay full fine and clear balance")
    void testPayFullFine() {
        // Given
        user.addFine(50.0);

        // When
        double remaining = user.payFine(50.0);

        // Then
        assertEquals(0.0, remaining);
        assertEquals(0.0, user.getFineBalance());
        assertTrue(user.canBorrow());
    }

    @Test
    @DisplayName("US2.3 - Should not allow negative payment")
    void testPayNegativeFine() {
        assertThrows(IllegalArgumentException.class, () -> {
            user.payFine(-10.0);
        });
    }

    @Test
    @DisplayName("Should handle loan management")
    void testLoanManagement() {
        // Given
        Book book = new Book("B001", "Test Book", "Author");
        Loan loan = new Loan("L001", user, book, 28);

        // When
        user.addLoan(loan);

        // Then
        assertEquals(1, user.getActiveLoans().size());
        assertTrue(user.getActiveLoans().contains(loan));

        // When - remove loan
        user.removeLoan(loan);

        // Then
        assertEquals(0, user.getActiveLoans().size());
    }

    @Nested
    @DisplayName("User Borrowing Eligibility Tests")
    class BorrowingEligibilityTests {

        @Test
        @DisplayName("Should be eligible with zero fines and no loans")
        void testEligibleWithNoFinesNoLoans() {
            assertTrue(user.canBorrow());
        }

        @Test
        @DisplayName("Should not be eligible with unpaid fines")
        void testNotEligibleWithFines() {
            user.addFine(1.0);
            assertFalse(user.canBorrow());
        }

        @Test
        @DisplayName("Should become eligible after paying fines")
        void testBecomeEligibleAfterPayingFines() {
            // Given - user with fines
            user.addFine(50.0);
            assertFalse(user.canBorrow());

            // When - pay fines
            user.payFine(50.0);

            // Then - should be eligible
            assertTrue(user.canBorrow());
        }
    }
}