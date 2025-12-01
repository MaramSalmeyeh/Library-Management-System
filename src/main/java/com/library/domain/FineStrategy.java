package com.library.domain;

/**
 * Strategy Pattern لحساب الغرامة بناءً على نوع المادة وعدد أيام التأخير.
 */
public interface FineStrategy {

    /**
     * @param overdueDays عدد الأيام المتأخرة (>= 1 يعني متأخر).
     * @return قيمة الغرامة بالـ NIS.
     */
    double calculateFine(long overdueDays);
}
