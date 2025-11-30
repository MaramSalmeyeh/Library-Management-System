package com.library.service;

import com.library.domain.FileStorage;
import com.library.domain.User;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {

    private final FileStorage storage;
    private User currentUser;


    public UserService(FileStorage storage, EmailService emailService) {
        this.storage = storage;

    }

    public UserService(FileStorage storage) {
        this.storage = storage;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isUserLoggedIn() {
        return currentUser != null;
    }


    public User register(String name, String email, String password) {
        List<User> users = storage.loadUsers();

        boolean exists = users.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
        if (exists) {
            throw new IllegalArgumentException("Email already registered");
        }

        String id ="U" + (users.size() + 1);

        User user = new User(id, name, email, password);
        users.add(user);
        storage.saveUsers(users);
        return user;
    }

    // تسجيل دخول
    public User login(String email, String password) {
        List<User> users = storage.loadUsers();

        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) &&
                    u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }


    public void logout() {
        currentUser = null;
    }

    public User findById(String userId) {
        return storage.loadUsers()
                .stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

}
