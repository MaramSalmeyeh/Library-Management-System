package com.library.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageTest {

    @TempDir
    Path tempDir;

    private FileStorage newStorage() {
        return new FileStorage(tempDir.toString());
    }

    @Test
    void saveAndLoadUsers_roundTrip() {
        FileStorage storage = newStorage();
        List<User> users = List.of(
                new User("U1", "Dana", "dana@example.com", "pwd")
        );

        storage.saveUsers(users);
        List<User> loaded = storage.loadUsers();

        assertEquals(1, loaded.size());
        User u = loaded.get(0);
        assertEquals("U1", u.getId());
        assertEquals("Dana", u.getName());
        assertEquals("dana@example.com", u.getEmail());
        assertEquals("pwd", u.getPassword());
    }

    @Test
    void saveAndLoadAdmins_roundTrip() {
        FileStorage storage = newStorage();

        List<Admin> admins = List.of(
                new Admin("A1", "Admin", "admin@example.com", "a")
        );

        storage.saveAdmins(admins);

        List<Admin> loadedAdmins = storage.loadAdmins();
        assertEquals(1, loadedAdmins.size());
        assertEquals("A1", loadedAdmins.get(0).getId());
        assertEquals("Admin", loadedAdmins.get(0).getName());
    }

    @Test
    void loadLibrarians_readsFromFile() throws IOException {

        Path librariansFile = tempDir.resolve("librarians.txt");
        Files.writeString(
                librariansFile,
                "L1;Lib;lib@example.com;lpass"   // id;name;email;password
        );

        FileStorage storage = newStorage();

        List<Librarian> loadedLibs = storage.loadLibrarians();
        assertEquals(1, loadedLibs.size());
        Librarian lib = loadedLibs.get(0);
        assertEquals("L1", lib.getId());
        assertEquals("Lib", lib.getName());
        assertEquals("lib@example.com", lib.getEmail());
        assertEquals("lpass", lib.getPassword());
    }

    @Test
    void saveAndLoadBooks_roundTrip() {
        FileStorage storage = newStorage();
        List<Book> books = List.of(
                new Book("B1", "Title", "Author", "ISBN", false)
        );

        storage.saveBooks(books);
        List<Book> loaded = storage.loadBooks();

        assertEquals(1, loaded.size());
        Book b = loaded.get(0);
        assertEquals("B1", b.getId());
        assertEquals("Title", b.getTitle());
        assertEquals("Author", b.getAuthor());
        assertEquals("ISBN", b.getIsbn());
        assertFalse(b.isBorrowed());
    }

    @Test
    void saveAndLoadLoans_roundTrip_includesMediaType() {
        FileStorage storage = newStorage();

        LocalDate borrow = LocalDate.of(2024, 1, 1);
        LocalDate due    = LocalDate.of(2024, 1, 10);

        Loan loan = new Loan("L1", "U1", "B1", borrow, due, null, MediaType.CD);

        storage.saveLoans(List.of(loan));
        List<Loan> loaded = storage.loadLoans();

        assertEquals(1, loaded.size());
        Loan l = loaded.get(0);
        assertEquals("L1", l.getId());
        assertEquals("U1", l.getUserId());
        assertEquals("B1", l.getBookId());
        assertEquals(borrow, l.getBorrowDate());
        assertEquals(due, l.getDueDate());
        assertNull(l.getReturnDate());
        assertEquals(MediaType.CD, l.getMediaType());
    }

    @Test
    void loadLoans_whenFileMissing_returnsEmptyList() {
        FileStorage storage = newStorage();

        List<Loan> loans = storage.loadLoans();

        assertNotNull(loans);
        assertTrue(loans.isEmpty());
    }

    @Test
    void saveAndLoadFines_roundTrip() {
        FileStorage storage = newStorage();
        Fine fine = new Fine("F1", "U1", 30.5, false);

        storage.saveFines(List.of(fine));
        List<Fine> loaded = storage.loadFines();

        assertEquals(1, loaded.size());
        Fine f = loaded.get(0);
        assertEquals("F1", f.getId());
        assertEquals("U1", f.getUserId());
        assertEquals(30.5, f.getAmount());
        assertFalse(f.isPaid());
    }

    @Test
    void loadUsers_whenFileMissing_returnsEmptyList() {
        FileStorage storage = newStorage();

        List<User> users = storage.loadUsers();

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }
}
