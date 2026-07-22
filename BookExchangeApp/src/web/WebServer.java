package web;

import dao.WishlistDAO;
import model.WishlistItem;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import dao.UserDAO;
import dao.BookDAO;
import dao.SwapRequestDAO;
import model.User;
import model.Book;
import model.SwapRequest;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebServer {

    private static UserDAO userDAO = new UserDAO();
    private static BookDAO bookDAO = new BookDAO();
    private static SwapRequestDAO requestDAO = new SwapRequestDAO();
    private static WishlistDAO wishlistDAO = new WishlistDAO();
    
    // Simple session
    private static Integer loggedInUserId = null; 
    private static String loggedInUserName = null;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // --- ROUTES ---
        server.createContext("/", new HomeHandler());          // NEW: The landing page
        server.createContext("/login", new LoginHandler());    // Login page
        server.createContext("/register", new RegisterHandler());
        server.createContext("/dashboard", new DashboardHandler());
        server.createContext("/addBook", new AddBookHandler());
        server.createContext("/myBooks", new MyBooksHandler());
        server.createContext("/search", new SearchHandler());
        server.createContext("/requestBook", new RequestBookHandler());
        server.createContext("/requests", new RequestsHandler());
        server.createContext("/respondRequest", new RespondRequestHandler());
        server.createContext("/logout", new LogoutHandler());
        server.createContext("/wishlist", new WishlistHandler());           // View wishlist
        server.createContext("/addToWishlist", new AddToWishlistHandler()); // Add to wishlist
        server.createContext("/removeFromWishlist", new RemoveFromWishlistHandler()); // Remove

        server.setExecutor(null);
        server.start();
        System.out.println("🚀 Server started at http://localhost:8080");
        System.out.println("📄 Home page available at http://localhost:8080");
        System.out.println("🔐 Login page available at http://localhost:8080/login");
        System.out.println("⚠️ Press Ctrl+C to stop.");
    }

    // ------------------ LAYOUT ENGINE (Used by all pages) ------------------
    private static String layout(String title, String content) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1'>" +
                "<title>" + title + " - BookExchange</title>" +
                "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>" +
                "<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'>" +
                "<style>" +
                "body { background: #f0f2f5; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }" +
                ".navbar-custom { background: #2d3748 !important; box-shadow: 0 4px 12px rgba(0,0,0,0.15); }" +
                ".card-shadow { border: none; border-radius: 20px; box-shadow: 0 4px 20px rgba(0,0,0,0.04); transition: 0.3s; }" +
                ".card-shadow:hover { transform: translateY(-4px); box-shadow: 0 12px 30px rgba(0,0,0,0.08); }" +
                ".btn-primary { background: #667eea; border: none; border-radius: 50px; padding: 10px 28px; font-weight: 600; }" +
                ".btn-primary:hover { background: #5a67d8; }" +
                ".gradient-text { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }" +
                ".hero-section { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 80px 0; border-radius: 0 0 50% 50% / 20px; margin-bottom: 40px; }" +
                ".feature-icon { font-size: 40px; color: #667eea; }" +
                "</style></head><body>" +
                // NAVBAR
                "<nav class='navbar navbar-expand-lg navbar-custom'>" +
                "<div class='container'><span class='navbar-brand text-white'><i class='fas fa-book-open me-2'></i>BookExchange</span>" +
                "<div class='navbar-nav ms-auto'>" +
                (loggedInUserId != null ? 
                        "<span class='nav-link text-light'><i class='fas fa-user me-1'></i> " + loggedInUserName + "</span>" +
                        "<a href='/logout' class='btn btn-outline-light btn-sm'>Logout</a>" 
                        : 
                        "<a href='/login' class='btn btn-outline-light btn-sm'>Login</a>") +
                "</div></div></nav>" +
                // CONTENT
                "<div class='container mt-4'>" + content + "</div>" +
                "<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>" +
                "</body></html>";
    }

    private static void redirect(HttpExchange exchange, String url) throws IOException {
        exchange.getResponseHeaders().set("Location", url);
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    // ------------------ HOME PAGE HANDLER (NEW!) ------------------
    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // If user is already logged in, redirect to dashboard
            if (loggedInUserId != null) {
                redirect(exchange, "/dashboard");
                return;
            }
            // Show the beautiful landing page
            String content = getLandingPage();
            sendResponse(exchange, layout("Home", content));
        }
    }

    private static String getLandingPage() {
        return 
            // Hero Section
            "<div class='hero-section text-center'>" +
            "<div class='container'><h1 class='display-3 fw-bold'>📚 Discover & Exchange Books</h1>" +
            "<p class='lead mt-3'>Join our community to share your favorite books with others. <br>Find your next great read today!</p>" +
            "<a href='/login' class='btn btn-light btn-lg mt-3 rounded-pill px-5'><i class='fas fa-rocket me-2'></i>Get Started</a>" +
            "</div></div>" +
            // Features Grid
            "<div class='row g-4 mt-4'>" +
            "<div class='col-md-4'><div class='card card-shadow p-4 text-center'>" +
            "<i class='fas fa-plus-circle feature-icon'></i><h5 class='mt-3'>Add Books</h5>" +
            "<p class='text-muted'>List the books you own and want to share with others.</p></div></div>" +
            "<div class='col-md-4'><div class='card card-shadow p-4 text-center'>" +
            "<i class='fas fa-search feature-icon'></i><h5 class='mt-3'>Search & Request</h5>" +
            "<p class='text-muted'>Find books by title, author, or genre and request them instantly.</p></div></div>" +
            "<div class='col-md-4'><div class='card card-shadow p-4 text-center'>" +
            "<i class='fas fa-handshake feature-icon'></i><h5 class='mt-3'>Exchange Smoothly</h5>" +
            "<p class='text-muted'>Accept or reject requests and manage your swaps easily.</p></div></div>" +
            "</div>";
    }

    // ------------------ LOGIN HANDLER (URL: /login) ------------------
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);
                User user = userDAO.loginUser(params.get("email"), params.get("password"));
                if (user != null) {
                    loggedInUserId = user.getUserId();
                    loggedInUserName = user.getName();
                    redirect(exchange, "/dashboard");
                } else {
                    String content = getLoginForm("❌ Invalid credentials");
                    sendResponse(exchange, layout("Login", content));
                }
                return;
            }
            // GET Request
            if (loggedInUserId != null) {
                redirect(exchange, "/dashboard");
                return;
            }
            String content = getLoginForm(null);
            sendResponse(exchange, layout("Login", content));
        }
    }

    private static String getLoginForm(String error) {
        String err = error == null ? "" : "<div class='alert alert-danger'>" + error + "</div>";
        return "<div class='row justify-content-center mt-5'><div class='col-md-5'>" +
                "<div class='card card-shadow p-4'><div class='text-center mb-3'><i class='fas fa-book-open' style='font-size:48px;color:#667eea;'></i>" +
                "<h2 class='fw-bold'>Welcome Back</h2><p class='text-muted'>Login to exchange your books</p></div>" +
                err +
                "<form action='/login' method='post'>" +
                "<div class='mb-3'><input type='email' name='email' class='form-control' placeholder='Email' required></div>" +
                "<div class='mb-3'><input type='password' name='password' class='form-control' placeholder='Password' required></div>" +
                "<button type='submit' class='btn btn-primary w-100'>Login</button></form>" +
                "<div class='text-center mt-3'><a href='/register'>Create an Account</a></div></div></div></div>";
    }

    // ------------------ REGISTER HANDLER ------------------
    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);
                User newUser = new User(params.get("name"), params.get("email"), params.get("password"), params.get("location"));
                if (userDAO.registerUser(newUser)) {
                    String content = "<div class='alert alert-success'>✅ Registration successful! <a href='/login'>Login now</a></div>";
                    sendResponse(exchange, layout("Register", content));
                } else {
                    String content = getRegisterForm("❌ Email already exists.");
                    sendResponse(exchange, layout("Register", content));
                }
                return;
            }
            String content = getRegisterForm(null);
            sendResponse(exchange, layout("Register", content));
        }
    }

    private static String getRegisterForm(String error) {
        String err = error == null ? "" : "<div class='alert alert-danger'>" + error + "</div>";
        return "<div class='row justify-content-center mt-4'><div class='col-md-5'>" +
                "<div class='card card-shadow p-4'><h3 class='text-center fw-bold'>Create Account</h3>" +
                err +
                "<form action='/register' method='post'>" +
                "<div class='mb-2'><input type='text' name='name' class='form-control' placeholder='Full Name' required></div>" +
                "<div class='mb-2'><input type='email' name='email' class='form-control' placeholder='Email' required></div>" +
                "<div class='mb-2'><input type='password' name='password' class='form-control' placeholder='Password' required></div>" +
                "<div class='mb-3'><input type='text' name='location' class='form-control' placeholder='City'></div>" +
                "<button type='submit' class='btn btn-success w-100'>Register</button></form>" +
                "<div class='text-center mt-3'><a href='/login'>Back to Login</a></div></div></div></div>";
    }

    // ------------------ DASHBOARD HANDLER ------------------
    static class DashboardHandler implements HttpHandler {
        @Override
public void handle(HttpExchange exchange) throws IOException {
    if (loggedInUserId == null) { redirect(exchange, "/"); return; }
    
    List<Book> myBooks = bookDAO.getBooksByOwner(loggedInUserId);
    long availableCount = myBooks.stream().filter(Book::isAvailable).count();

    String content = 
        "<div class='row mb-4'><div class='col'><h2 class='fw-bold'>Dashboard</h2></div></div>" +
        "<div class='row g-4'>" +
        // Stats Cards
        "<div class='col-md-3'><div class='card card-shadow p-3 text-center'><h4><i class='fas fa-book' style='color:#667eea;'></i> " + myBooks.size() + "</h4><p class='text-muted'>My Books</p></div></div>" +
        "<div class='col-md-3'><div class='card card-shadow p-3 text-center'><h4><i class='fas fa-check-circle' style='color:#48bb78;'></i> " + availableCount + "</h4><p class='text-muted'>Available</p></div></div>" +
        "<div class='col-md-3'><div class='card card-shadow p-3 text-center'><h4><i class='fas fa-exchange-alt' style='color:#f6ad55;'></i> " + requestDAO.getOutgoingRequests(loggedInUserId).size() + "</h4><p class='text-muted'>My Requests</p></div></div>" +
        "<div class='col-md-3'><div class='card card-shadow p-3 text-center'><h4><i class='fas fa-inbox' style='color:#fc8181;'></i> " + requestDAO.getIncomingRequests(loggedInUserId).size() + "</h4><p class='text-muted'>Incoming</p></div></div>" +
        "</div>" +
        // Action Grid (UPDATED WITH WISHLIST)
        "<div class='row g-4 mt-2'>" +
        "<div class='col-md-3'><a href='/addBook' class='text-decoration-none'><div class='card card-shadow p-4 text-center'><i class='fas fa-plus-circle' style='font-size:32px;color:#667eea;'></i><h5 class='mt-2'>Add Book</h5></div></a></div>" +
        "<div class='col-md-3'><a href='/myBooks' class='text-decoration-none'><div class='card card-shadow p-4 text-center'><i class='fas fa-list' style='font-size:32px;color:#48bb78;'></i><h5 class='mt-2'>My Books</h5></div></a></div>" +
        "<div class='col-md-3'><a href='/search' class='text-decoration-none'><div class='card card-shadow p-4 text-center'><i class='fas fa-search' style='font-size:32px;color:#4299e1;'></i><h5 class='mt-2'>Search</h5></div></a></div>" +
        "<div class='col-md-3'><a href='/wishlist' class='text-decoration-none'><div class='card card-shadow p-4 text-center'><i class='fas fa-heart' style='font-size:32px;color:#fc8181;'></i><h5 class='mt-2'>Wishlist</h5></div></a></div>" +
        "<div class='col-md-3'><a href='/requests' class='text-decoration-none'><div class='card card-shadow p-4 text-center'><i class='fas fa-envelope' style='font-size:32px;color:#f6ad55;'></i><h5 class='mt-2'>Requests</h5></div></a></div>" +
        "</div>";
    sendResponse(exchange, layout("Dashboard", content));
}
    }

    // ------------------ ADD BOOK HANDLER ------------------
    static class AddBookHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (loggedInUserId == null) { redirect(exchange, "/"); return; }

            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);
                Book book = new Book(loggedInUserId, params.get("title"), params.get("author"), params.get("genre"), params.get("condition"));
                if (bookDAO.addBook(book)) {
                    String content = "<div class='alert alert-success'>✅ Book added successfully! <a href='/dashboard'>Go Home</a></div>";
                    sendResponse(exchange, layout("Add Book", content));
                } else {
                    String content = getAddBookForm("❌ Failed to add book.");
                    sendResponse(exchange, layout("Add Book", content));
                }
                return;
            }
            sendResponse(exchange, layout("Add Book", getAddBookForm(null)));
        }
    }

    private static String getAddBookForm(String error) {
        String err = error == null ? "" : "<div class='alert alert-danger'>" + error + "</div>";
        return "<div class='row justify-content-center'><div class='col-md-6'>" +
                "<div class='card card-shadow p-4'><h3 class='fw-bold'><i class='fas fa-plus-circle' style='color:#667eea;'></i> Add Book</h3>" +
                err +
                "<form action='/addBook' method='post'>" +
                "<div class='mb-3'><input type='text' name='title' class='form-control' placeholder='Title' required></div>" +
                "<div class='mb-3'><input type='text' name='author' class='form-control' placeholder='Author' required></div>" +
                "<div class='mb-3'><input type='text' name='genre' class='form-control' placeholder='Genre'></div>" +
                "<div class='mb-3'><select name='condition' class='form-select'><option value='New'>New</option><option value='Good' selected>Good</option><option value='Fair'>Fair</option><option value='Poor'>Poor</option></select></div>" +
                "<button type='submit' class='btn btn-primary w-100'>Save Book</button></form>" +
                "<div class='text-center mt-3'><a href='/dashboard'>Back to Dashboard</a></div></div></div></div>";
    }

    // ------------------ MY BOOKS HANDLER ------------------
    static class MyBooksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (loggedInUserId == null) { redirect(exchange, "/"); return; }
            
            List<Book> books = bookDAO.getBooksByOwner(loggedInUserId);
            StringBuilder table = new StringBuilder();
            table.append("<div class='d-flex justify-content-between align-items-center mb-3'><h2 class='fw-bold'>My Books</h2>");
            table.append("<a href='/dashboard' class='btn btn-secondary btn-sm'>Back</a></div>");

            if (books.isEmpty()) {
                table.append("<div class='alert alert-info'>You haven't added any books yet. <a href='/addBook'>Add one now</a></div>");
            } else {
                table.append("<div class='table-responsive'><table class='table table-hover bg-white rounded shadow-sm'>");
                table.append("<thead style='background:#667eea;color:white;'><tr><th>#</th><th>Title</th><th>Author</th><th>Condition</th><th>Status</th></tr></thead><tbody>");
                int i=1;
                for (Book b : books) {
                    String status = b.isAvailable() ? "<span class='badge bg-success'>Available</span>" : "<span class='badge bg-warning'>Requested</span>";
                    table.append("<tr><td>").append(i++).append("</td><td><strong>").append(b.getTitle()).append("</strong></td>");
                    table.append("<td>").append(b.getAuthor()).append("</td><td>").append(b.getCondition()).append("</td><td>").append(status).append("</td></tr>");
                }
                table.append("</tbody></table></div>");
            }
            sendResponse(exchange, layout("My Books", table.toString()));
        }
    }

    // ------------------ SEARCH HANDLER ------------------
        // ------------------ SEARCH HANDLER ------------------
    static class SearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (loggedInUserId == null) { 
                redirect(exchange, "/"); 
                return; 
            }

            String keyword = getQueryParam(exchange, "keyword");
            List<Book> results = (keyword != null && !keyword.isEmpty()) ? bookDAO.searchBooks(keyword) : null;

            StringBuilder html = new StringBuilder();
            html.append("<div class='d-flex justify-content-between align-items-center mb-3'>");
            html.append("<h2 class='fw-bold'><i class='fas fa-search' style='color:#4299e1;'></i> Search Books</h2>");
            html.append("<a href='/dashboard' class='btn btn-secondary btn-sm'>Back</a></div>");
            
            // Search Form
            html.append("<form action='/search' method='get' class='mb-4'>");
            html.append("<div class='input-group'>");
            html.append("<input type='text' name='keyword' class='form-control' placeholder='Title, Author, or Genre' value='").append(keyword != null ? keyword : "").append("'>");
            html.append("<button type='submit' class='btn btn-primary'><i class='fas fa-search'></i> Search</button>");
            html.append("</div></form>");

            if (keyword != null) {
                if (results == null || results.isEmpty()) {
                    html.append("<div class='alert alert-warning'>No books found for '<strong>").append(keyword).append("</strong>'</div>");
                } else {
                    html.append("<p class='text-muted'>Found ").append(results.size()).append(" results</p>");
                    html.append("<div class='table-responsive'>");
                    html.append("<table class='table table-hover bg-white rounded shadow-sm'>");
                    html.append("<thead style='background:#667eea;color:white;'>");
                    html.append("<tr><th>Title</th><th>Author</th><th>Condition</th><th>Status</th><th>Action</th></tr>");
                    html.append("</thead><tbody>");
                    
                    for (Book b : results) {
                        boolean isAvailable = b.isAvailable() && b.getOwnerId() != loggedInUserId;
                        
                        html.append("<tr>");
                        html.append("<td><strong>").append(b.getTitle()).append("</strong></td>");
                        html.append("<td>").append(b.getAuthor()).append("</td>");
                        html.append("<td>").append(b.getCondition()).append("</td>");
                        html.append("<td>").append(b.isAvailable() ? "✅ Available" : "❌ Unavailable").append("</td>");
                        
                        html.append("<td>");
                        if (isAvailable) {
                            // 1. Request Button
                            html.append("<form action='/requestBook' method='post' style='display:inline;'>");
                            html.append("<input type='hidden' name='bookId' value='").append(b.getBookId()).append("'>");
                            html.append("<button type='submit' class='btn btn-sm btn-success me-1'>Request</button></form>");
                            
                            // 2. Wishlist Heart Button (NEW!)
                            html.append("<form action='/addToWishlist' method='post' style='display:inline;'>");
                            html.append("<input type='hidden' name='title' value='").append(b.getTitle()).append("'>");
                            html.append("<input type='hidden' name='author' value='").append(b.getAuthor()).append("'>");
                            html.append("<input type='hidden' name='genre' value='").append(b.getGenre()).append("'>");
                            html.append("<button type='submit' class='btn btn-sm btn-outline-danger' title='Add to Wishlist'><i class='fas fa-heart'></i></button></form>");
                            
                        } else if (b.getOwnerId() == loggedInUserId) {
                            html.append("<span class='badge bg-secondary'>Your book</span>");
                        } else {
                            html.append("<span class='text-muted'>Unavailable</span>");
                        }
                        html.append("</td>");
                        html.append("</tr>");
                    }
                    html.append("</tbody></table></div>");
                }
            }
            sendResponse(exchange, layout("Search", html.toString()));
        }
    }

    // ------------------ REQUEST BOOK HANDLER ------------------
    static class RequestBookHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (loggedInUserId == null) { redirect(exchange, "/"); return; }
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);
                int bookId = Integer.parseInt(params.get("bookId"));
                boolean success = requestDAO.sendRequest(bookId, loggedInUserId);
                String msg = success ? "✅ Request sent successfully!" : "❌ Failed to send request (maybe already requested or your own book).";
                String content = "<div class='alert alert-" + (success ? "success" : "danger") + "'>" + msg + " <a href='/search'>Back to Search</a></div>";
                sendResponse(exchange, layout("Request", content));
            } else {
                redirect(exchange, "/search");
            }
        }
    }

    // ------------------ REQUESTS HANDLER ------------------
    static class RequestsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (loggedInUserId == null) { redirect(exchange, "/"); return; }

            List<SwapRequest> incoming = requestDAO.getIncomingRequests(loggedInUserId);
            List<SwapRequest> outgoing = requestDAO.getOutgoingRequests(loggedInUserId);

            StringBuilder html = new StringBuilder();
            html.append("<h2 class='fw-bold mb-4'>Requests</h2>");
            html.append("<ul class='nav nav-tabs' id='myTab' role='tablist'>");
            html.append("<li class='nav-item'><button class='nav-link active' id='incoming-tab' data-bs-toggle='tab' data-bs-target='#incoming'>Incoming (").append(incoming.size()).append(")</button></li>");
            html.append("<li class='nav-item'><button class='nav-link' id='outgoing-tab' data-bs-toggle='tab' data-bs-target='#outgoing'>Outgoing (").append(outgoing.size()).append(")</button></li>");
            html.append("</ul><div class='tab-content mt-3'><div class='tab-pane fade show active' id='incoming'>");

            // Incoming Table
            if (incoming.isEmpty()) {
                html.append("<div class='alert alert-light'>No incoming requests.</div>");
            } else {
                html.append("<div class='table-responsive'><table class='table table-hover bg-white'><thead><tr><th>Book</th><th>Requester</th><th>Status</th><th>Action</th></tr></thead><tbody>");
                for (SwapRequest req : incoming) {
                    Book b = bookDAO.getBookById(req.getBookId());
                    String bookTitle = b != null ? b.getTitle() : "Unknown";
                    html.append("<tr><td>").append(bookTitle).append("</td><td>User #").append(req.getRequesterId()).append("</td>");
                    html.append("<td><span class='badge bg-warning'>").append(req.getStatus()).append("</span></td>");
                    if ("Pending".equals(req.getStatus())) {
                        html.append("<td><form action='/respondRequest' method='post' style='display:inline;'><input type='hidden' name='requestId' value='").append(req.getRequestId()).append("'>");
                        html.append("<button type='submit' name='action' value='accept' class='btn btn-sm btn-success me-1'>Accept</button>");
                        html.append("<button type='submit' name='action' value='reject' class='btn btn-sm btn-danger'>Reject</button></form></td>");
                    } else {
                        html.append("<td><span class='text-muted'>Processed</span></td>");
                    }
                    html.append("</tr>");
                }
                html.append("</tbody></table></div>");
            }
            html.append("</div><div class='tab-pane fade' id='outgoing'>");

            // Outgoing Table
            if (outgoing.isEmpty()) {
                html.append("<div class='alert alert-light'>No outgoing requests.</div>");
            } else {
                html.append("<div class='table-responsive'><table class='table table-hover bg-white'><thead><tr><th>Book</th><th>Owner</th><th>Status</th></tr></thead><tbody>");
                for (SwapRequest req : outgoing) {
                    Book b = bookDAO.getBookById(req.getBookId());
                    String bookTitle = b != null ? b.getTitle() : "Unknown";
                    String badgeClass = "bg-warning";
                    if ("Accepted".equals(req.getStatus())) badgeClass = "bg-success";
                    else if ("Rejected".equals(req.getStatus())) badgeClass = "bg-danger";
                    html.append("<tr><td>").append(bookTitle).append("</td><td>User #").append(req.getOwnerId()).append("</td>");
                    html.append("<td><span class='badge ").append(badgeClass).append("'>").append(req.getStatus()).append("</span></td></tr>");
                }
                html.append("</tbody></table></div>");
            }
            html.append("</div></div><div class='mt-3'><a href='/dashboard' class='btn btn-secondary btn-sm'>Back</a></div>");

            sendResponse(exchange, layout("Requests", html.toString()));
        }
    }

    // ------------------ RESPOND REQUEST HANDLER ------------------
    static class RespondRequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (loggedInUserId == null) { redirect(exchange, "/"); return; }
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);
                int requestId = Integer.parseInt(params.get("requestId"));
                String action = params.get("action");
                if ("accept".equals(action)) requestDAO.acceptRequest(requestId);
                else if ("reject".equals(action)) requestDAO.rejectRequest(requestId);
                redirect(exchange, "/requests");
            } else {
                redirect(exchange, "/requests");
            }
        }
    }

    // ------------------ LOGOUT HANDLER ------------------
    static class LogoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            loggedInUserId = null;
            loggedInUserName = null;
            redirect(exchange, "/");
        }
    }

