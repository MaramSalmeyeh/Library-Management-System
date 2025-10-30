package com.library.repository;

import com.library.domain.model.Loan;
import java.util.*;

public class LoanRepository {
    private Map<String, Loan> loans = new HashMap<>();

    public Loan save(Loan loan) {
        loans.put(loan.getId(), loan);
        return loan;
    }

    public List<Loan> findActiveLoans() {
        List<Loan> activeLoans = new ArrayList<>();
        for (Loan loan : loans.values()) {
            if (loan.getReturnDate() == null) {
                activeLoans.add(loan);
            }
        }
        return activeLoans;
    }
}