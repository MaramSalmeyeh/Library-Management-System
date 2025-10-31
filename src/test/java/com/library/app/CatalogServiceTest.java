package com.library.app;

import com.library.domain.model.Book;
import com.library.repository.BookRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for CatalogService - Sprint 1 (US1.3, US1.4)
 * @author Your Name
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CatalogServiceTest {

    @Mock
    private BookRepository mockBookRepository;

    @Mock
    private AuthService mockAuthService;

    private CatalogService catalogService;
    private Book testBook;

    @BeforeAll
    void setUpBeforeAll() {
        System.out.println("=== Starting CatalogService tests ===");
    }

    @AfterAll
    void tearDownAfterAll() {
        System.out.println("=== CatalogService tests completed ===");
    }

    @BeforeEach
    void setUpBeforeEach() {
        catalogService = new CatalogService(mockBookRepository, mockAuthService);
        testBook = new Book("978-0134685991", "Effective Java", "Joshua Bloch");
        System.out.println("Initialized CatalogService for testing");
    }

    @AfterEach
    void tearDownAfterEach() {
        catalogService = null;
        testBook = null;
        reset(mockBookRepository, mockAuthService);
        System.out.println("Cleaned up test resources");
    }

    // ===== US1.3 - Add Book Tests =====

    @Test
    @DisplayName("US1.3 - Should add book successfully when admin is logged in")
    void testAddBook_Success_AdminLoggedIn() {
        // Given
        String isbn = "1234567890";
        String title = "Test Book";
        String author = "Test Author";

        when(mockAuthService.isAdminLoggedIn()).thenReturn(true);
        when(mockBookRepository.findByIsbn(isbn)).thenReturn(null);
        when(mockBookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        Book result = catalogService.addBook(isbn, title, author);

        // Then
        assertNotNull(result);
        verify(mockAuthService).isAdminLoggedIn();
        verify(mockBookRepository).findByIsbn(isbn);
        verify(mockBookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("US1.3 - Should throw exception when adding book without admin login")
    void testAddBook_ThrowsException_WhenNotAdmin() {
        // Given
        when(mockAuthService.isAdminLoggedIn()).thenReturn(false);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            catalogService.addBook("123", "Title", "Author");
        });

        assertEquals(" Only logged-in administrators can add books.", exception.getMessage());
        verify(mockAuthService).isAdminLoggedIn();
        verify(mockBookRepository, never()).findByIsbn(anyString());
        verify(mockBookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("US1.3 - Should throw exception when book with same ISBN exists")
    void testAddBook_ThrowsException_WhenDuplicateISBN() {
        // Given
        String existingIsbn = "1234567890";
        when(mockAuthService.isAdminLoggedIn()).thenReturn(true);
        when(mockBookRepository.findByIsbn(existingIsbn)).thenReturn(testBook);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            catalogService.addBook(existingIsbn, "New Title", "New Author");
        });

        assertTrue(exception.getMessage().contains("already exists"));
        verify(mockBookRepository).findByIsbn(existingIsbn);
        verify(mockBookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("US1.3 - Should throw exception when ISBN is empty")
    void testAddBook_ThrowsException_WhenEmptyISBN() {
        // Given
        when(mockAuthService.isAdminLoggedIn()).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            catalogService.addBook("", "Title", "Author");
        });

        assertEquals("ISBN cannot be empty", exception.getMessage());
        verify(mockBookRepository, never()).findByIsbn(anyString());
        verify(mockBookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("US1.3 - Should throw exception when title is empty")
    void testAddBook_ThrowsException_WhenEmptyTitle() {
        // Given
        when(mockAuthService.isAdminLoggedIn()).thenReturn(true);
        when(mockBookRepository.findByIsbn("123")).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            catalogService.addBook("123", "", "Author");
        });

        assertEquals("Title cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("US1.3 - Should throw exception when author is empty")
    void testAddBook_ThrowsException_WhenEmptyAuthor() {
        // Given
        when(mockAuthService.isAdminLoggedIn()).thenReturn(true);
        when(mockBookRepository.findByIsbn("123")).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            catalogService.addBook("123", "Title", "");
        });

        assertEquals("Author cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("US1.3 - Should trim whitespace from inputs")
    void testAddBook_TrimsWhitespace_FromInputs() {
        // Given
        when(mockAuthService.isAdminLoggedIn()).thenReturn(true);
        when(mockBookRepository.findByIsbn("123")).thenReturn(null);
        when(mockBookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book savedBook = invocation.getArgument(0);
            assertEquals("123", savedBook.getIsbn());
            assertEquals("Clean Title", savedBook.getTitle());
            assertEquals("Clean Author", savedBook.getAuthor());
            return savedBook;
        });

        // When
        catalogService.addBook("  123  ", "  Clean Title  ", "  Clean Author  ");

        // Then - Verification happens in the mock above
        verify(mockBookRepository).save(any(Book.class));
    }

    // ===== US1.4 - Search Books Tests =====

    @Test
    @DisplayName("US1.4 - Should search books by title")
    void testSearchBooks_ByTitle() {
        // Given
        String query = "Java";
        List<Book> mockBooks = Arrays.asList(
                new Book("1", "Java Programming", "Author1"),
                new Book("2", "Advanced Java", "Author2")
        );
        when(mockBookRepository.findAll()).thenReturn(mockBooks);

        // When
        List<Book> results = catalogService.searchBooks(query);

        // Then
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(book ->
                book.getTitle().toLowerCase().contains("java")));
    }

    @Test
    @DisplayName("US1.4 - Should search books by author")
    void testSearchBooks_ByAuthor() {
        // Given
        String query = "Bloch";
        List<Book> mockBooks = Arrays.asList(
                new Book("1", "Effective Java", "Joshua Bloch"),
                new Book("2", "Other Book", "Other Author")
        );
        when(mockBookRepository.findAll()).thenReturn(mockBooks);

        // When
        List<Book> results = catalogService.searchBooks(query);

        // Then
        assertEquals(1, results.size());
        assertEquals("Effective Java", results.get(0).getTitle());
        assertEquals("Joshua Bloch", results.get(0).getAuthor());
    }

    @Test
    @DisplayName("US1.4 - Should search books by ISBN")
    void testSearchBooks_ByISBN() {
        // Given
        String query = "013468599";
        List<Book> mockBooks = Arrays.asList(
                new Book("978-0134685991", "Effective Java", "Joshua Bloch"),
                new Book("978-0201633610", "Design Patterns", "GoF")
        );
        when(mockBookRepository.findAll()).thenReturn(mockBooks);

        // When
        List<Book> results = catalogService.searchBooks(query);

        // Then
        assertEquals(1, results.size());
        assertEquals("978-0134685991", results.get(0).getIsbn());
    }

    @Test
    @DisplayName("US1.4 - Should return empty list when no matches found")
    void testSearchBooks_ReturnsEmpty_WhenNoMatches() {
        // Given
        String query = "Nonexistent";
        List<Book> mockBooks = Arrays.asList(
                new Book("1", "Java Programming", "Author1"),
                new Book("2", "Python Basics", "Author2")
        );
        when(mockBookRepository.findAll()).thenReturn(mockBooks);

        // When
        List<Book> results = catalogService.searchBooks(query);

        // Then
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("US1.4 - Should return all books when query is empty")
    void testSearchBooks_ReturnsAll_WhenEmptyQuery() {
        // Given
        String query = "";
        List<Book> mockBooks = Arrays.asList(
                new Book("1", "Book1", "Author1"),
                new Book("2", "Book2", "Author2")
        );
        when(mockBookRepository.findAll()).thenReturn(mockBooks);

        // When
        List<Book> results = catalogService.searchBooks(query);

        // Then
        assertEquals(2, results.size());
        verify(mockBookRepository).findAll();
    }

    @Test
    @DisplayName("US1.4 - Should return all books when query is null")
    void testSearchBooks_ReturnsAll_WhenNullQuery() {
        // Given
        List<Book> mockBooks = Arrays.asList(
                new Book("1", "Book1", "Author1"),
                new Book("2", "Book2", "Author2")
        );
        when(mockBookRepository.findAll()).thenReturn(mockBooks);

        // When
        List<Book> results = catalogService.searchBooks(null);

        // Then
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("US1.4 - Should handle case insensitive search")
    void testSearchBooks_CaseInsensitive() {
        // Given
        String query = "JAVA";
        List<Book> mockBooks = Arrays.asList(
                new Book("1", "Java Programming", "Author1"),
                new Book("2", "Python Basics", "Author2")
        );
        when(mockBookRepository.findAll()).thenReturn(mockBooks);

        // When
        List<Book> results = catalogService.searchBooks(query);

        // Then
        assertEquals(1, results.size());
        assertEquals("Java Programming", results.get(0).getTitle());
    }

    // ===== Additional Catalog Service Tests =====

    @Test
    @DisplayName("Should get all books from repository")
    void testGetAllBooks() {
        // Given
        List<Book> mockBooks = Arrays.asList(
                new Book("1", "Book1", "Author1"),
                new Book("2", "Book2", "Author2"),
                new Book("3", "Book3", "Author3")
        );
        when(mockBookRepository.findAll()).thenReturn(mockBooks);

        // When
        List<Book> results = catalogService.getAllBooks();

        // Then
        assertEquals(3, results.size());
        verify(mockBookRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when repository returns null")
    void testGetAllBooks_ReturnsEmpty_WhenRepositoryReturnsNull() {
        // Given
        when(mockBookRepository.findAll()).thenReturn(null);

        // When
        List<Book> results = catalogService.getAllBooks();

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should find book by ISBN")
    void testFindBookByIsbn() {
        // Given
        String isbn = "1234567890";
        when(mockBookRepository.findByIsbn(isbn)).thenReturn(testBook);

        // When
        Book result = catalogService.findBookByIsbn(isbn);

        // Then
        assertNotNull(result);
        assertEquals(isbn, result.getIsbn());
        verify(mockBookRepository).findByIsbn(isbn);
    }

    @Test
    @DisplayName("Should return null when finding book with empty ISBN")
    void testFindBookByIsbn_ReturnsNull_WhenEmptyISBN() {
        // When
        Book result = catalogService.findBookByIsbn("");

        // Then
        assertNull(result);
        verify(mockBookRepository, never()).findByIsbn(anyString());
    }

    @Test
    @DisplayName("Should return null when finding book with null ISBN")
    void testFindBookByIsbn_ReturnsNull_WhenNullISBN() {
        // When
        Book result = catalogService.findBookByIsbn(null);

        // Then
        assertNull(result);
        verify(mockBookRepository, never()).findByIsbn(anyString());
    }

    @Test
    @DisplayName("Should remove book successfully when admin is logged in")
    void testRemoveBook_Success_AdminLoggedIn() {
        // Given
        String isbn = "1234567890";
        when(mockAuthService.isAdminLoggedIn()).thenReturn(true);
        when(mockBookRepository.findByIsbn(isbn)).thenReturn(testBook);

        // When
        boolean result = catalogService.removeBook(isbn);

        // Then
        assertTrue(result);
        verify(mockBookRepository).delete(isbn);
    }

    @Test
    @DisplayName("Should return false when removing non-existent book")
    void testRemoveBook_ReturnsFalse_WhenBookNotFound() {
        // Given
        String isbn = "9999999999";
        when(mockAuthService.isAdminLoggedIn()).thenReturn(true);
        when(mockBookRepository.findByIsbn(isbn)).thenReturn(null);

        // When
        boolean result = catalogService.removeBook(isbn);

        // Then
        assertFalse(result);
        verify(mockBookRepository, never()).delete(anyString());
    }

    @Test
    @DisplayName("Should throw exception when removing book without admin login")
    void testRemoveBook_ThrowsException_WhenNotAdmin() {
        // Given
        when(mockAuthService.isAdminLoggedIn()).thenReturn(false);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            catalogService.removeBook("123");
        });

        assertEquals(" Only logged-in administrators can remove books.", exception.getMessage());
        verify(mockBookRepository, never()).delete(anyString());
    }

    @Test
    @DisplayName("Should get correct book count")
    void testGetBookCount() {
        // Given
        List<Book> mockBooks = Arrays.asList(
                new Book("1", "Book1", "Author1"),
                new Book("2", "Book2", "Author2")
        );
        when(mockBookRepository.findAll()).thenReturn(mockBooks);

        // When
        int count = catalogService.getBookCount();

        // Then
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should check book availability correctly")
    void testIsBookAvailable() {
        // Given
        String availableIsbn = "123";
        String unavailableIsbn = "456";

        Book availableBook = new Book(availableIsbn, "Available Book", "Author");
        Book unavailableBook = new Book(unavailableIsbn, "Unavailable Book", "Author");
        unavailableBook.markBorrowed();

        when(mockBookRepository.findByIsbn(availableIsbn)).thenReturn(availableBook);
        when(mockBookRepository.findByIsbn(unavailableIsbn)).thenReturn(unavailableBook);

        // When & Then
        assertTrue(catalogService.isBookAvailable(availableIsbn));
        assertFalse(catalogService.isBookAvailable(unavailableIsbn));
        assertFalse(catalogService.isBookAvailable("nonexistent"));
    }

    @Nested
    @DisplayName("Catalog Service Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle special characters in search query")
        void testSearchBooks_WithSpecialCharacters() {
            // Given
            String query = "C# Programming";
            List<Book> mockBooks = Arrays.asList(
                    new Book("1", "C# Programming", "Microsoft"),
                    new Book("2", "Java Programming", "Oracle")
            );
            when(mockBookRepository.findAll()).thenReturn(mockBooks);

            // When
            List<Book> results = catalogService.searchBooks(query);

            // Then
            assertEquals(1, results.size());
            assertEquals("C# Programming", results.get(0).getTitle());
        }

        @Test
        @DisplayName("Should handle very long search query")
        void testSearchBooks_WithLongQuery() {
            // Given
            String longQuery = "This is a very long search query that should still work correctly";
            List<Book> mockBooks = Collections.singletonList(
                    new Book("1", "Short", "Author")
            );
            when(mockBookRepository.findAll()).thenReturn(mockBooks);

            // When
            List<Book> results = catalogService.searchBooks(longQuery);

            // Then
            assertNotNull(results);
            // Should not throw any exceptions
        }

        @Test
        @DisplayName("Should handle books with similar titles")
        void testSearchBooks_WithSimilarTitles() {
            // Given
            String query = "Java";
            List<Book> mockBooks = Arrays.asList(
                    new Book("1", "Java", "Author1"),
                    new Book("2", "JavaScript", "Author2"),
                    new Book("3", "Java Programming", "Author3"),
                    new Book("4", "Advanced Java", "Author4")
            );
            when(mockBookRepository.findAll()).thenReturn(mockBooks);

            // When
            List<Book> results = catalogService.searchBooks(query);

            // Then
            assertEquals(4, results.size());
        }
    }

    @Nested
    @DisplayName("Catalog Service Integration Scenarios")
    class IntegrationScenariosTests {

        @Test
        @DisplayName("Should handle complete add-search-remove workflow")
        void testCompleteWorkflow() {
            // Setup - Add book
            when(mockAuthService.isAdminLoggedIn()).thenReturn(true);
            when(mockBookRepository.findByIsbn("123")).thenReturn(null);
            when(mockBookRepository.save(any(Book.class))).thenReturn(testBook);

            Book addedBook = catalogService.addBook("123", "Workflow Test", "Test Author");
            assertNotNull(addedBook);

            // Search for the book
            List<Book> mockBooks = Collections.singletonList(testBook);
            when(mockBookRepository.findAll()).thenReturn(mockBooks);

            List<Book> searchResults = catalogService.searchBooks("Workflow");
            assertEquals(1, searchResults.size());

            // Remove the book
            when(mockBookRepository.findByIsbn("123")).thenReturn(testBook);
            boolean removed = catalogService.removeBook("123");
            assertTrue(removed);

            // Verify all interactions
            verify(mockBookRepository).save(any(Book.class));
            verify(mockBookRepository).delete("123");
        }
    }
}