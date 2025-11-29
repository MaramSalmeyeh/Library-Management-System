package com.library.domain;

import java.io.IOException;
import java.nio.file.*;
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

    private Path booksFile() {
        return baseDir.resolve("books.txt");
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
            Files.write(adminsFile(), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save admins", e);
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
            Files.write(booksFile(), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save books", e);
        }
    }
}
