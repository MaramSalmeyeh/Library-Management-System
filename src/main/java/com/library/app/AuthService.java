package com.library.app;

import com.library.domain.model.Admin;
import java.util.Objects;
public class AuthService {
    public boolean login(Admin admin, String username, String passwordPlain) {
        boolean success = Objects.equals(admin.getUsername(), username)
                && Objects.equals(admin.getPasswordPlain(), passwordPlain);
        admin.setLoggedIn(success);
        return success;
    }

    public void logout(Admin admin) {
        admin.setLoggedIn(false);
    }
}
