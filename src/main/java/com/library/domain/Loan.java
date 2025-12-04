package com.library.domain;

import java.time.LocalDate;


public class Loan {

    private final String id;
    private final String userId;
    private final String bookId;       // ممكن نسميه itemId لكن نتركه هيك عشان ما نكسّر الكود
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private final MediaType mediaType; // NEW

    // ✅ constructor القديم المتوافق مع الكود والملفات القديمة
    public Loan(String id, String userId, String bookId,
                LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate) {
        this(id, userId, bookId, borrowDate, dueDate, returnDate, MediaType.BOOK);
    }

    // ✅ constructor الجديد مع نوع الوسيط (BOOK / CD)
    public Loan(String id, String userId, String bookId,
                LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate,
                MediaType mediaType) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.mediaType = (mediaType == null) ? MediaType.BOOK : mediaType;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getBookId() {
        return bookId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void markReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public boolean isOverdue(LocalDate today) {
        return !isReturned() && today.isAfter(dueDate);
    }
}