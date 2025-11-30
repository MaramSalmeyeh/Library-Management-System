package com.library.service;

import com.library.domain.Admin;
import com.library.domain.FileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @TempDir
    Path tempDir;

    private AuthService authService;

    @BeforeEach
    void setUp() throws IOException {

        Path adminsFile = tempDir.resolve("admins.txt");
        List<String> adminsLines = List.of(
                "1;Admin One;admin@example.com;1234",
                "2;Another Admin;admin2@example.com;abcd"
        );
        Files.write(adminsFile, adminsLines);


        FileStorage storage = new FileStorage(tempDir.toString());

        authService = new AuthService(storage);
    }

    @Test
    void login_withValidCredentials_returnsAdminAndMarksLoggedIn() {
        Admin admin = authService.login("admin@example.com", "1234");

        assertNotNull(admin, "Login should return an Admin for valid credentials");
        assertEquals("Admin One", admin.getName());
        assertTrue(authService.isAdminLoggedIn(), "Admin should be marked as logged in");
    }

    @Test
    void login_withInvalidPassword_returnsNullAndNotLoggedIn() {
        Admin admin = authService.login("admin@example.com", "wrong");

        assertNull(admin, "Login with wrong password should return null");
        assertFalse(authService.isAdminLoggedIn(), "No admin should be logged in");
    }

    @Test
    void login_withUnknownEmail_returnsNull() {
        Admin admin = authService.login("unknown@example.com", "1234");

        assertNull(admin, "Login with unknown email should return null");
        assertFalse(authService.isAdminLoggedIn());
    }

    @Test
    void logout_clearsCurrentAdmin() {

        authService.login("admin@example.com", "1234");
        assertTrue(authService.isAdminLoggedIn());


        authService.logout();

        assertFalse(authService.isAdminLoggedIn(), "After logout no admin should be logged in");
    }
}
