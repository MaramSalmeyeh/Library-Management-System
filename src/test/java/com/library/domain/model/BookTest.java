package com.library.domain.model;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

// Allows @BeforeAll/@AfterAll to be non-static (optional)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookTest {

    private Book book;

    @BeforeAll
    void beforeAll() {
        System.out.println("BeforeAll(BookTest)");
    }

    @AfterAll
    void afterAll() {
        System.out.println("AfterAll(BookTest)");
    }

    @BeforeEach
    void setUp() {
        book = new Book("Engineering", "Maram", "9780132350884");
    }

    @AfterEach
    void tearDown() {
        book = null;
    }

    @Test
    @DisplayName("Getters, matching, and availability toggling")
    void getters_matching_and_availability() {
        assertEquals("Engineering", book.getTitle());
        assertEquals("Maram", book.getAuthor());
        assertEquals("9780132350884", book.getIsbn());

        assertTrue(book.matches("engineer"));
        assertTrue(book.matches("maram"));
        assertTrue(book.matches("0884"));
        assertFalse(book.matches("zzz"));

        assertTrue(book.isAvailable());
        book.markBorrowed();
        assertFalse(book.isAvailable());
        book.markReturned();
        assertTrue(book.isAvailable());
    }
}
