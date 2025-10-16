package test_library;

import org.junit.jupiter.api.Test;
import software.library.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibrarianTest {
    @Test
    void search_by_title_author_isbn_returns_matches() {
        Librarian lib = new Librarian();
        List<Book> catalog = Arrays.asList(
                new Book("Engineering", "Maram", "9780132350884"),
                new Book("ComputerEng", "Aseel", "9780201485677")
        );

        assertEquals(1, lib.search(catalog, "Engineering").size());
        assertEquals(2, lib.search(catalog, "m").size());
        assertEquals(1, lib.search(catalog, "0884").size());
        assertEquals(0, lib.search(catalog, "zzz").size());
    }

}
