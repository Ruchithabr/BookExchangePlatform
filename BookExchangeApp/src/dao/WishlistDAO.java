package dao;

import model.WishlistItem;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {

    // Add book to wishlist
    public boolean addToWishlist(int userId, String title, String author, String genre) {
        String query = "INSERT INTO Wishlist (UserID, Title, Author, Genre) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.setString(4, genre);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all wishlist items for a user
    public List<WishlistItem> getWishlistByUser(int userId) {
        List<WishlistItem> items = new ArrayList<>();
        String query = "SELECT * FROM Wishlist WHERE UserID = ? ORDER BY AddedDate DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                WishlistItem item = new WishlistItem(
                    rs.getInt("WishlistID"),
                    rs.getInt("UserID"),
                    rs.getString("Title"),
                    rs.getString("Author"),
                    rs.getString("Genre")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    // Remove a book from wishlist by ID
    public boolean removeFromWishlist(int wishlistId) {
        String query = "DELETE FROM Wishlist WHERE WishlistID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, wishlistId);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if a book already exists in wishlist (prevent duplicates)
    public boolean existsInWishlist(int userId, String title, String author) {
        String query = "SELECT COUNT(*) FROM Wishlist WHERE UserID = ? AND Title = ? AND Author = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}