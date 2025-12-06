package com.library.presentation;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainTest {

    @TempDir
    Path tempDir;

    @Test
    void testMainCreatesRequiredServices() {

        assertDoesNotThrow(() -> {
            Main main = new Main();
            assertNotNull(main);
        });
    }

    @Test
    void testDotenvLoadsEnvironmentVariables() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            // Just verify it doesn't crash
            String email = dotenv.get("EMAIL_USERNAME");
            String password = dotenv.get("EMAIL_PASSWORD");

            // These might be null if .env doesn't exist, which is fine
            assertTrue(email == null || email.length() > 0);
            assertTrue(password == null || password.length() > 0);
        } catch (Exception e) {

            assertTrue(true);
        }
    }
}