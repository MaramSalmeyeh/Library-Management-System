package software.library;

public class Book {
	 public Book(String title, String author, String isbn) {}
	    public String getTitle() { return null; }
	    public String getAuthor() { return null; }
	    public String getIsbn() { return null; }
	    public boolean isAvailable() { return true; }
	    boolean matches(String q) { return false; }
	    void markBorrowed() {}
	    void markReturned() {}
}
