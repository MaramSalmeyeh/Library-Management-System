package com.library.service;

import com.library.domain.Admin;
import com.library.domain.FileStorage;
import com.library.domain.Librarian;
import com.library.domain.User;

import java.util.List;

public class AuthService {

    private final FileStorage storage;

    private Admin currentAdmin;
    private Librarian currentLibrarian;
    private User currentUser;

    public AuthService(FileStorage storage) {
        this.storage = storage;
    }



    public Admin login(String email, String password) {
        List<Admin> admins = storage.loadAdmins();
        for (Admin a : admins) {
            if (a.getEmail().equalsIgnoreCase(email) &&
                    a.getPassword().equals(password)) {

                currentAdmin = a;
                currentLibrarian = null;
                currentUser = null;
                return a;
            }
        }
        return null;
    }

    public boolean isAdminLoggedIn() {
        return currentAdmin != null;
    }

    public Admin getCurrentAdmin() {
        return currentAdmin;
    }



    public Librarian loginLibrarian(String email, String password) {
        List<Librarian> librarians = storage.loadLibrarians();
        for (Librarian l : librarians) {
            if (l.getEmail().equalsIgnoreCase(email) &&
                    l.getPassword().equals(password)) {

                currentLibrarian = l;
                currentAdmin = null;
                currentUser = null;
                return l;
            }
        }
        return null;
    }

    public boolean isLibrarianLoggedIn() {
        return currentLibrarian != null;
    }

    public Librarian getCurrentLibrarian() {
        return currentLibrarian;
    }



    public User loginUser(String email, String password) {
        List<User> users = storage.loadUsers();
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) &&
                    u.getPassword().equals(password)) {

                currentUser = u;
                currentAdmin = null;
                currentLibrarian = null;
                return u;
            }
        }
        return null;
    }

    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }



    public void logout() {
        currentAdmin = null;
        currentLibrarian = null;
        currentUser = null;
    }
}
