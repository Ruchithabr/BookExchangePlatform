package model;

public class SwapRequest {
    private int requestId;
    private int bookId;
    private int requesterId;
    private int ownerId;
    private String status; // Pending, Accepted, Rejected, Completed

    // Constructor for fetching from DB
    public SwapRequest(int requestId, int bookId, int requesterId, int ownerId, String status) {
        this.requestId = requestId;
        this.bookId = bookId;
        this.requesterId = requesterId;
        this.ownerId = ownerId;
        this.status = status;
    }

    // Getters and Setters
    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getRequesterId() { return requesterId; }
    public void setRequesterId(int requesterId) { this.requesterId = requesterId; }
    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Request #" + requestId + " | Book ID: " + bookId + " | Status: " + status;
    }
}