// ------------------ WISHLIST HANDLER ------------------
static class WishlistHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (loggedInUserId == null) { redirect(exchange, "/"); return; }

        List<WishlistItem> items = wishlistDAO.getWishlistByUser(loggedInUserId);
        StringBuilder html = new StringBuilder();
        html.append("<div class='d-flex justify-content-between align-items-center mb-3'><h2 class='fw-bold'><i class='fas fa-heart' style='color:#fc8181;'></i> My Wishlist</h2>");
        html.append("<a href='/dashboard' class='btn btn-secondary btn-sm'>Back</a></div>");

        if (items.isEmpty()) {
            html.append("<div class='alert alert-info'>Your wishlist is empty. <a href='/search'>Search for books</a> to add them!</div>");
        } else {
            html.append("<div class='table-responsive'><table class='table table-hover bg-white rounded shadow-sm'>");
            html.append("<thead style='background:#fc8181;color:white;'><tr><th>#</th><th>Title</th><th>Author</th><th>Genre</th><th>Action</th></tr></thead><tbody>");
            int i = 1;
            for (WishlistItem item : items) {
                html.append("<tr><td>").append(i++).append("</td>");
                html.append("<td><strong>").append(item.getTitle()).append("</strong></td>");
                html.append("<td>").append(item.getAuthor()).append("</td>");
                html.append("<td>").append(item.getGenre()).append("</td>");
                html.append("<td><form action='/removeFromWishlist' method='post' style='display:inline;'>");
                html.append("<input type='hidden' name='wishlistId' value='").append(item.getWishlistId()).append("'>");
                html.append("<button type='submit' class='btn btn-sm btn-danger'><i class='fas fa-trash'></i> Remove</button></form></td>");
                html.append("</tr>");
            }
            html.append("</tbody></table></div>");
        }
        sendResponse(exchange, layout("Wishlist", html.toString()));
    }
}

