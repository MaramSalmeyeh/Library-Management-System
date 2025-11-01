package com.library.domain.repository;

import com.library.domain.model.Loan;
import java.util.List;

public interface LoanRepository {
    void save(Loan loan);
    List<Loan> findAll();
}
