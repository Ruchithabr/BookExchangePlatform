# 📚 Book Exchange Platform

A comprehensive Java-based desktop and web application designed to facilitate the exchange and swapping of books among users. Built using core Java, JDBC, and MySQL, the platform features a robust DAO architecture, user authentication, wishlist management, and an integrated lightweight web server.

## ✨ Features

- **User Authentication:** Secure login and registration system for users.
- **Book Management:** Users can add new books, view their own listed books, and search through the available inventory.
- **Swap Requests:** Users can request to swap books with others, and manage incoming/outgoing requests.
- **Wishlist Management:** Add desired books to a personal wishlist for future swaps.
- **Dashboard:** A centralized UI for users to view their activity, books, and pending requests.
- **Integrated Web Server:** Includes a lightweight Java web server component to handle HTTP requests for book and user operations.

## 🛠️ Tech Stack

- **Language:** Java (JDK 8+)
- **Database:** MySQL
- **Database Connectivity:** JDBC (MySQL Connector/J)
- **Frontend (Desktop):** Java Swing / AWT (UI Components)
- **Frontend (Web):** Java Built-in `HttpServer` with custom Handlers
- **Architecture:** MVC (Model-View-Controller) & DAO Pattern

## 📂 Project Structure

The project follows a clean, modular architecture:

```text
BookExchangePlatform/
│
├── lib/                     # External JAR dependencies
│   └── mysql-connector-j-9.5.0.jar
│
├── src/                     # Source code root
│   ├── dao/                 # Data Access Objects (Database operations)
│   ├── main/                # Main application entry point
│   ├── model/               # Java Beans / Entity classes
│   ├── ui/                  # Desktop User Interface components
│   ├── utils/               # Utility classes (e.g., DatabaseConnection)
│   └── web/                 # Web server and request handlers
│
├── dao/                     # Compiled .class files for dao
├── main/                    # Compiled .class files for main
├── model/                   # Compiled .class files for model
├── ui/                      # Compiled .class files for ui
├── utils/                   # Compiled .class files for utils
└── web/                     # Compiled .class files for web
```

## ⚙️ How It Works

1. **Database Layer:** The `DatabaseConnection` utility establishes a JDBC connection to the MySQL database.
2. **DAO Layer:** Classes like `BookDAO`, `UserDAO`, and `SwapRequestDAO` handle all SQL queries (CRUD operations) securely.
3. **Model Layer:** Plain Java Objects (POJOs) like `Book`, `User`, and `SwapRequest` represent the data structures.
4. **UI Layer:** The `ui` package provides a desktop interface (e.g., `DashboardUI`, `LoginUI`) for the user to interact with.
5. **Web Layer:** The `WebServer` class and its inner handlers provide an alternative HTTP-based interface for the application.

## 🚀 Installation & Setup

### Prerequisites
- Java Development Kit (JDK) installed.
- MySQL Server installed and running.
- An IDE (Eclipse, IntelliJ IDEA, or VS Code with Java extensions).

### Database Setup
1. Create a new MySQL database (e.g., `book_exchange_db`).
2. Update the database credentials (URL, username, password) in `src/utils/DatabaseConnection.java` to match your local MySQL setup.
3. Execute any required SQL scripts to create the necessary tables (`users`, `books`, `swap_requests`, etc.).

### Running the Application
1. **Clone the repository:**
   ```bash
   git clone https://github.com/Ruchithabr/BookExchangePlatform.git
   cd BookExchangePlatform
   ```
2. **Compile the source code:**
   Ensure the `mysql-connector` jar is in your classpath.
   ```bash
   javac -cp "lib/*" -d . src/**/*.java
   ```
3. **Run the Main Application (Desktop UI):**
   ```bash
   java -cp "lib/*;." main.Main
   ```
   *(Note: Use `:` instead of `;` in the classpath if you are on Mac/Linux).*
4. **Run the Web Server:**
   ```bash
   java -cp "lib/*;." web.WebServer
   ```

## 📫 Connect with Me

- **LinkedIn:** [linkedin.com/in/ruchitha-br](https://linkedin.com/in/ruchitha-br)
- **Email:** ruchitha.b.r.01@gmail.com
- **GitHub:** [github.com/Ruchithabr](https://github.com/Ruchithabr)
