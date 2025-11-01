package com.library.domain.service;

import com.library.domain.model.Loan;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class OverdueService {
    private final Clock clock;
    public OverdueService(Clock clock) { this.clock = clock; }

    public List<Loan> findOverdues(List<Loan> loans) {
        LocalDate today = LocalDate.now(clock);
        return loans.stream()
                .filter(l -> !l.isReturned() && today.isAfter(l.getDueDate()))
                .collect(Collectors.toList());
    }
}
