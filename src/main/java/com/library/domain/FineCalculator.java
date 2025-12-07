package com.library.domain;

import java.util.EnumMap;
import java.util.Map;

/**
 * Calculates fines for different media types using a strategy pattern.
 * <p>
 * The {@code FineCalculator} maps each {@link MediaType} to a corresponding
 * {@link FineStrategy} implementation. This allows flexible fine calculation
 * depending on the borrowed media (e.g., Book vs CD).
 * </p>
 *
 * <p>Currently supports:</p>
 * <ul>
 *   <li>{@link MediaType#BOOK} → {@link BookFineStrategy}</li>
 *   <li>{@link MediaType#CD}   → {@link CDFineStrategy}</li>
 * </ul>
 *
 * @author Maram
 * @version 1.0
 */
public class FineCalculator {

    /**
     * A mapping between media types and their fine calculation strategies.
     */
    private final Map<MediaType, FineStrategy> strategies = new EnumMap<>(MediaType.class);

    /**
     * Initializes the calculator and registers fine strategies
     * for all supported media types.
     */
    public FineCalculator() {
        strategies.put(MediaType.BOOK, new BookFineStrategy());
        strategies.put(MediaType.CD, new CDFineStrategy());
    }

    /**
     * Calculates the fine for a given media type and overdue duration.
     *
     * @param type         the type of borrowed media (e.g., BOOK or CD)
     * @param overdueDays  number of days the item is overdue
     * @return the calculated fine amount
     * @throws IllegalArgumentException if no strategy exists for the given media type
     */
    public double calculate(MediaType type, long overdueDays) {
        FineStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("No fine strategy for media type " + type);
        }
        return strategy.calculateFine(overdueDays);
    }
}
