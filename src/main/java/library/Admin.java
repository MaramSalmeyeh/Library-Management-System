package library;

import java.util.List;
import java.util.Objects;

public class Admin {
    private final String username;
    private final String passwordPlain;
    private boolean loggedIn;

    public Admin(String username, String passwordPlain) {
        this.username = username;
        this.passwordPlain = passwordPlain;
    }

    public boolean login(String username, String passwordPlain) {
        loggedIn = Objects.equals(this.username, username)
                && Objects.equals(this.passwordPlain, passwordPlain);
        return loggedIn;
    }

    public void logout() { loggedIn = false; }
    public boolean isLoggedIn() { return loggedIn; }

    public boolean addBook(List<Book> catalog, Book book) {
        if (!loggedIn) throw new IllegalStateException("Admin must be logged in.");
        return catalog.add(book);
    }
}
