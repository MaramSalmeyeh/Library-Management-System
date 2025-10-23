package com.library.domain.model;

import org.junit.jupiter.api.*;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

// Makes @BeforeAll/@AfterAll non-static (optional)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LibrarianTest {

    private Librarian librarian;
    private List<Book> catalog;

    @BeforeAll
    void beforeAll() {
        System.out.println("BeforeAll: This runs once before all tests.");
    }

    @AfterAll
    void afterAll() {
        System.out.println("AfterAll: This runs once after all tests.");
    }

    @BeforeEach
    void setUp() {
        librarian = new Librarian();
        catalog = Arrays.asList(
                new Book("Engineering", "Maram", "9780132350884"),
                new Book("ComputerEng", "Aseel", "9780201485677")
        );
    }

    @AfterEach
    void tearDown() {
        librarian = null;
        catalog = null;
    }

    @Test
    @DisplayName("Search by title, author, or ISBN returns correct matches")
    void search_by_title_author_isbn_returns_matches() {
        assertEquals(1, librarian.search(catalog, "Engineering").size());
        assertEquals(2, librarian.search(catalog, "m").size());
        assertEquals(1, librarian.search(catalog, "0884").size());
        assertEquals(0, librarian.search(catalog, "zzz").size());
    }
}
