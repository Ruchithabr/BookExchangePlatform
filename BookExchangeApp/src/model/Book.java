package model;

public class Book {
    private int bookId;
    private int ownerId;
    private String title;
    private String author;
    private String genre;
    private String condition;
    private boolean isAvailable;

    // Constructor for adding a new book (without bookId)
    public Book(int ownerId, String title, String author, String genre, String condition) {
        this.ownerId = ownerId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.condition = condition;
        this.isAvailable = true; // Default: available
    }

    // Constructor for fetching from DB (with bookId)
    public Book(int bookId, int ownerId, String title, String author, String genre, String condition, boolean isAvailable) {
        this.bookId = bookId;
        this.ownerId = ownerId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.condition = condition;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    @Override
    public String toString() {
        return "ID: " + bookId + " | " + title + " by " + author + " | " + genre + " | " + condition + " | " + (isAvailable ? "Available" : "Not Available");
    }
}