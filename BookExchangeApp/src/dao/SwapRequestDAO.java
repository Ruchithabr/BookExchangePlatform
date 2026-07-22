package dao;

import model.SwapRequest;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SwapRequestDAO {

    // 1. Send a request for a book
    public boolean sendRequest(int bookId, int requesterId) {
        // First, check if the book is available
        String checkQuery = "SELECT OwnerID, IsAvailable FROM Books WHERE BookID = ?";
        String insertQuery = "INSERT INTO SwapRequests (BookID, RequesterID, OwnerID, Status) VALUES (?, ?, ?, 'Pending')";
        String updateBookQuery = "UPDATE Books SET IsAvailable = FALSE WHERE BookID = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Step 1: Check if book exists and is available
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Book not found!");
                return false;
            }

            if (!rs.getBoolean("IsAvailable")) {
                System.out.println("❌ Book is already requested or not available.");
                return false;
            }

            int ownerId = rs.getInt("OwnerID");

            // Prevent requesting your own book
            if (ownerId == requesterId) {
                System.out.println("❌ You cannot request your own book!");
                return false;
            }

            // Step 2: Insert the request
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, bookId);
            insertStmt.setInt(2, requesterId);
            insertStmt.setInt(3, ownerId);
            int rowsInserted = insertStmt.executeUpdate();

            if (rowsInserted > 0) {
                // Step 3: Mark book as unavailable
                PreparedStatement updateStmt = conn.prepareStatement(updateBookQuery);
                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();
                System.out.println("✅ Request sent successfully!");
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. Get incoming requests (books requested from me)
    public List<SwapRequest> getIncomingRequests(int ownerId) {
        List<SwapRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM SwapRequests WHERE OwnerID = ? AND Status = 'Pending'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, ownerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                requests.add(new SwapRequest(
                        rs.getInt("RequestID"),
                        rs.getInt("BookID"),
                        rs.getInt("RequesterID"),
                        rs.getInt("OwnerID"),
                        rs.getString("Status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    // 3. Get outgoing requests (books I requested)
    public List<SwapRequest> getOutgoingRequests(int requesterId) {
        List<SwapRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM SwapRequests WHERE RequesterID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, requesterId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                requests.add(new SwapRequest(
                        rs.getInt("RequestID"),
                        rs.getInt("BookID"),
                        rs.getInt("RequesterID"),
                        rs.getInt("OwnerID"),
                        rs.getString("Status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    // 4. Accept a request
    public boolean acceptRequest(int requestId) {
        String updateQuery = "UPDATE SwapRequests SET Status = 'Accepted' WHERE RequestID = ? AND Status = 'Pending'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setInt(1, requestId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Request accepted! Contact the user to exchange.");
                return true;
            } else {
                System.out.println("❌ Request not found or already processed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 5. Reject a request (Also makes the book available again)
    public boolean rejectRequest(int requestId) {
        // First get the BookID to make it available again
        String getBookQuery = "SELECT BookID FROM SwapRequests WHERE RequestID = ?";
        String updateRequestQuery = "UPDATE SwapRequests SET Status = 'Rejected' WHERE RequestID = ? AND Status = 'Pending'";
        String updateBookQuery = "UPDATE Books SET IsAvailable = TRUE WHERE BookID = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get Book ID
            PreparedStatement getStmt = conn.prepareStatement(getBookQuery);
            getStmt.setInt(1, requestId);
            ResultSet rs = getStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Request not found.");
                return false;
            }
            int bookId = rs.getInt("BookID");

            // Update request status
            PreparedStatement updateReqStmt = conn.prepareStatement(updateRequestQuery);
            updateReqStmt.setInt(1, requestId);
            int rowsUpdated = updateReqStmt.executeUpdate();

            if (rowsUpdated > 0) {
                // Make book available again
                PreparedStatement updateBookStmt = conn.prepareStatement(updateBookQuery);
                updateBookStmt.setInt(1, bookId);
                updateBookStmt.executeUpdate();
                System.out.println("✅ Request rejected. Book is available again.");
                return true;
            } else {
                System.out.println("❌ Request not found or already processed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}