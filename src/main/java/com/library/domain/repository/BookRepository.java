package com.library.repository;

import com.library.domain.model.Book;

import java.util.*;

public class BookRepository {
    private Map<String, Book> books = new HashMap<>();

    public Book save(Book book) {
        books.put(book.getIsbn(), book);
        return book;
    }

    public Book findByIsbn(String isbn) {
        return books.get(isbn);
    }

    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }

    public void delete(String isbn) {
        books.remove(isbn);
    }
}