package com.library.app;

import com.library.domain.model.Fine;
import com.library.domain.model.User;
import com.library.domain.repository.FineRepository;

public class FineService {
    private final FineRepository fineRepo;
    public FineService(FineRepository fineRepo) { this.fineRepo = fineRepo; }

    public void addFine(User user, double amount) {
        Fine f = fineRepo.findByUser(user).orElse(new Fine(user, 0));
        f = new Fine(user, f.getAmount() + amount);
        fineRepo.save(f);
    }

    public void pay(User user, double amount) {
        Fine f = fineRepo.findByUser(user).orElse(new Fine(user, 0));
        f.pay(amount);
        fineRepo.save(f);
    }

    public double balance(User user) {
        return fineRepo.findByUser(user).map(Fine::getAmount).orElse(0.0);
    }
}
