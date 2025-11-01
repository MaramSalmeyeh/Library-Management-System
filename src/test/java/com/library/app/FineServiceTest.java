package com.library.app;

import com.library.domain.model.User;
import com.library.infrastructure.InMemoryFineRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FineServiceTest {
    @Test
    void add_and_pay_fine() {
        var repo = new InMemoryFineRepository();
        var svc = new FineService(repo);
        var u = new User("U1","Aseel");

        svc.addFine(u, 30);
        assertEquals(30.0, svc.balance(u), 0.0001);

        svc.pay(u, 10);
        assertEquals(20.0, svc.balance(u), 0.0001);

        svc.pay(u, 25);
        assertEquals(0.0, svc.balance(u), 0.0001);
    }
}

