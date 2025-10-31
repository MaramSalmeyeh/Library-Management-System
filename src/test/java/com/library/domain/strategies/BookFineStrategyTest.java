package com.library.domain.strategies;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Fine Strategy Pattern - Sprint 5 (US5.2)
 * @author Your Name
 * @version 1.0
 */
class BookFineStrategyTest {
    private FineStrategy bookFineStrategy;
    private FineStrategy cdFineStrategy;

    @BeforeEach
    void setUp() {
        bookFineStrategy = new BookFineStrategy();
        cdFineStrategy = new CDFineStrategy();
    }

    @Test
    @DisplayName("US5.2 - Book fine should be 10 NIS per day")
    void testBookFineCalculation() {
        assertEquals(0, bookFineStrategy.calculateFine(0));
        assertEquals(10, bookFineStrategy.calculateFine(1));
        assertEquals(50, bookFineStrategy.calculateFine(5));
        assertEquals(100, bookFineStrategy.calculateFine(10));
    }

    @Test
    @DisplayName("US5.2 - CD fine should be 20 NIS per day")
    void testCDFineCalculation() {
        assertEquals(0, cdFineStrategy.calculateFine(0));
        assertEquals(20, cdFineStrategy.calculateFine(1));
        assertEquals(100, cdFineStrategy.calculateFine(5));
        assertEquals(200, cdFineStrategy.calculateFine(10));
    }

    @Test
    @DisplayName("Should handle negative overdue days gracefully")
    void testNegativeOverdueDays() {
        assertEquals(0, bookFineStrategy.calculateFine(-1));
        assertEquals(0, bookFineStrategy.calculateFine(-5));
        assertEquals(0, cdFineStrategy.calculateFine(-1));
        assertEquals(0, cdFineStrategy.calculateFine(-10));
    }

    @Test
    @DisplayName("Should calculate fine for large number of days")
    void testLargeOverdueDays() {
        assertEquals(1000, bookFineStrategy.calculateFine(100));  // 100 days * 10 NIS
        assertEquals(2000, cdFineStrategy.calculateFine(100));    // 100 days * 20 NIS
    }

    @Test
    @DisplayName("CD fine should be exactly double book fine for same days")
    void testCDFineIsDoubleBookFine() {
        for (int days = 0; days <= 30; days++) {
            double bookFine = bookFineStrategy.calculateFine(days);
            double cdFine = cdFineStrategy.calculateFine(days);
            assertEquals(bookFine * 2, cdFine, 0.01,
                    "CD fine should be double book fine for " + days + " days");
        }
    }

    @Nested
    @DisplayName("Strategy Pattern Validation")
    class StrategyPatternTests {

        @Test
        @DisplayName("BookFineStrategy should implement FineStrategy interface")
        void testBookStrategyImplementsInterface() {
            assertTrue(bookFineStrategy instanceof FineStrategy);
        }

        @Test
        @DisplayName("CDFineStrategy should implement FineStrategy interface")
        void testCDStrategyImplementsInterface() {
            assertTrue(cdFineStrategy instanceof FineStrategy);
        }

        @Test
        @DisplayName("Strategies should have different implementations")
        void testDifferentStrategyImplementations() {
            // Same input should produce different outputs
            int overdueDays = 5;
            double bookFine = bookFineStrategy.calculateFine(overdueDays);
            double cdFine = cdFineStrategy.calculateFine(overdueDays);

            assertNotEquals(bookFine, cdFine);
            assertEquals(50.0, bookFine);  // 5 * 10
            assertEquals(100.0, cdFine);   // 5 * 20
        }
    }

    @Nested
    @DisplayName("Fine Calculation Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("Zero days should return zero fine")
        void testZeroDays() {
            assertEquals(0, bookFineStrategy.calculateFine(0));
            assertEquals(0, cdFineStrategy.calculateFine(0));
        }

        @Test
        @DisplayName("One day should return daily rate")
        void testOneDay() {
            assertEquals(10, bookFineStrategy.calculateFine(1));
            assertEquals(20, cdFineStrategy.calculateFine(1));
        }

        @Test
        @DisplayName("Should handle maximum integer days")
        void testMaximumDays() {
            // This tests that calculation doesn't overflow
            int largeDays = 1000;
            double bookFine = bookFineStrategy.calculateFine(largeDays);
            double cdFine = cdFineStrategy.calculateFine(largeDays);

            assertEquals(10000.0, bookFine);  // 1000 * 10
            assertEquals(20000.0, cdFine);    // 1000 * 20
        }
    }
}