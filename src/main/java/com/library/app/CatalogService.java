package com.library.app;


import com.library.domain.model.Admin;
import com.library.domain.model.Book;
import java.util.List;

public class CatalogService {

    public boolean addBook(Admin admin, List<Book> catalog, Book book) {
        if (!admin.isLoggedIn()) {
            throw new IllegalStateException("Admin must be logged in.");
        }
        return catalog.add(book);
    }
}
