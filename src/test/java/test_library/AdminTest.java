package test_library;

import org.junit.jupiter.api.Test;
import software.library.Admin;
import software.library.Book;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    @Test
    void login_withValidCredentials_succeeds_and_logout_blocksActions() {
        Admin a = new Admin("admin", "admin");
        assertTrue(a.login("admin","admin"));     // US1.1
        assertTrue(a.isLoggedIn());
        a.logout();                               // US1.2
        assertFalse(a.isLoggedIn());

        List<Book> catalog = new ArrayList<>();
        assertThrows(IllegalStateException.class,
                () -> a.addBook(catalog, new Book("T","A","ISBN")));
    }

    @Test
    void login_withInvalidCredentials_fails() {
        Admin a = new Admin("admin","admin");
        assertFalse(a.login("wrong","pass"));
        assertFalse(a.isLoggedIn());
    }

    @Test
    void addBook_whenLoggedIn_addsAndIsSearchable() {
        Admin a = new Admin("admin","admin");
        List<Book> catalog = new ArrayList<>();
        a.login("admin","admin");
        assertTrue(a.addBook(catalog, new Book("Clean Code","Robert C. Martin","9780132350884"))); // US1.3
        assertEquals(1, catalog.size());
    }
}
