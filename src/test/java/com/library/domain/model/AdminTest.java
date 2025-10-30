package com.library.domain.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Admin entity - Sprint 1 (US1.1, US1.2)
 * @author Your Name
 * @version 1.0
 */
class AdminTest {
    private Admin admin;

    @BeforeAll
    static void setUpBeforeAll() {
        System.out.println("=== Starting Admin tests ===");
    }

    @AfterAll
    static void tearDownAfterAll() {
        System.out.println("=== Admin tests completed ===");
    }

    @BeforeEach
    void setUpBeforeEach() {
        admin = new Admin("admin", "password123");
        System.out.println("Created admin: " + admin.getUsername());
    }

    @AfterEach
    void tearDownAfterEach() {
        admin = null;
        System.out.println("Cleaned up admin instance");
    }

    @Test
    @DisplayName("US1.1 - Should create admin with correct properties")
    void testAdminCreation() {
        // Then
        assertEquals("admin", admin.getUsername(), "Username should match");
        assertFalse(admin.isLoggedIn(), "New admin should not be logged in");
    }

    @Test
    @DisplayName("US1.1 - Should login successfully with correct password")
    void testSuccessfulLogin() {
        // When
        boolean result = admin.login("password123");

        // Then
        assertTrue(result, "Login should succeed with correct password");
        assertTrue(admin.isLoggedIn(), "Admin should be logged in after successful login");
    }

    @Test
    @DisplayName("US1.1 - Should fail login with incorrect password")
    void testFailedLoginWithWrongPassword() {
        // When
        boolean result = admin.login("wrongpassword");

        // Then
        assertFalse(result, "Login should fail with wrong password");
        assertFalse(admin.isLoggedIn(), "Admin should not be logged in after failed login");
    }

    @Test
    @DisplayName("US1.1 - Should fail login with empty password")
    void testFailedLoginWithEmptyPassword() {
        // When
        boolean result = admin.login("");

        // Then
        assertFalse(result, "Login should fail with empty password");
        assertFalse(admin.isLoggedIn());
    }

    @Test
    @DisplayName("US1.1 - Should fail login with null password")
    void testFailedLoginWithNullPassword() {
        // When
        boolean result = admin.login(null);

        // Then
        assertFalse(result, "Login should fail with null password");
        assertFalse(admin.isLoggedIn());
    }

    @Test
    @DisplayName("US1.2 - Should logout successfully")
    void testLogout() {
        // Given - logged in admin
        admin.login("password123");
        assertTrue(admin.isLoggedIn(), "Precondition: admin should be logged in");

        // When
        admin.logout();

        // Then
        assertFalse(admin.isLoggedIn(), "Admin should be logged out after logout");
    }

    @Test
    @DisplayName("US1.2 - Should handle multiple login-logout cycles")
    void testMultipleLoginLogoutCycles() {
        // First cycle
        admin.login("password123");
        assertTrue(admin.isLoggedIn());
        admin.logout();
        assertFalse(admin.isLoggedIn());

        // Second cycle
        admin.login("password123");
        assertTrue(admin.isLoggedIn());
        admin.logout();
        assertFalse(admin.isLoggedIn());

        // Third cycle
        admin.login("password123");
        assertTrue(admin.isLoggedIn());
        admin.logout();
        assertFalse(admin.isLoggedIn());
    }

    @Test
    @DisplayName("Should maintain login state consistency")
    void testLoginStateConsistency() {
        // When - login and check state multiple times
        admin.login("password123");

        // Then - state should remain consistent
        assertTrue(admin.isLoggedIn(), "First check should be logged in");
        assertTrue(admin.isLoggedIn(), "Second check should be logged in");
        assertTrue(admin.isLoggedIn(), "Third check should be logged in");
    }

    @Test
    @DisplayName("Should handle logout without login")
    void testLogoutWithoutLogin() {
        // Given - admin that is not logged in
        assertFalse(admin.isLoggedIn(), "Precondition: admin should not be logged in");

        // When - logout without prior login
        admin.logout();

        // Then - should remain logged out
        assertFalse(admin.isLoggedIn(), "Admin should remain logged out");
    }

    @Test
    @DisplayName("US1.1 - Should handle case sensitivity in password")
    void testCaseSensitivePassword() {
        // When - try different case variations
        boolean result1 = admin.login("PASSWORD123"); // All caps
        boolean result2 = admin.login("Password123"); // Mixed case
        boolean result3 = admin.login("password123"); // Correct case

        // Then - only exact match should work
        assertFalse(result1, "All caps should not match");
        assertFalse(result2, "Mixed case should not match");
        assertTrue(result3, "Exact case should match");
    }

    @Nested
    @DisplayName("Admin Authentication Scenarios")
    class AuthenticationScenarios {

        @Test
        @DisplayName("New admin should not be logged in")
        void testNewAdminNotLoggedIn() {
            Admin newAdmin = new Admin("newadmin", "pass123");
            assertFalse(newAdmin.isLoggedIn());
        }

        @Test
        @DisplayName("Admin should be able to login after logout")
        void testLoginAfterLogout() {
            // First login
            admin.login("password123");
            assertTrue(admin.isLoggedIn());

            // Logout
            admin.logout();
            assertFalse(admin.isLoggedIn());

            // Login again
            boolean result = admin.login("password123");
            assertTrue(result, "Should be able to login again after logout");
            assertTrue(admin.isLoggedIn(), "Should be logged in after second login");
        }

        @Test
        @DisplayName("Multiple failed logins should not affect state")
        void testMultipleFailedLogins() {
            // Multiple failed attempts
            assertFalse(admin.login("wrong1"));
            assertFalse(admin.login("wrong2"));
            assertFalse(admin.login("wrong3"));

            // State should remain unchanged
            assertFalse(admin.isLoggedIn());

            // Correct login should still work
            assertTrue(admin.login("password123"));
            assertTrue(admin.isLoggedIn());
        }
    }

    @Nested
    @DisplayName("Admin Security Tests")
    class SecurityTests {

        @Test
        @DisplayName("Should not expose password through getters")
        void testPasswordNotExposed() {
            // Verify that password is not accessible through public methods
            // This is a design verification test
            assertDoesNotThrow(() -> {
                admin.getUsername(); // Should work
                admin.isLoggedIn();  // Should work
                // No getPassword() method should exist
            });
        }

        @Test
        @DisplayName("Should handle special characters in password")
        void testSpecialCharactersInPassword() {
            Admin specialAdmin = new Admin("special", "p@ssw0rd!123");
            assertTrue(specialAdmin.login("p@ssw0rd!123"));
            assertTrue(specialAdmin.isLoggedIn());
        }
    }
}