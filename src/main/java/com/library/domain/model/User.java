package com.library.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a library user.
 */
public class User {
    private final String id;
    private final String name;
    private final List<Book> borrowed = new ArrayList<>();

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public List<Book> getBorrowed() {
        return new ArrayList<>(borrowed);
    }

    public void addBorrowed(Book book) { borrowed.add(book); }
    public void removeBorrowed(Book book) { borrowed.remove(book); }

    public boolean hasBorrowed(String isbn) {
        return borrowed.stream().anyMatch(b -> b.getIsbn().equals(isbn));
    }

    public int countBorrowed() { return borrowed.size(); }

    /**
     * Attempt to borrow a book. Succeeds only when the book is currently available.
     * If successful, marks the book as borrowed and adds it to the user's borrowed list.
     *
     * @param book the book to borrow
     * @return true when the borrow succeeded
     */
    public boolean borrow(Book book) {
        if (book == null) return false;
        if (!book.isAvailable()) return false;
        book.markBorrowed();
        borrowed.add(book);
        return true;
    }

    /**
     * Return a previously borrowed book. If the user had the book, removes it from
     * the user's borrowed list and marks it as returned.
     *
     * @param book the book to return
     * @return true when the book was returned by this user
     */
    public boolean returnBook(Book book) {
        if (book == null) return false;
        boolean had = borrowed.remove(book);
        if (had) {
            book.markReturned();
            return true;
        }
        return false;
    }
}
