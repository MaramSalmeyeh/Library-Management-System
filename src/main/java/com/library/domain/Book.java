package com.library.domain;

/**
 * Represents a book in the library system.
 * <p>
 * Each book has a unique ID, title, author, and ISBN. It also keeps track
 * of whether it is currently borrowed by a user.
 * </p>
 *
 * @author Maram
 * @version 1.0
 */
public class Book {

    /**
     * The unique identifier of the book.
     */
    private final String id;

    /**
     * The title of the book.
     */
    private final String title;

    /**
     * The author of the book.
     */
    private final String author;

    /**
     * The ISBN number of the book.
     */
    private final String isbn;

    /**
     * Indicates whether the book is currently borrowed.
     */
    private boolean borrowed;

    /**
     * Creates a new Book object with the given information.
     *
     * @param id        the unique ID of the book
     * @param title     the book title
     * @param author    the author of the book
     * @param isbn      the ISBN of the book
     * @param borrowed  whether the book is currently borrowed
     */
    public Book(String id, String title, String author, String isbn, boolean borrowed) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.borrowed = borrowed;
    }

    /**
     * Gets the unique book ID.
     *
     * @return the book ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the title of the book.
     *
     * @return the book title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the author of the book.
     *
     * @return the author's name
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Gets the ISBN number of the book.
     *
     * @return the ISBN
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Checks if the book is borrowed.
     *
     * @return {@code true} if the book is borrowed, otherwise {@code false}
     */
    public boolean isBorrowed() {
        return borrowed;
    }

    /**
     * Updates the borrowed status of the book.
     *
     * @param borrowed the new borrowed status
     */
    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }
}
