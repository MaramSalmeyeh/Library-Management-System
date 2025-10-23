package com.library.domain.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String id;
    private final String name;
    private final List<Book> borrowed = new ArrayList<>();

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId()   { return id; }
    public String getName() { return name; }

    public boolean borrow(Book book) {
        if (!book.isAvailable()) return false;
        book.markBorrowed();
        borrowed.add(book);
        return true;
    }

    public boolean returnBook(Book book) {
        if (!borrowed.remove(book)) return false;
        book.markReturned();
        return true;
    }

    public boolean hasBorrowed(String isbn) {
        return borrowed.stream().anyMatch(b -> b.getIsbn().equals(isbn));
    }

    public List<Book> getBorrowed() { return new java.util.ArrayList<>(borrowed); }

    public int countBorrowed() { return borrowed.size(); }
}
