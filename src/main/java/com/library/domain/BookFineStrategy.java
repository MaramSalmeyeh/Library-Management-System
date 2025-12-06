package com.library.domain;


public class BookFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(long overdueDays) {
        if (overdueDays <= 0) {
            return 0.0;
        }
        return 10.0;
    }
}
