package com.library.domain.repository;

import com.library.domain.model.Fine;
import com.library.domain.model.User;
import java.util.Optional;

public interface FineRepository {
    void save(Fine fine);
    Optional<Fine> findByUser(User user);
}
