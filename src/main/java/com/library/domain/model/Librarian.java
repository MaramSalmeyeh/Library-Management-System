package com.library.domain.model;

import java.util.List;
import java.util.stream.Collectors;

public class Librarian {

    public List<Book> search(List<Book> catalog, String query) {
        String k = query.toLowerCase();
        return catalog.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(k)
                        || b.getAuthor().toLowerCase().contains(k)
                        || b.getIsbn().toLowerCase().contains(k))
                .collect(Collectors.toList());
    }
}
