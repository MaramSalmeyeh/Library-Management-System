package com.library.service;

import com.library.domain.Book;
import com.library.domain.FileStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookService {

    private final FileStorage storage;

    public BookService(FileStorage storage) {
        this.storage = storage;
    }


    public Book addBook(String title, String author, String isbn) {
        List<Book> books = storage.loadBooks();


        for (Book b : books) {
            if (b.getIsbn().equalsIgnoreCase(isbn)) {

                return null;
            }
        }


        String id = "B" + (books.size() + 1);

        Book newBook = new Book(id, title, author, isbn, false);
        books.add(newBook);

        storage.saveBooks(books);

        return newBook;
    }



    public List<Book> searchByTitle(String titlePart) {
        List<Book> result = new ArrayList<>();
        String keyword = titlePart.toLowerCase();

        for (Book b : storage.loadBooks()) {
            if (b.getTitle().toLowerCase().contains(keyword)) {
                result.add(b);
            }
        }
        return result;
    }


    public List<Book> searchByAuthor(String authorPart) {
        List<Book> result = new ArrayList<>();
        String keyword = authorPart.toLowerCase();

        for (Book b : storage.loadBooks()) {
            if (b.getAuthor().toLowerCase().contains(keyword)) {
                result.add(b);
            }
        }
        return result;
    }


    public Book searchByIsbn(String isbn) {
        for (Book b : storage.loadBooks()) {
            if (b.getIsbn().equalsIgnoreCase(isbn)) {
                return b;
            }
        }
        return null;
    }


    public List<Book> getAllBooks() {
        return storage.loadBooks();
    }
}
