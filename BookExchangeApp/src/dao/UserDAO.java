package dao;

import model.User;
import utils.DatabaseConnection;
import java.sql.*;

public class UserDAO {

    public boolean registerUser(User user) {
        String query = "INSERT INTO Users (Name, Email, Password, Location) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getLocation());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("❌ Error: This email is already registered!");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public User loginUser(String email, String password) {
        String query = "SELECT * FROM Users WHERE Email = ? AND Password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("UserID"),
                    rs.getString("Name"),
                    rs.getString("Email"),
                    rs.getString("Password"),
                    rs.getString("Location")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}