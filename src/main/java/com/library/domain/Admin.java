package com.library.domain;

/**
 * Represents an administrator in the library system.
 * <p>
 * The Admin is a specialized type of {@link User} who has elevated
 * permissions to manage the system, such as adding books, managing users,
 * and performing high-level administrative operations.
 * </p>
 *
 * @author Maram
 * @version 1.0
 */
public class Admin extends User {

    /**
     * Creates a new Admin object with the given information.
     *
     * @param id        the unique identifier of the admin
     * @param name      the admin's full name
     * @param email     the admin's email address
     * @param password  the admin's login password
     */
    public Admin(String id, String name, String email, String password) {
        super(id, name, email, password);
    }

}
