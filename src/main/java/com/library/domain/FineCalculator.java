package com.library.domain;

import java.util.EnumMap;
import java.util.Map;

/**
 * يستخدم استراتيجيات مختلفة لحساب الغرامة حسب نوع الوسيط.
 */
public class FineCalculator {

    private final Map<MediaType, FineStrategy> strategies = new EnumMap<>(MediaType.class);

    public FineCalculator() {
        // نربط كل نوع بالمخطط المناسب
        strategies.put(MediaType.BOOK, new BookFineStrategy());
        strategies.put(MediaType.CD, new CDFineStrategy());
    }

    /**
     * يحسب الغرامة لنوع وسائط معين.
     */
    public double calculate(MediaType type, long overdueDays) {
        FineStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("No fine strategy for media type " + type);
        }
        return strategy.calculateFine(overdueDays);
    }
}
