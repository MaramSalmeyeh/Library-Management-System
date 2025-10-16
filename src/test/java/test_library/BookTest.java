package test_library;

import org.junit.jupiter.api.Test;
import software.library.Book;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {
    @Test
    void getters_defaults_and_matches() {
        Book b = new Book("Clean Code","Robert C. Martin","9780132350884");
        assertEquals("Clean Code", b.getTitle());
        assertEquals("Robert C. Martin", b.getAuthor());
        assertEquals("9780132350884", b.getIsbn());
        assertTrue(b.isAvailable());
        assertTrue(b.matches("clean"));
        assertTrue(b.matches("martin"));
        assertTrue(b.matches("0884"));
        assertFalse(b.matches("not-there"));
    }

    @Test
    void borrow_and_return_flipAvailability() {
        Book b = new Book("T","A","I");
        b.markBorrowed();
        assertFalse(b.isAvailable());
        b.markReturned();
        assertTrue(b.isAvailable());
    }
}
