package com.library.app;

import com.library.domain.model.*;
import com.library.domain.repository.LoanRepository;
import java.time.Clock;
import java.time.LocalDate;

public class BorrowingService {
    private final LoanRepository loanRepo;
    private final Clock clock;

    public BorrowingService(LoanRepository loanRepo, Clock clock) {
        this.loanRepo = loanRepo; this.clock = clock;
    }

    public Loan borrow(User user, Book book) {
        if (!book.isAvailable()) throw new IllegalStateException("Book not available.");
        // business rule: due = today + 28 days
        LocalDate today = LocalDate.now(clock);
        LocalDate due = today.plusDays(28);
        book.markBorrowed(); user.addBorrowed(book);
        Loan loan = new Loan(user, book, today, due);
        loanRepo.save(loan);
        return loan;
    }

    public void returnBook(User user, Book book) {
        if (!user.getBorrowed().contains(book)) throw new IllegalStateException("User doesn't have this book.");
        book.markReturned(); user.removeBorrowed(book);
        // Mark the most recent matching loan as returned
        loanRepo.findAll().stream()
                .filter(l -> !l.isReturned() && l.getUser().equals(user) && l.getBook().equals(book))
                .reduce((a,b) -> b) // last
                .ifPresent(Loan::markReturned);
    }
}
