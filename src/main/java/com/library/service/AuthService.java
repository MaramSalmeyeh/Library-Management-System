package com.library.service;

import com.library.domain.Admin;
import com.library.domain.FileStorage;
import com.library.domain.Librarian;
import com.library.domain.User;

import java.util.List;

/**
 * Handles authentication and session management for admins, librarians, and users.
 * <p>
 * The {@code AuthService} verifies login credentials against data stored in
 * {@link FileStorage}, tracks the currently logged-in account, and provides
 * helper methods to check login state.
 * </p>
 *
 * <p>
 * Only one account type (Admin, Librarian, or User) can be logged in at a time.
 * Logging in with one role automatically logs out any other role.
 * </p>
 *
 * @author Maram
 * @version 1.0
 */
public class AuthService {

    /**
     * Storage handler used to load account data.
     */
    private final FileStorage storage;

    /**
     * Currently logged-in admin, or null if none.
     */
    private Admin currentAdmin;

    /**
     * Currently logged-in librarian, or null if none.
     */
    private Librarian currentLibrarian;

    /**
     * Currently logged-in user, or null if none.
     */
    private User currentUser;

    /**
     * Creates a new authentication service.
     *
     * @param storage the storage backend containing account records
     */
    public AuthService(FileStorage storage) {
        this.storage = storage;
    }

    // ===================== ADMIN LOGIN =====================

    /**
     * Attempts to log in an admin using an email and password.
     *
     * @param email    the admin email
     * @param password the admin password
     * @return the authenticated {@link Admin}, or null if credentials are invalid
     */
    public Admin login(String email, String password) {
        List<Admin> admins = storage.loadAdmins();
        for (Admin a : admins) {
            if (a.getEmail().equalsIgnoreCase(email) &&
                    a.getPassword().equals(password)) {

                currentAdmin = a;
                currentLibrarian = null;
                currentUser = null;
                return a;
            }
        }
        return null;
    }

    /**
     * @return true if an admin is currently logged in
     */
    public boolean isAdminLoggedIn() {
        return currentAdmin != null;
    }

    /**
     * @return the currently logged-in admin, or null if none
     */
    public Admin getCurrentAdmin() {
        return currentAdmin;
    }

    // ===================== LIBRARIAN LOGIN =====================

    /**
     * Attempts to log in a librarian using an email and password.
     *
     * @param email    librarian email
     * @param password librarian password
     * @return the authenticated {@link Librarian}, or null if invalid credentials
     */
    public Librarian loginLibrarian(String email, String password) {
        List<Librarian> librarians = storage.loadLibrarians();
        for (Librarian l : librarians) {
            if (l.getEmail().equalsIgnoreCase(email) &&
                    l.getPassword().equals(password)) {

                currentLibrarian = l;
                currentAdmin = null;
                currentUser = null;
                return l;
            }
        }
        return null;
    }

    /**
     * @return true if a librarian is logged in
     */
    public boolean isLibrarianLoggedIn() {
        return currentLibrarian != null;
    }

    /**
     * @return the currently logged-in librarian, or null if none
     */
    public Librarian getCurrentLibrarian() {
        return currentLibrarian;
    }

    // ===================== USER LOGIN =====================

    /**
     * Attempts to log in a regular user.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return the authenticated {@link User}, or null if invalid credentials
     */
    public User loginUser(String email, String password) {
        List<User> users = storage.loadUsers();
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) &&
                    u.getPassword().equals(password)) {

                currentUser = u;
                currentAdmin = null;
                currentLibrarian = null;
                return u;
            }
        }
        return null;
    }

    /**
     * @return true if a regular user is logged in
     */
    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    /**
     * @return the currently logged-in user, or null if none
     */
    public User getCurrentUser() {
        return currentUser;
    }

    // ===================== LOGOUT =====================

    /**
     * Logs out any currently logged-in admin, librarian, or user.
     * Clears the authentication state for all roles.
     */
    public void logout() {
        currentAdmin = null;
        currentLibrarian = null;
        currentUser = null;
    }
}
