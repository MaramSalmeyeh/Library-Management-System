package com.library.domain.model;

import org.junit.jupiter.api.*;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminTest {

    private Admin admin;
    private List<Book> catalog;

    @BeforeAll
    void beforeAll() {
        System.out.println("BeforeAll(AdminTest)");
    }

    @AfterAll
    void afterAll() {
        System.out.println("AfterAll(AdminTest)");
    }

    @BeforeEach
    void setUp() {
        admin = new Admin("root", "1234");
        catalog = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        admin = null;
        catalog = null;
    }

    @Test
    @DisplayName("Login/Logout and addBook requires login")
    void login_logout_and_addBook_requires_login() {
        // before login
        assertFalse(admin.isLoggedIn());
        assertThrows(IllegalStateException.class,
                () -> admin.addBook(catalog, new Book("X", "Y", "Z")));

        // login
        assertTrue(admin.login("root", "1234"));
        assertTrue(admin.isLoggedIn());

        // add book when logged in
        assertTrue(admin.addBook(catalog, new Book("Engineering", "Maram", "9780132350884")));
        assertEquals(1, catalog.size());

        // logout blocks actions
        admin.logout();
        assertFalse(admin.isLoggedIn());
        assertThrows(IllegalStateException.class,
                () -> admin.addBook(catalog, new Book("Another", "Author", "111")));
    }

    @Test
    @DisplayName("Invalid login fails")
    void invalid_login_fails() {
        assertFalse(admin.login("root", "wrong"));
        assertFalse(admin.isLoggedIn());
    }
}
