package com.library.domain;

/**
 * A fine calculation strategy for CDs.
 * <p>
 * This strategy applies a fixed fine amount when a CD is overdue.
 * </p>
 *
 * Implements the {@link FineStrategy} interface.
 *
 * @author Maram
 * @version 1.0
 */
public class CDFineStrategy implements FineStrategy {

    /**
     * Calculates the fine for an overdue CD.
     *
     * @param overdueDays the number of overdue days
     * @return a fixed fine of 20.0 if overdue, otherwise 0.0
     */
    @Override
    public double calculateFine(long overdueDays) {
        if (overdueDays <= 0) {
            return 0.0;
        }
        return 20.0;
    }
}
