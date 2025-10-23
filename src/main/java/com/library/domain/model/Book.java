package com.library.domain.model;

public class Book {
    private final String title;
    private final String author;
    private final String isbn;
    private boolean available = true;

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public String getTitle()  { return title; }
    public String getAuthor() { return author; }
    public String getIsbn()   { return isbn; }

    public boolean isAvailable() { return available; }


    public boolean matches(String q) {
        String k = q.toLowerCase();
        return title.toLowerCase().contains(k)
                || author.toLowerCase().contains(k)
                || isbn.toLowerCase().contains(k);
    }

    public void markBorrowed() { available = false; }
    public void markReturned() { available = true; }
}
