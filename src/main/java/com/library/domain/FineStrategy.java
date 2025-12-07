package com.library.domain;

/**
 * Represents a strategy for calculating fines for overdue library items.
 * <p>
 * Implementations of this interface define how fines are computed based on
 * the type of media (e.g., Book, CD) and the number of overdue days.
 * </p>
 *
 * <p>Used by {@link FineCalculator}.</p>
 *
 * @author Maram
 * @version 1.0
 */
public interface FineStrategy {

    /**
     * Calculates the fine for an overdue item.
     *
     * @param overdueDays the number of overdue days
     * @return the calculated fine amount
     */
    double calculateFine(long overdueDays);
}
