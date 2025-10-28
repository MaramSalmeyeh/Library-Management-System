package com.library.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents an admin in the system.
 */
public class Admin {
    private final String username;
    private final String passwordPlain;
    private boolean loggedIn;

    public Admin(String username, String passwordPlain) {
        this.username = username;
        this.passwordPlain = passwordPlain;
    }

    /**
     * Returns the admin's username.
     *
     * @return the username
     */
    public String getUsername() { return username; }

    /**
     * Returns the stored plain-text password.
     * NOTE: storing or exposing plain-text passwords is unsafe. Prefer hashed
     * passwords in production. This accessor is kept for compatibility with
     * existing tests/examples.
     * @return the plain-text password
     */
    public String getPasswordPlain() { return passwordPlain; }

    /**
     * Whether the admin is currently logged in.
     *
     * @return true if logged in
     */
    public boolean isLoggedIn() { return loggedIn; }

    /**
     * Set the admin's logged-in state.
     *
     * @param loggedIn new logged-in state
     */
    public void setLoggedIn(boolean loggedIn) { this.loggedIn = loggedIn; }

    /**
     * Attempt to log in with the provided credentials.
     * Returns true if credentials match the stored username/password and
     * sets the logged-in state.
     *
     * @param username supplied username
     * @param password supplied password
     * @return true when login succeeded
     */
    public boolean login(String username, String password) {
        boolean ok = Objects.equals(this.username, username) && Objects.equals(this.passwordPlain, password);
        this.loggedIn = ok;
        return ok;
    }

    /**
     * Logout the admin (clears logged-in state).
     */
    public void logout() {
        this.loggedIn = false;
    }

    /**
     * Add a book to the provided catalog. Requires the admin to be logged in.
     *
     * @param catalog mutable list of books
     * @param book book to add
     * @return true if added
     * @throws IllegalStateException when the admin is not logged in
     */
    public boolean addBook(List<Book> catalog, Book book) {
        if (!isLoggedIn()) throw new IllegalStateException("Admin must be logged in to add books");
        return catalog.add(book);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof Admin admin) {
            return Objects.equals(username, admin.username);
        }
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(username); }
}
