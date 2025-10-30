package com.library.app;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AuthService - Sprint 1 (US1.1, US1.2)
 */
class AuthServiceTest {
    private static AuthService authService;

    @BeforeAll
    static void setUpBeforeAll() {
        System.out.println("=== Setting up AuthService tests ===");
        authService = new AuthService();
    }

    @BeforeEach
    void setUpBeforeEach() {
        System.out.println("--- Preparing test ---");
        // Ensure we start with logged out state for each test
        authService.logout();
    }

    @AfterEach
    void tearDownAfterEach() {
        System.out.println("--- Cleaning up after test ---");
        authService.logout();
    }

    @AfterAll
    static void tearDownAfterAll() {
        System.out.println("=== AuthService tests completed ===");
        authService = null;
    }

    @Test
    @DisplayName("US1.1 - Should login successfully with correct credentials")
    void testSuccessfulLogin() {
        // Given - correct credentials
        String username = "admin";
        String password = "password123";

        // When - login attempt
        boolean result = authService.login(username, password);

        // Then - should succeed and be logged in
        assertTrue(result, "Login should succeed with correct credentials");
        assertTrue(authService.isAdminLoggedIn(), "Admin should be logged in after successful login");
    }

    @Test
    @DisplayName("US1.1 - Should fail login with incorrect password")
    void testFailedLoginWithWrongPassword() {
        // Given - wrong password
        String username = "admin";
        String wrongPassword = "wrong123";

        // When - login attempt
        boolean result = authService.login(username, wrongPassword);

        // Then - should fail and not be logged in
        assertFalse(result, "Login should fail with wrong password");
        assertFalse(authService.isAdminLoggedIn(), "Admin should not be logged in after failed login");
    }

    @Test
    @DisplayName("US1.1 - Should fail login with incorrect username")
    void testFailedLoginWithWrongUsername() {
        // Given - wrong username
        String wrongUsername = "wronguser";
        String password = "password123";

        // When - login attempt
        boolean result = authService.login(wrongUsername, password);

        // Then - should fail
        assertFalse(result, "Login should fail with wrong username");
        assertFalse(authService.isAdminLoggedIn());
    }

    @Test
    @DisplayName("US1.2 - Should logout successfully")
    void testLogout() {
        // Given - logged in admin
        authService.login("admin", "password123");
        assertTrue(authService.isAdminLoggedIn(), "Precondition: admin should be logged in");

        // When - logout
        authService.logout();

        // Then - should be logged out
        assertFalse(authService.isAdminLoggedIn(), "Admin should be logged out after logout");
    }

    @Test
    @DisplayName("Should maintain logged in state across multiple operations")
    void testLoginStatePersistence() {
        // Given
        authService.login("admin", "password123");

        // When - perform multiple checks
        boolean state1 = authService.isAdminLoggedIn();
        boolean state2 = authService.isAdminLoggedIn();
        boolean state3 = authService.isAdminLoggedIn();

        // Then - state should remain consistent
        assertTrue(state1, "First check should be logged in");
        assertTrue(state2, "Second check should be logged in");
        assertTrue(state3, "Third check should be logged in");
    }
}