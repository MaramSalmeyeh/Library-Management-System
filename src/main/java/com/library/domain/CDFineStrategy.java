package com.library.domain;

/**
 * غرامة الـ CD: 20 NIS للمادة المتأخرة.
 */
public class CDFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(long overdueDays) {
        if (overdueDays <= 0) {
            return 0.0;
        }
        return 20.0;
    }
}
