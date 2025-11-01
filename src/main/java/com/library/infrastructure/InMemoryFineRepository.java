package com.library.infrastructure;

import com.library.domain.model.Fine;
import com.library.domain.model.User;
import com.library.domain.repository.FineRepository;
import java.util.*;

public class InMemoryFineRepository implements FineRepository {
    private final Map<String, Fine> map = new HashMap<>();
    public void save(Fine fine) { map.put(fine.getUser().getId(), fine); }
    public Optional<Fine> findByUser(User user) { return Optional.ofNullable(map.get(user.getId())); }
}
