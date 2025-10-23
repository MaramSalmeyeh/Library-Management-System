package com.library.domain.model;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserTest {

    private User user;

    @BeforeAll
    void beforeAll() {
        System.out.println("BeforeAll(UserTest)");
    }

    @AfterAll
    void afterAll() {
        System.out.println("AfterAll(UserTest)");
    }

    @BeforeEach
    void setUp() {
        user = new User("u1", "Mona");
    }

    @AfterEach
    void tearDown() {
        user = null;
    }

    @Test
    @DisplayName("Basic getters and initial borrowed state")
    void basic_getters_and_initial_state() {
        assertEquals("u1", user.getId());
        assertEquals("Mona", user.getName());
        assertEquals(0, user.countBorrowed());
        assertFalse(user.hasBorrowed("xyz"));
    }

    @Test
    @DisplayName("Borrow/Return flow toggles availability")
    void borrow_and_return_flow() {
        Book b = new Book("Engineering", "Maram", "9780132350884");
        assertTrue(b.isAvailable());
        assertTrue(user.borrow(b));       // should borrow when available
        assertFalse(b.isAvailable());
        assertTrue(user.hasBorrowed("9780132350884"));

        assertTrue(user.returnBook(b));   // return should flip availability
        assertTrue(b.isAvailable());
    }
}
