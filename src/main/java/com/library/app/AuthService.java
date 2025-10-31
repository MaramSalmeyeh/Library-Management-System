package com.library.app;

public class AuthService {
    private boolean loggedIn = false;

    // غير كلمة المرور هنا
    private final String USERNAME = "admin";
    private final String PASSWORD = "password123";

    public boolean login(String username, String password) {
        System.out.println("Debug: Username='" + username + "', Password='" + password + "'");

        if (USERNAME.equals(username) && PASSWORD.equals(password)) {
            loggedIn = true;
            System.out.println(" Login successful!");
            return true;
        } else {
            System.out.println(" Login failed! Expected: '" + PASSWORD + "'");
            return false;
        }
    }

    public void logout() {
        loggedIn = false;
    }

    public boolean isAdminLoggedIn() {
        return loggedIn;
    }
}