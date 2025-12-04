package com.library.service;

import com.library.domain.FineCalculator;
import com.library.domain.MediaType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FineCalculatorTest {

    @Test
    void calculate_bookFine_is10WhenOverdue() {
        FineCalculator calc = new FineCalculator();

        assertEquals(0.0, calc.calculate(MediaType.BOOK, 0));
        assertEquals(10.0, calc.calculate(MediaType.BOOK, 1));
        assertEquals(10.0, calc.calculate(MediaType.BOOK, 5));
    }

    @Test
    void calculate_cdFine_is20WhenOverdue() {
        FineCalculator calc = new FineCalculator();

        assertEquals(0.0, calc.calculate(MediaType.CD, 0));
        assertEquals(20.0, calc.calculate(MediaType.CD, 1));
        assertEquals(20.0, calc.calculate(MediaType.CD, 10));
    }
}
