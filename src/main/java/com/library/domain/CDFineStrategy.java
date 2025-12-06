package com.library.domain;


public class CDFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(long overdueDays) {
        if (overdueDays <= 0) {
            return 0.0;
        }
        return 20.0;
    }
}
