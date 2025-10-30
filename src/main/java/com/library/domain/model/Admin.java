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

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
        this.loggedIn = false;
    }

    // Getters
    public String getUsername() { return username; }
    public boolean isLoggedIn() { return loggedIn; }

    /**
     * Authenticate admin user
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

    public void logout() {
        this.loggedIn = false;
    }
}