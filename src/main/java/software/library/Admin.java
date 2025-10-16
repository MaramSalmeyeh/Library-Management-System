package software.library;

import java.util.List;

public class Admin {
	public Admin(String username, String passwordPlain) {}

    public boolean login(String username, String passwordPlain) {
        return false; 
    }

    public void logout() {}

    public boolean isLoggedIn() {
        return false;
    }

    public boolean addBook(List catalog, Book book) {
        return false;  
    }
}
