package com.library.domain.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Book entity - Sprint 1 (US1.3, US1.4)
 */
class BookTest {
    private static final String TEST_ISBN = "978-0134685991";
    private static final String TEST_TITLE = "Effective Java";
    private static final String TEST_AUTHOR = "Joshua Bloch";

    private Book book;

    @BeforeAll
    static void setUpBeforeAll() {
        System.out.println("=== Starting Book tests ===");
    }

    @AfterAll
    static void tearDownAfterAll() {
        System.out.println("=== Book tests completed ===");
    }

    @BeforeEach
    void setUpBeforeEach() {
        book = new Book(TEST_ISBN, TEST_TITLE, TEST_AUTHOR);
        System.out.println("Created new book: " + TEST_TITLE);
    }

    @AfterEach
    void tearDownAfterEach() {
        book = null;
        System.out.println("Cleaned up book instance");
    }

    @Test
    @DisplayName("US1.3 - Should create book with correct properties")
    void testBookCreation() {
        // Then - verify all properties
        assertEquals(TEST_ISBN, book.getIsbn(), "ISBN should match");
        assertEquals(TEST_TITLE, book.getTitle(), "Title should match");
        assertEquals(TEST_AUTHOR, book.getAuthor(), "Author should match");
        assertTrue(book.isAvailable(), "New book should be available");
    }

    @Test
    @DisplayName("US1.3 - Should mark book as borrowed")
    void testMarkBorrowed() {
        // When
        book.markBorrowed();

        // Then
        assertFalse(book.isAvailable(), "Book should not be available after borrowing");
    }

    @Test
    @DisplayName("US1.3 - Should mark book as returned")
    void testMarkReturned() {
        // Given - borrowed book
        book.markBorrowed();
        assertFalse(book.isAvailable(), "Precondition: book should be borrowed");

        // When
        book.markReturned();

        // Then
        assertTrue(book.isAvailable(), "Book should be available after return");
    }

    @Test
    @DisplayName("Should handle multiple borrow-return cycles")
    void testMultipleBorrowReturnCycles() {
        // First cycle
        book.markBorrowed();
        assertFalse(book.isAvailable());

        book.markReturned();
        assertTrue(book.isAvailable());

        // Second cycle
        book.markBorrowed();
        assertFalse(book.isAvailable());

        book.markReturned();
        assertTrue(book.isAvailable());
    }

    @Test
    @DisplayName("US1.4 - Book should maintain state correctly")
    void testBookStateConsistency() {
        // Verify initial state
        assertTrue(book.isAvailable());

        // Change state and verify
        book.markBorrowed();
        assertFalse(book.isAvailable());

        // Change back and verify
        book.markReturned();
        assertTrue(book.isAvailable());
    }
}