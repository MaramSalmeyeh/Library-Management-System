package com.library.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanTest {

    @Test
    void isReturned_returnsTrueWhenReturnDateIsNotNull() {
        Loan loan = new Loan(
                "L1",
                "U1",
                "B1",
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(5),
                null
        );

        assertFalse(loan.isReturned());

        loan.markReturned(LocalDate.now());
        assertTrue(loan.isReturned());
    }

    @Test
    void isOverdue_returnsTrueOnlyWhenNotReturnedAndTodayAfterDueDate() {
        LocalDate borrowDate = LocalDate.now().minusDays(10);
        LocalDate dueDate = LocalDate.now().minusDays(3);

        Loan loan = new Loan("L1", "U1", "B1",
                borrowDate,
                dueDate,
                null
        );

        assertTrue(loan.isOverdue(LocalDate.now()));
    }

    @Test
    void isOverdue_returnsFalseWhenReturned() {
        LocalDate borrowDate = LocalDate.now().minusDays(10);
        LocalDate dueDate = LocalDate.now().minusDays(3);

        Loan loan = new Loan("L1", "U1", "B1",
                borrowDate,
                dueDate,
                LocalDate.now() // returned
        );

        assertFalse(loan.isOverdue(LocalDate.now()));
    }

    @Test
    void constructor_setsMediaTypeToBookForOldConstructor() {
        Loan loan = new Loan(
                "L1",
                "U1",
                "B1",
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                null
        );

        assertEquals(MediaType.BOOK, loan.getMediaType());
    }

    @Test
    void constructor_acceptsMediaTypeForNewConstructor() {
        Loan loan = new Loan(
                "L2",
                "U1",
                "CD1",
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                null,
                MediaType.CD
        );

        assertEquals(MediaType.CD, loan.getMediaType());
    }
}
