package com.library.service;

import com.library.domain.FileStorage;
import com.library.domain.Fine;
import com.library.domain.FineCalculator;
import com.library.domain.MediaType;

import java.util.ArrayList;
import java.util.List;

public class FineService {

    private final FileStorage storage;
    private final FineCalculator fineCalculator;


    public FineService(FileStorage storage) {
        this(storage, new FineCalculator());
    }


    public FineService(FileStorage storage, FineCalculator fineCalculator) {
        this.storage = storage;
        this.fineCalculator = fineCalculator;
    }

    public List<Fine> getUserFines(String userId) {
        List<Fine> all = storage.loadFines();
        List<Fine> result = new ArrayList<>();
        for (Fine f : all) {
            if (f.getUserId().equals(userId)) {
                result.add(f);
            }
        }
        return result;
    }

    public double getUserOutstandingBalance(String userId) {
        double total = 0.0;


        for (Fine f : storage.loadFines()) {



            if (f.getUserId().trim().equals(userId.trim()) && !f.isPaid()) {
                total += f.getAmount();
            }
        }


        return total;
    }

    public Fine createFine(String userId, double amount) {
        List<Fine> fines = storage.loadFines();
        String id = "F" + (fines.size() + 1);

        Fine fine = new Fine(id, userId, amount, false);
        fines.add(fine);
        storage.saveFines(fines);
        return fine;
    }


    public Fine createFineForOverdue(String userId, MediaType mediaType, long overdueDays) {
        double amount = fineCalculator.calculate(mediaType, overdueDays);
        if (amount <= 0.0) {
            return null;
        }
        return createFine(userId, amount);
    }

    public double payFine(String userId, double amountToPay) {
        if (amountToPay <= 0) {
            return getUserOutstandingBalance(userId);
        }

        List<Fine> fines = storage.loadFines();
        double remainingToPay = amountToPay;

        for (Fine fine : fines) {
            if (!fine.getUserId().equals(userId) || fine.isPaid()) {
                continue;
            }

            if (remainingToPay <= 0) break;

            double fineAmount = fine.getAmount();

            if (remainingToPay >= fineAmount) {

                remainingToPay -= fineAmount;
                fine.setAmount(0);
                fine.setPaid(true);
            } else {

                fine.setAmount(fineAmount - remainingToPay);
                remainingToPay = 0;
            }
        }

        storage.saveFines(fines);

        return getUserOutstandingBalance(userId);
    }

    public boolean hasUnpaidFines(String userId) {
        return getUserOutstandingBalance(userId) > 0.0;
    }
}
