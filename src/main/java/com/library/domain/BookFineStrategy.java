package com.library.domain;

/**
 * A fine calculation strategy for books.
 * <p>
 * This strategy applies a fixed fine amount when a book is overdue.
 * </p>
 *
 * Implements the {@link FineStrategy} interface.
 *
 * @author Maram
 * @version 1.0
 */
public class BookFineStrategy implements FineStrategy {

    /**
     * Calculates the fine for an overdue book.
     *
     * @param overdueDays the number of days the book is overdue
     * @return a fixed fine of 10.0 if overdue, otherwise 0.0
     */
    @Override
    public double calculateFine(long overdueDays) {
        if (overdueDays <= 0) {
            return 0.0;
        }
        return 10.0;
    }
}
