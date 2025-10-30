package com.library.domain.strategies;

/**
 * Strategy interface for fine calculation
 * @author Your Name
 * @version 1.0
 */
public interface FineStrategy {
    /**
     * Calculate fine based on overdue days
     * @param overdueDays number of days overdue
     * @return calculated fine amount
     */
    double calculateFine(int overdueDays);
}