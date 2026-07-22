package dao;

import model.Book;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // 1. Add a new book
    public boolean addBook(Book book) {
        String query = "INSERT INTO Books (OwnerID, Title, Author, Genre, BookCondition) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, book.getOwnerId());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setString(4, book.getGenre());
            pstmt.setString(5, book.getCondition());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Get all books by a specific user (owner)
    public List<Book> getBooksByOwner(int ownerId) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM Books WHERE OwnerID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, ownerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("BookID"),
                        rs.getInt("OwnerID"),
                        rs.getString("Title"),
                        rs.getString("Author"),
                        rs.getString("Genre"),
                        rs.getString("BookCondition"),
                        rs.getBoolean("IsAvailable")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    // 3. Search books by Title, Author, or Genre (with wildcard)
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM Books WHERE IsAvailable = TRUE AND (Title LIKE ? OR Author LIKE ? OR Genre LIKE ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("BookID"),
                        rs.getInt("OwnerID"),
                        rs.getString("Title"),
                        rs.getString("Author"),
                        rs.getString("Genre"),
                        rs.getString("BookCondition"),
                        rs.getBoolean("IsAvailable")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    // 4. Get book by ID (for displaying request details)
    public Book getBookById(int bookId) {
        String query = "SELECT * FROM Books WHERE BookID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Book(
                        rs.getInt("BookID"),
                        rs.getInt("OwnerID"),
                        rs.getString("Title"),
                        rs.getString("Author"),
                        rs.getString("Genre"),
                        rs.getString("BookCondition"),
                        rs.getBoolean("IsAvailable")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}