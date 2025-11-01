package com.library.infrastructure;

import com.library.domain.model.Loan;
import com.library.domain.repository.LoanRepository;
import java.util.ArrayList; import java.util.List;

public class InMemoryLoanRepository implements LoanRepository {
    private final List<Loan> loans = new ArrayList<>();
    public void save(Loan loan) { loans.add(loan); }
    public List<Loan> findAll() { return new ArrayList<>(loans); }
}
