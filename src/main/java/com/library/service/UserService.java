package com.library.service;

import com.library.domain.FileStorage;
import com.library.domain.User;

import java.util.List;

/**
 * Service responsible for managing user accounts in the library system.
 * <p>
 * This includes:
 * <ul>
 *     <li>User registration</li>
 *     <li>User login and logout</li>
 *     <li>Searching users by ID</li>
 *     <li>Unregistering (deleting) users safely</li>
 * </ul>

 *
 * <p>
 * The service integrates with {@link LoanService} and {@link FineService}
 * to ensure users cannot be deleted while they have:
 * <ul>
 *     <li>Active loans</li>
 *     <li>Unpaid fines</li>
 * </ul>

 *
 * <p>
 * All user data is persisted through {@link FileStorage}.
 * </p>
 *
 * @author Maram
 * @version 1.0
 */
public class UserService {

    /**
     * Storage backend for loading and saving users.
     */
    private final FileStorage storage;

    /**
     * Tracks the currently logged-in user (nullable).
     */
    private User currentUser;

    /**
     * Creates a UserService using the provided storage and email service.
     * (Email service currently unused but reserved for future extensions.)
     *
     * @param storage      file storage backend
     * @param emailService email sending service (optional)
     */
    public UserService(FileStorage storage, EmailService emailService) {
        this.storage = storage;
    }

    /**
     * Creates a UserService with only a storage backend.
     *
     * @param storage file storage backend
     */
    public UserService(FileStorage storage) {
        this.storage = storage;
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return current user or null if none logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * @return true if a user is currently logged in
     */
    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    /**
     * Registers a new user in the system.
     * <p>
     * Validation performed:
     * <ul>
     *     <li>Email must not already exist</li>
     * </ul>

     *
     * @param name     user's full name
     * @param email    user's email address
     * @param password user's password
     * @return the newly created {@link User}
     *
     * @throws IllegalArgumentException if the email is already registered
     */
    public User register(String name, String email, String password) {
        List<User> users = storage.loadUsers();

        // Email format validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Email format example: user@example.com");
        }

        // Password validation
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$")) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters, contain upper & lower case letters, a digit, and a special character"
            );
        }

        // Check if email already exists
        boolean exists = users.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
        if (exists) {
            throw new IllegalArgumentException("Email already registered");
        }

        String id = "U" + (users.size() + 1);

        User user = new User(id, name, email, password);
        users.add(user);
        storage.saveUsers(users);
        return user;
    }


    /**
     * Attempts to authenticate a user.
     *
     * @param email    email entered
     * @param password password entered
     * @return the matching {@link User}, or null if credentials are invalid
     */
    public User login(String email, String password) {
        List<User> users = storage.loadUsers();

        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) &&
                    u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Logs out the currently logged-in user.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Searches for a user by ID.
     *
     * @param userId ID to search for
     * @return the user with the given ID, or null if not found
     */
    public User findById(String userId) {
        return storage.loadUsers()
                .stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Unregisters (deletes) a user from the system.
     * <p>
     * A user can only be removed if:
     * <ul>
     *     <li>They have no active loans (checked via {@link LoanService})</li>
     *     <li>They have no unpaid fines (checked via {@link FineService})</li>
     * </ul>
     
     *
     * @param userId      ID of the user to remove
     * @param loanService loan service to validate active loans
     * @param fineService fine service to validate outstanding balances
     *
     * @throws IllegalStateException    if the user has active loans or unpaid fines
     * @throws IllegalArgumentException if the user does not exist
     */
    public void unregisterUser(String userId,
                               LoanService loanService,
                               FineService fineService) {

        if (loanService != null && loanService.hasActiveLoans(userId)) {
            throw new IllegalStateException(
                    "User has active loans. Cannot unregister until all books are returned."
            );
        }

        if (fineService != null && fineService.getUserOutstandingBalance(userId) > 0.0) {
            throw new IllegalStateException(
                    "User has unpaid fines. Cannot unregister until all fines are paid."
            );
        }

        List<User> users = storage.loadUsers();
        boolean removed = users.removeIf(u -> u.getId().equals(userId));

        if (!removed) {
            throw new IllegalArgumentException("User with id " + userId + " not found.");
        }

        storage.saveUsers(users);

        // If the removed user was logged in, log them out
        if (currentUser != null && currentUser.getId().equals(userId)) {
            currentUser = null;
        }
    }
}