// ------------------ ADD TO WISHLIST HANDLER ------------------
static class AddToWishlistHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (loggedInUserId == null) { redirect(exchange, "/"); return; }

        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> params = parseFormData(exchange);
            String title = params.get("title");
            String author = params.get("author");
            String genre = params.get("genre");

            if (title == null || title.isEmpty()) {
                String content = "<div class='alert alert-danger'>❌ Book title is required.</div>";
                sendResponse(exchange, layout("Add to Wishlist", content));
                return;
            }

            if (wishlistDAO.existsInWishlist(loggedInUserId, title, author)) {
                String content = "<div class='alert alert-warning'>⚠️ This book is already in your wishlist! <a href='/wishlist'>View Wishlist</a></div>";
                sendResponse(exchange, layout("Add to Wishlist", content));
                return;
            }

            boolean success = wishlistDAO.addToWishlist(loggedInUserId, title, author, genre);
            String msg = success ? "✅ Book added to your wishlist!" : "❌ Failed to add to wishlist.";
            String content = "<div class='alert alert-" + (success ? "success" : "danger") + "'>" + msg + " <a href='/search'>Back to Search</a> | <a href='/wishlist'>View Wishlist</a></div>";
            sendResponse(exchange, layout("Add to Wishlist", content));
        } else {
            redirect(exchange, "/search");
        }
    }
}

// ------------------ REMOVE FROM WISHLIST HANDLER ------------------
static class RemoveFromWishlistHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (loggedInUserId == null) { redirect(exchange, "/"); return; }

        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> params = parseFormData(exchange);
            int wishlistId = Integer.parseInt(params.get("wishlistId"));
            wishlistDAO.removeFromWishlist(wishlistId);
            redirect(exchange, "/wishlist");
        } else {
            redirect(exchange, "/wishlist");
        }
    }
}

    // ------------------ UTILITY METHODS ------------------
    private static Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            }
        }
        return result;
    }

    private static String getQueryParam(HttpExchange exchange, String key) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return null;
        try {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1 && pair[0].equals(key)) {
                    return URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                }
            }
        } catch (Exception e) { /* ignore */ }
        return null;
    }

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}