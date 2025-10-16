package test_library;

import org.junit.jupiter.api.Test;
import software.library.*;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void basicGetters_and_initialBorrowState() {
        User u = new User("u1","Mona");
        assertEquals("u1", u.getId());
        assertEquals("Mona", u.getName());
        assertEquals(0, u.countBorrowed());
        assertFalse(u.hasBorrowed("xyz"));
    }
}
