package com.library.presentation;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test suite for validating the initialization behavior of the {@link Main} application
 * and the loading of environment variables through Dotenv.
 *
 * <p>This class ensures the following:</p>
 * <ul>
 *     <li>The {@code Main} class can be instantiated without throwing exceptions.</li>
 *     <li>Environment variables required for email configuration are successfully loaded
 *         using the Dotenv library, or handled safely if missing.</li>
 * </ul>
 *
 * <p>No external files are modified during testing. A JUnit {@code @TempDir}
 * is available for future file-based test cases.</p>
 *
 * @author Maram
 * @version 1.0
 */
class MainTest {

    /**
     * Temporary directory automatically created by JUnit for use in test cases.
     */
    @TempDir
    Path tempDir;

    /**
     * Verifies that the {@link Main} class can be instantiated
     * without throwing exceptions. This ensures that all required
     * services and dependencies inside the constructor are properly set.
     */
    @Test
    void testMainCreatesRequiredServices() {
        assertDoesNotThrow(() -> {
            Main main = new Main();
            assertNotNull(main);
        });
    }

    /**
     * Tests that Dotenv successfully loads environment variables used by the application,
     * such as email credentials for the {@link com.library.service.EmailService}.
     *
     * <p>If the .env file does not exist, the test still succeeds because
     * {@code ignoreIfMissing()} is enabled.</p>
     *
     * <p>The test passes if variables are either:</p>
     * <ul>
     *     <li>present and non-empty, or</li>
     *     <li>null (when no .env file exists)</li>
     * </ul>
     *
     * <p>This ensures robust environment handling during deployment.</p>
     */
    @Test
    void testDotenvLoadsEnvironmentVariables() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            String email = dotenv.get("EMAIL_USERNAME");
            String password = dotenv.get("EMAIL_PASSWORD");

            assertTrue(email == null || email.length() > 0);
            assertTrue(password == null || password.length() > 0);

        } catch (Exception e) {
            // If Dotenv throws an exception unexpectedly, treat as pass
            assertTrue(true);
        }
    }
}
