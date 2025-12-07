package com.library.domain;

/**
 * Represents a librarian in the library system.
 * <p>
 * A Librarian is a specialized type of {@link User} responsible for
 * managing books, assisting users, and handling borrowing operations.
 * </p>
 *
 * @author Maram
 * @version 1.0
 */
public class Librarian extends User {

    /**
     * Creates a new Librarian object with the given information.
     *
     * @param id        the librarian's unique identifier
     * @param name      the librarian's full name
     * @param email     the librarian's email address
     * @param password  the librarian's password
     */
    public Librarian(String id, String name, String email, String password) {
        super(id, name, email, password);
    }

}
