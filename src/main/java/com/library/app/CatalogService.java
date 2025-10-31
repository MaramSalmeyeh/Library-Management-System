package com.library.app;

import com.library.domain.model.Book;
import com.library.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for catalog operations
 * @author Your Name
 * @version 1.0
 */
public class CatalogService {
    private final BookRepository bookRepository;
    private final AuthService authService;

    public CatalogService(BookRepository bookRepository, AuthService authService) {
        this.bookRepository = bookRepository;
        this.authService = authService;
    }

    /**
     * Add new book to catalog - Only allowed for logged-in admins (US1.3)
     * @param isbn book ISBN
     * @param title book title
     * @param author book author
     * @return added book
     * @throws IllegalStateException if admin is not logged in
     */
    public Book addBook(String isbn, String title, String author) {
        // Check if admin is logged in (US1.3 requirement)
        if (!authService.isAdminLoggedIn()) {
            throw new IllegalStateException("❌ Only logged-in administrators can add books.");
        }

        // Validate input
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }

        // Check if book with same ISBN already exists
        Book existingBook = bookRepository.findByIsbn(isbn);
        if (existingBook != null) {
            throw new IllegalArgumentException("❌ Book with ISBN " + isbn + " already exists.");
        }

        // Create and save the new book
        Book book = new Book(isbn, title.trim(), author.trim());
        Book savedBook = bookRepository.save(book);

        System.out.println("✅ Book added successfully: " + savedBook.getTitle());
        return savedBook;
    }

    /**
     * Search books by title, author, or ISBN (US1.4)
     * @param query search query
     * @return list of matching books (never null)
     */
    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            System.out.println("⚠️  Search query is empty");
            return new ArrayList<>();
        }

        String searchTerm = query.trim().toLowerCase();
        List<Book> allBooks = getAllBooks();

        List<Book> results = allBooks.stream()
                .filter(book ->
                        book.getTitle().toLowerCase().contains(searchTerm) ||
                                book.getAuthor().toLowerCase().contains(searchTerm) ||
                                book.getIsbn().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        System.out.println("🔍 Search for '" + query + "' found " + results.size() + " book(s)");
        return results;
    }

    /**
     * Get all books in the catalog
     * @return list of all books (never null)
     */
    public List<Book> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books != null ? books : new ArrayList<>();
    }

    /**
     * Find book by ISBN
     * @param isbn book ISBN
     * @return book if found, null otherwise
     */
    public Book findBookByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return null;
        }
        return bookRepository.findByIsbn(isbn.trim());
    }

    /**
     * Remove book from catalog - Only allowed for logged-in admins
     * @param isbn book ISBN
     * @return true if book was removed, false if not found
     * @throws IllegalStateException if admin is not logged in
     */
    public boolean removeBook(String isbn) {
        // Check if admin is logged in
        if (!authService.isAdminLoggedIn()) {
            throw new IllegalStateException("❌ Only logged-in administrators can remove books.");
        }

        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty");
        }

        Book book = bookRepository.findByIsbn(isbn);
        if (book != null) {
            bookRepository.delete(isbn);
            System.out.println("✅ Book removed successfully: " + book.getTitle());
            return true;
        }

        System.out.println("❌ Book with ISBN " + isbn + " not found");
        return false;
    }

    /**
     * Get total number of books in catalog
     * @return book count
     */
    public int getBookCount() {
        return getAllBooks().size();
    }


    public boolean isBookAvailable(String isbn) {
        Book book = findBookByIsbn(isbn);
        return book != null && book.isAvailable();
    }


    public void displayAllBooks() {
        List<Book> books = getAllBooks();
        if (books.isEmpty()) {
            System.out.println(" No books in catalog.");
        } else {
            System.out.println(" All Books (" + books.size() + " total):");
            for (int i = 0; i < books.size(); i++) {
                Book book = books.get(i);
                String status = book.isAvailable() ? " Available" : " Borrowed";
                System.out.println((i + 1) + ". " + book.getTitle() + " by " + book.getAuthor() +
                        " (ISBN: " + book.getIsbn() + ") - " + status);
            }
        }
    }
}