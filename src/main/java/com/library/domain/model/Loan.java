package com.library.domain.model;

import java.time.LocalDate;

public class Loan {
    private final User user;
    private final Book book;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private boolean returned;

    public Loan(User user, Book book, LocalDate borrowDate, LocalDate dueDate) {
        this.user = user; this.book = book; this.borrowDate = borrowDate; this.dueDate = dueDate;
    }
    public User getUser() { return user; }
    public Book getBook() { return book; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isReturned() { return returned; }
    public void markReturned() { this.returned = true; }
}
