package com.library.domain.service;

import com.library.domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OverdueServiceTest {
    @Test
    void finds_loans_past_due_date() {
        Clock clock = Clock.fixed(Instant.parse("2025-02-01T00:00:00Z"), ZoneId.of("UTC"));
        var overdueSvc = new OverdueService(clock);

        var u = new User("U1","Aseel");
        var b = new Book("A","B","I");

        var l1 = new Loan(u,b, LocalDate.of(2024,12,1), LocalDate.of(2024,12,29)); // overdue
        var l2 = new Loan(u,b, LocalDate.of(2025,1,10), LocalDate.of(2025,2,7));   // not yet

        var res = overdueSvc.findOverdues(List.of(l1,l2));
        assertEquals(1, res.size());
        assertTrue(res.contains(l1));
    }
}
