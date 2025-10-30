package com.library.domain.strategies;

/**
 * Fine calculation strategy for books
 * @author Your Name
 * @version 1.0
 */
public class BookFineStrategy implements FineStrategy {
    private static final double DAILY_FINE = 10.0;

    @Override
    public double calculateFine(int overdueDays) {
        return overdueDays * DAILY_FINE;
    }
}