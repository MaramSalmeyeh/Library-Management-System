// OverdueService.java
package com.library.domain.service;

import com.library.domain.model.Loan;
import com.library.domain.model.User;
import com.library.infrastructure.notification.Observer;
import com.library.repository.LoanRepository;

import java.util.List;

/**
 * Service for handling overdue items and notifications
 * @author Your Name
 * @version 1.0
 */
public class OverdueService {
    private LoanRepository loanRepository;
    private Observer notifier;

    public OverdueService(LoanRepository loanRepository, Observer notifier) {
        this.loanRepository = loanRepository;
        this.notifier = notifier;
    }

    /**
     * Check for overdue items and send notifications
     */
    public void checkAndNotifyOverdue() {
        List<Loan> activeLoans = loanRepository.findActiveLoans();

        for (Loan loan : activeLoans) {
            if (loan.isOverdue()) {
                User user = loan.getUser();
                int overdueCount = (int) user.getActiveLoans().stream()
                        .filter(Loan::isOverdue)
                        .count();

                String message = String.format(
                        "You have %d overdue item(s). Please return them as soon as possible.",
                        overdueCount
                );

                notifier.notify(user, message);
            }
        }
    }

    /**
     * Get all overdue loans
     * @return list of overdue loans
     */
    public List<Loan> getOverdueLoans() {
        return loanRepository.findActiveLoans().stream()
                .filter(Loan::isOverdue)
                .collect(java.util.stream.Collectors.toList());
    }
}