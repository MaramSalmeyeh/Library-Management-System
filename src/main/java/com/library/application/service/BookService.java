package com.library.application.service;

import com.library.domain.model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Minimal in-memory Book service for Sprint 1 demo.
 */
public class BookService {
    private final List<Book> books = new ArrayList<>();

    /**
     * Add a book to the in-memory catalog.
     */
    public void addBook(Book book) {
        books.add(book);
    }

    /**
     * Return immutable copy of all books.
     */
    public List<Book> listBooks() {
        return new ArrayList<>(books);
    }

    /**
     * Search books by a keyword across title, author, or isbn.
     */
    public List<Book> search(String keyword, String field) {
        if (keyword == null || keyword.isBlank()) return listBooks();
        String k = keyword.toLowerCase();
        return books.stream().filter(b -> {
            switch (field) {
                case "Title": return b.getTitle().toLowerCase().contains(k);
                case "Author": return b.getAuthor().toLowerCase().contains(k);
                case "ISBN": return b.getIsbn().toLowerCase().contains(k);
                default: return b.getTitle().toLowerCase().contains(k)
                        || b.getAuthor().toLowerCase().contains(k)
                        || b.getIsbn().toLowerCase().contains(k);
            }
        }).collect(Collectors.toList());
    }
}

