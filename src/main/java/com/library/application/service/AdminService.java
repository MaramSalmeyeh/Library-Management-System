package com.library.application.service;


public class AdminService {
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "1234";

    private boolean loggedIn = false;

    /**
     * Authenticate using the demo credentials.
     * @param username supplied username
     * @param password supplied password
     * @return true if authenticated
     */
    public boolean authenticate(String username, String password) {
        boolean ok = DEFAULT_USERNAME.equals(username) && DEFAULT_PASSWORD.equals(password);
        loggedIn = ok;
        return ok;
    }

    /**
     * Log the admin out.
     */
    public void logout() {
        loggedIn = false;
    }

    /**
     * Whether an admin is currently logged in.
     * @return true if logged in
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }
}

