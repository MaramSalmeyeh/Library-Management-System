package com.library.service;

import com.library.domain.Admin;
import com.library.domain.FileStorage;
import com.library.domain.Librarian;

import java.util.List;

public class AuthService {

    private final FileStorage storage;

    private Admin currentAdmin;
    private Librarian currentLibrarian;

    public AuthService(FileStorage storage) {
        this.storage = storage;
    }

    // ===== Admin login =====

    /**
     * Sprint 1: Admin login
     */
    public Admin login(String email, String password) {
        List<Admin> admins = storage.loadAdmins();

        for (Admin admin : admins) {
            if (admin.getEmail().equalsIgnoreCase(email)
                    && admin.getPassword().equals(password)) {
                currentAdmin = admin;
                return admin;
            }
        }

        currentAdmin = null;
        return null;
    }

    public boolean isAdminLoggedIn() {
        return currentAdmin != null;
    }

    public Admin getCurrentAdmin() {
        return currentAdmin;
    }

    // ===== Librarian login =====

    /**
     * Sprint 2: Librarian login
     */
    public Librarian loginLibrarian(String email, String password) {
        List<Librarian> librarians = storage.loadLibrarians();

        for (Librarian librarian : librarians) {
            if (librarian.getEmail().equalsIgnoreCase(email)
                    && librarian.getPassword().equals(password)) {
                currentLibrarian = librarian;
                return librarian;
            }
        }

        currentLibrarian = null;
        return null;
    }

    public boolean isLibrarianLoggedIn() {
        return currentLibrarian != null;
    }

    public Librarian getCurrentLibrarian() {
        return currentLibrarian;
    }

    // ===== Logout مشترك =====

    public void logout() {
        currentAdmin = null;
        currentLibrarian = null;
    }
}
