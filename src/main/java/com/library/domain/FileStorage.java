package com.library.domain;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {

    private final Path baseDir;

    public FileStorage(String baseDirName) {
        this.baseDir = Paths.get(baseDirName);
    }




    private Path adminsFile() {
        return baseDir.resolve("admins.txt");
    }

    private Path librariansFile() {
        return baseDir.resolve("librarians.txt");
    }

    private Path usersFile() {
        return baseDir.resolve("users.txt");
    }


    private Path booksFile() {
        return baseDir.resolve("books.txt");
    }

    private Path loansFile() {
        return baseDir.resolve("loans.txt");
    }

    private Path finesFile() {
        return baseDir.resolve("fines.txt");
    }



    public List<Admin> loadAdmins() {
        List<Admin> admins = new ArrayList<>();
        try {
            if (!Files.exists(adminsFile())) {
                return admins;
            }
            for (String line : Files.readAllLines(adminsFile())) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";");
                if (parts.length < 4) continue;
                String id = parts[0];
                String name = parts[1];
                String email = parts[2];
                String password = parts[3];
                admins.add(new Admin(id, name, email, password));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load admins", e);
        }
        return admins;
    }

    public void saveAdmins(List<Admin> admins) {
        List<String> lines = new ArrayList<>();
        for (Admin a : admins) {
            String line = String.join(";",
                    a.getId(),
                    a.getName(),
                    a.getEmail(),
                    a.getPassword()
            );
            lines.add(line);
        }
        try {
            Files.createDirectories(baseDir);
            Files.write(adminsFile(), lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save admins", e);
        }
    }



    public List<Librarian> loadLibrarians() {
        List<Librarian> librarians = new ArrayList<>();
        try {
            if (!Files.exists(librariansFile())) {
                return librarians;
            }
            for (String line : Files.readAllLines(librariansFile())) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";");
                if (parts.length < 4) continue;
                String id = parts[0];
                String name = parts[1];
                String email = parts[2];
                String password = parts[3];
                librarians.add(new Librarian(id, name, email, password));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load librarians", e);
        }
        return librarians;
    }


    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try {
            if (!Files.exists(usersFile())) {
                return users; // لا يوجد ملف → برجع ليست فاضية
            }
            for (String line : Files.readAllLines(usersFile())) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";");
                if (parts.length < 4) continue;

                String id = parts[0];
                String name = parts[1];
                String email = parts[2];
                String password = parts[3];

                users.add(new User(id, name, email, password));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load users.txt", e);
        }
        return users;
    }

    public void saveUsers(List<User> users) {
        List<String> lines = new ArrayList<>();
        for (User u : users) {
            String line = String.join(";",
                    u.getId(),
                    u.getName(),
                    u.getEmail(),
                    u.getPassword()
            );
            lines.add(line);
        }
        try {
            Files.createDirectories(baseDir);
            Files.write(usersFile(), lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save users.txt", e);
        }
    }


    public List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        try {
            if (!Files.exists(booksFile())) {
                return books;
            }
            for (String line : Files.readAllLines(booksFile())) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";");
                if (parts.length < 5) continue;
                String id = parts[0];
                String title = parts[1];
                String author = parts[2];
                String isbn = parts[3];
                boolean borrowed = Boolean.parseBoolean(parts[4]);
                books.add(new Book(id, title, author, isbn, borrowed));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load books", e);
        }
        return books;
    }

    public void saveBooks(List<Book> books) {
        List<String> lines = new ArrayList<>();
        for (Book b : books) {
            String line = String.join(";",
                    b.getId(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getIsbn(),
                    Boolean.toString(b.isBorrowed())
            );
            lines.add(line);
        }
        try {
            Files.createDirectories(baseDir);
            Files.write(booksFile(), lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save books", e);
        }
    }


    public List<Loan> loadLoans() {
        List<Loan> loans = new ArrayList<>();
        try {
            if (!Files.exists(loansFile())) {
                return loans;
            }
            for (String line : Files.readAllLines(loansFile())) {
                if (line.isBlank()) continue;

                // مهم: -1 عشان ما يحذف الحقول الفاضية بالأخير
                String[] parts = line.split(";", -1);
                if (parts.length < 6) continue;

                String id = parts[0];
                String userId = parts[1];
                String bookId = parts[2];
                LocalDate borrowDate = LocalDate.parse(parts[3]);
                LocalDate dueDate = LocalDate.parse(parts[4]);
                LocalDate returnDate = parts[5].isEmpty() ? null : LocalDate.parse(parts[5]);

                // NEW: mediaType (للأسطر القديمة ما في هذا الحقل → نعتبرها BOOK)
                MediaType mediaType = MediaType.BOOK;
                if (parts.length >= 7 && !parts[6].isBlank()) {
                    mediaType = MediaType.valueOf(parts[6]);
                }

                loans.add(new Loan(id, userId, bookId, borrowDate, dueDate, returnDate, mediaType));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load loans", e);
        }
        return loans;
    }



    public void saveLoans(List<Loan> loans) {
        List<String> lines = new ArrayList<>();
        for (Loan loan : loans) {
            String returnDateStr = (loan.getReturnDate() == null)
                    ? ""
                    : loan.getReturnDate().toString();

            String line = String.join(";",
                    loan.getId(),
                    loan.getUserId(),
                    loan.getBookId(),
                    loan.getBorrowDate().toString(),
                    loan.getDueDate().toString(),
                    returnDateStr,
                    loan.getMediaType().name()   // NEW
            );
            lines.add(line);
        }
        try {
            Files.createDirectories(baseDir);
            Files.write(loansFile(), lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save loans", e);
        }
    }



    public List<Fine> loadFines() {
        List<Fine> fines = new ArrayList<>();
        try {
            if (!Files.exists(finesFile())) {
                return fines;
            }
            for (String line : Files.readAllLines(finesFile())) {
                if (line.isBlank()) continue;
                String[] parts = line.split(";");
                if (parts.length < 4) continue;

                String id = parts[0];
                String userId = parts[1];
                double amount = Double.parseDouble(parts[2]);
                boolean paid = Boolean.parseBoolean(parts[3]);

                fines.add(new Fine(id, userId, amount, paid));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load fines.txt", e);
        }
        return fines;
    }

    public void saveFines(List<Fine> fines) {
        List<String> lines = new ArrayList<>();
        for (Fine fine : fines) {
            String line = String.join(";",
                    fine.getId(),
                    fine.getUserId(),
                    Double.toString(fine.getAmount()),
                    Boolean.toString(fine.isPaid())
            );
            lines.add(line);
        }
        try {
            Files.createDirectories(baseDir);
            Files.write(finesFile(), lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save fines.txt", e);
        }
    }

    public void saveLibrarians(List<Librarian> librarians) {

    }
}
