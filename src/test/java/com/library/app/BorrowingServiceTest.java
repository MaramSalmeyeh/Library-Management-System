package com.library.app;

import com.library.domain.model.*;
import com.library.infrastructure.InMemoryLoanRepository;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class BorrowingServiceTest {
    @Test
    void borrow_setsDueDatePlus28_and_marksUnavailable() {
        Clock fixed = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"));
        var svc = new BorrowingService(new InMemoryLoanRepository(), fixed);

        var u = new User("U1","Aseel");
        var b = new Book("Eng","Mike","ISBN-1");

        Loan loan = svc.borrow(u,b);
        assertEquals(LocalDate.of(2025,1,1), loan.getBorrowDate());
        assertEquals(LocalDate.of(2025,1,29), loan.getDueDate());
        assertFalse(b.isAvailable());
        assertTrue(u.hasBorrowed("ISBN-1"));
    }

    @Test
    void returnBook_marksReturned_and_available() {
        Clock fixed = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"));
        var repo = new InMemoryLoanRepository();
        var svc = new BorrowingService(repo, fixed);

        var u = new User("U1","Aseel");
        var b = new Book("Eng","Mike","ISBN-1");
        svc.borrow(u,b);

        svc.returnBook(u,b);
        assertTrue(b.isAvailable());
        assertFalse(u.hasBorrowed("ISBN-1"));
        assertTrue(repo.findAll().get(0).isReturned());
    }
}
