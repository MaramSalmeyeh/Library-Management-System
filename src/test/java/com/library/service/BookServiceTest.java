package com.library.service;

import com.library.domain.Book;
import com.library.domain.FileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {

    @TempDir
    Path tempDir;

    private BookService bookService;
    private FileStorage storage;

    @BeforeEach
    void setUp() throws IOException {
        // نجهّز ملفات admins/books (حتى لو الأدمن مش مهم هنا)
        Path adminsFile = tempDir.resolve("admins.txt");
        Files.write(adminsFile, List.of()); // فاضي

        Path booksFile = tempDir.resolve("books.txt");
        Files.write(booksFile, List.of());  // نبدأ بدون كتب

        storage = new FileStorage(tempDir.toString());
        bookService = new BookService(storage);
    }

    @Test
    void addBook_addsNewBookAndPersistsIt() throws IOException {
        Book book = bookService.addBook("Harry Potter", "Rowling", "111");

        assertNotNull(book);
        assertEquals("Harry Potter", book.getTitle());
        assertEquals("Rowling", book.getAuthor());
        assertEquals("111", book.getIsbn());

        // نتأكد أنه انكتب في الملف
        List<String> lines = Files.readAllLines(tempDir.resolve("books.txt"));
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("Harry Potter"));
    }

    @Test
    void addBook_withDuplicateIsbn_returnsNullAndDoesNotAdd() throws IOException {
        // أول كتاب
        Book first = bookService.addBook("Book1", "Author1", "123");
        assertNotNull(first);

        // نفس الـ ISBN
        Book second = bookService.addBook("Book2", "Author2", "123");
        assertNull(second, "Second book with same ISBN should not be added");

        // الملف لازم فيه كتاب واحد بس
        List<String> lines = Files.readAllLines(tempDir.resolve("books.txt"));
        assertEquals(1, lines.size());
    }

    @Test
    void searchByTitle_findsMatchingBooks() {
        bookService.addBook("Java Programming", "A", "1");
        bookService.addBook("Advanced Java", "B", "2");
        bookService.addBook("Python Basics", "C", "3");

        List<Book> result = bookService.searchByTitle("java");

        assertEquals(2, result.size());
    }

    @Test
    void searchByAuthor_findsMatchingBooks() {
        bookService.addBook("Book1", "Aseel", "10");
        bookService.addBook("Book2", "ASEEL Q", "11");
        bookService.addBook("Book3", "Someone Else", "12");

        List<Book> result = bookService.searchByAuthor("aseel");

        assertEquals(2, result.size());
    }

    @Test
    void searchByIsbn_returnsSingleBookOrNull() {
        bookService.addBook("Book1", "A", "111");
        bookService.addBook("Book2", "B", "222");

        Book found = bookService.searchByIsbn("222");
        assertNotNull(found);
        assertEquals("Book2", found.getTitle());

        Book notFound = bookService.searchByIsbn("999");
        assertNull(notFound);
    }
}
