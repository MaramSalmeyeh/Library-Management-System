package com.library.domain.strategies;

/**
 * Fine calculation strategy for CDs
 * @author Your Name
 * @version 1.0
 */
public class CDFineStrategy implements FineStrategy {
    private static final double DAILY_FINE = 20.0;

    @Override
    public double calculateFine(int overdueDays) {
        return overdueDays * DAILY_FINE;
    }
}