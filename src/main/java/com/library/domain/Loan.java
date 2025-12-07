package com.library.domain;

import java.time.LocalDate;

/**
 * Represents a loan record for a borrowed library item.
 * <p>
 * A {@code Loan} stores information about which user borrowed which item,
 * when the item was borrowed, its due date, and whether it has been returned.
 * It also stores the {@link MediaType} to distinguish between Books, CDs, etc.
 * </p>
 *
 * <p>
 * This class supports checking overdue status, marking returns, and retrieving
 * all relevant loan information.
 * </p>
 *
 * @author Maram
 * @version 1.0
 */
public class Loan {

    /**
     * Unique identifier of the loan.
     */
    private final String id;

    /**
     * The ID of the user who borrowed the item.
     */
    private final String userId;

    /**
     * The ID of the borrowed book or media item.
     */
    private final String bookId;

    /**
     * The date on which the item was borrowed.
     */
    private final LocalDate borrowDate;

    /**
     * The due date for returning the item.
     */
    private final LocalDate dueDate;

    /**
     * The actual return date, or {@code null} if not yet returned.
     */
    private LocalDate returnDate;

    /**
     * The type of media associated with this loan (BOOK, CD, etc.).
     */
    private final MediaType mediaType;

    /**
     * Creates a loan for a book, assuming media type is {@link MediaType#BOOK}.
     *
     * @param id          loan ID
     * @param userId      ID of the user who borrowed the item
     * @param bookId      ID of the borrowed book/media
     * @param borrowDate  date the item was borrowed
     * @param dueDate     date the item is due
     * @param returnDate  return date, or {@code null} if not yet returned
     */
    public Loan(String id,
                String userId,
                String bookId,
                LocalDate borrowDate,
                LocalDate dueDate,
                LocalDate returnDate) {
        this(id, userId, bookId, borrowDate, dueDate, returnDate, MediaType.BOOK);
    }

    /**
     * Creates a loan for any type of media.
     *
     * @param id          loan ID
     * @param userId      ID of the user who borrowed the item
     * @param bookId      ID of the borrowed item
     * @param borrowDate  date the item was borrowed
     * @param dueDate     date the item is due
     * @param returnDate  return date, may be null
     * @param mediaType   type of media (BOOK, CD, etc.)
     */
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

    /**
     * @return the loan ID
     */
    public String getId() {
        return id;
    }

    /**
     * @return the ID of the user who borrowed the item
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @return the ID of the borrowed media item
     */
    public String getBookId() {
        return bookId;
    }

    /**
     * @return the date the item was borrowed
     */
    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    /**
     * @return the date the item is due
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * @return the return date, or null if not returned
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * @return the type of media associated with the loan
     */
    public MediaType getMediaType() {
        return mediaType;

    }

    /**
     * Marks the item as returned by setting the return date.
     *
     * @param returnDate the return date of the item
     */
    public void markReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Checks if the item has been returned.
     *
     * @return true if returned, otherwise false
     */
    public boolean isReturned() {
        return returnDate != null;
    }

    /**
     * Determines if the loan is overdue as of the given date.
     *
     * @param today the date to compare against the due date
     * @return true if overdue and not returned, otherwise false
     */
    public boolean isOverdue(LocalDate today) {
        return !isReturned() && today.isAfter(dueDate);
    }

}
