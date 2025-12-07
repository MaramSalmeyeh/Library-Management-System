package com.library.domain;

/**
 * Represents a generic user in the library system.
 * <p>
 * This class serves as the base for all user-related types, such as
 * {@link Admin} and {@link Librarian}. It stores essential user
 * information such as ID, name, email, and password.
 * </p>
 *
 * @author Maram
 * @version 1.0
 */
public class User {

    /**
     * Unique identifier of the user.
     */
    private final String id;

    /**
     * Full name of the user.
     */
    private final String name;

    /**
     * Email address of the user.
     */
    private final String email;

    /**
     * Password for user authentication.
     */
    private final String password;

    /**
     * Creates a new User instance with the given details.
     *
     * @param id        the user's unique ID
     * @param name      the user's name
     * @param email     the user's email address
     * @param password  the user's login password
     */
    public User(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * @return the user's ID
     */
    public String getId() {
        return id;
    }

    /**
     * @return the user's full name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }
}
