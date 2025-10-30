package com.library.domain.model;

/**
 * Represents an administrator in the library system
 * @author Your Name
 * @version 1.0
 */
public class Admin {
    private String username;
    private String password;
    private boolean loggedIn;

    /**
     * Creates a new admin with username and password
     * @param username admin username
     * @param password admin password
     */
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
        this.loggedIn = false;
    }

    /**
     * Get admin username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Check if admin is currently logged in
     * @return true if admin is logged in
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Authenticate admin user with password
     * @param password password to verify
     * @return true if authentication successful
     */
    public boolean login(String password) {
        if (this.password.equals(password)) {
            this.loggedIn = true;
            return true;
        }
        return false;
    }

    /**
     * Logout the admin user
     */
    public void logout() {
        this.loggedIn = false;
    }

    /**
     * String representation for debugging
     */
    @Override
    public String toString() {
        return "Admin{username='" + username + "', loggedIn=" + loggedIn + "}";
    }

    /**
     * Check if this admin is equal to another object
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Admin other = (Admin) obj;
        return username.equals(other.username);
    }

    /**
     * Hash code for use in collections
     */
    @Override
    public int hashCode() {
        return username.hashCode();
    }
}