package com.library.domain;

public class Book {

    private final String id;
    private final String title;
    private final String author;
    private final String isbn;
    private boolean borrowed;

    public Book(String id, String title, String author, String isbn, boolean borrowed) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.borrowed = borrowed;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }
}
