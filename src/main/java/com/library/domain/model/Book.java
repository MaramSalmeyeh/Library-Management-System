package com.library.domain.model;

/**
 * Represents a book in the library system
 */
public class Book {
    private String isbn;
    private String title;
    private String author;
    private boolean available;

    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.available = true;
    }

    // Getters
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return available; }

    public void markBorrowed() {
        this.available = false;
    }

    public void markReturned() {
        this.available = true;
    }

    /**
     * String representation with availability status
     */
    @Override
    public String toString() {
        return title + " by " + author + " (ISBN: " + isbn + ") - " +
                (available ? "Available" : "Borrowed");
    }
}