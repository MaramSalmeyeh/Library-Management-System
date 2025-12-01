package com.library.domain;

/**
 * غرامة الكتاب: 10 NIS للمادة المتأخرة
 * (نحسبها كغرامة ثابتة per item وليس لكل يوم.
 * لو الدكتور بده per-day، نغير المعادلة بسهولة).
 */
public class BookFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(long overdueDays) {
        if (overdueDays <= 0) {
            return 0.0;
        }
        return 10.0;
    }
}
