package software.library;

import java.util.List;

public class User {
	  public User(String id, String name) {}

	    public String getId() { return null; }
	    public String getName() { return null; }

	    
	    public boolean borrow(Book book) { return false; }
	    public boolean returnBook(Book book) { return false; }
	    public boolean hasBorrowed(String isbn) { return false; }
	    public List<Book> getBorrowed() { return java.util.List.of(); }
	    public int countBorrowed() { return 0; }
}
