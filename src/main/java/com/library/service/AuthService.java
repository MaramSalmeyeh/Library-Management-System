package com.library.service;

import com.library.domain.Admin;
import com.library.domain.FileStorage;

import java.util.List;

public class AuthService {

    private final FileStorage storage;
    private Admin currentAdmin;

    public AuthService(FileStorage storage) {
        this.storage = storage;
    }


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


    public void logout() {
        currentAdmin = null;
    }


    public boolean isAdminLoggedIn() {
        return currentAdmin != null;
    }

    public Admin getCurrentAdmin() {
        return currentAdmin;
    }
}
