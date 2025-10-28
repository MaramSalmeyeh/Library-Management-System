package com.library.domain.service;


import com.library.domain.model.User;
import com.library.domain.model.Book;

public class BorrowingDomainService {


    public boolean borrowBook(User user, Book book) {
        if (!book.isAvailable()) return false;
        book.markBorrowed();
        user.addBorrowed(book);
        return true;
    }

    public boolean returnBook(User user, Book book) {
        if (!user.getBorrowed().contains(book)) return false;
        book.markReturned();
        user.removeBorrowed(book);
        return true;
    }
}
