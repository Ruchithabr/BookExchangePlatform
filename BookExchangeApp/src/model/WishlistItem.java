package model;

public class WishlistItem {
    private int wishlistId;
    private int userId;
    private String title;
    private String author;
    private String genre;

    public WishlistItem(int wishlistId, int userId, String title, String author, String genre) {
        this.wishlistId = wishlistId;
        this.userId = userId;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public WishlistItem(int userId, String title, String author, String genre) {
        this.userId = userId;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    // Getters
    public int getWishlistId() { return wishlistId; }
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }

    // Setters
    public void setWishlistId(int wishlistId) { this.wishlistId = wishlistId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setGenre(String genre) { this.genre = genre; }
}