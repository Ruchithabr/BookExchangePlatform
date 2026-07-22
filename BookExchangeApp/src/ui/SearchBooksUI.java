package ui;

import dao.BookDAO;
import dao.SwapRequestDAO;
import model.Book;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SearchBooksUI extends JFrame {
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private BookDAO bookDAO = new BookDAO();
    private SwapRequestDAO requestDAO = new SwapRequestDAO();
    private User loggedInUser;

    public SearchBooksUI(User user) {
        this.loggedInUser = user;
        setTitle("🔍 Search Books");
        setSize(700, 450);
        setLocationRelativeTo(null);

        // Top panel - Search
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);
        JButton searchBtn = new JButton("Search");
        topPanel.add(searchBtn);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Title", "Author", "Genre", "Condition", "Available"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom - Request button
        JPanel bottomPanel = new JPanel();
        JButton requestBtn = new JButton("📩 Request Selected Book");
        requestBtn.addActionListener(e -> requestBook(table));
        bottomPanel.add(requestBtn);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        bottomPanel.add(closeBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        // Search action
        searchBtn.addActionListener(e -> searchBooks());
        searchField.addActionListener(e -> searchBooks());

        setVisible(true);
    }

    private void searchBooks() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a keyword.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);
        List<Book> results = bookDAO.searchBooks(keyword);

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No books found for: " + keyword, "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Book book : results) {
            tableModel.addRow(new Object[]{
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getGenre(),
                    book.getCondition(),
                    book.isAvailable() ? "✅ Yes" : "❌ No"
            });
        }
    }

    private void requestBook(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);

        // Confirm before requesting
        int confirm = JOptionPane.showConfirmDialog(this, "Request Book ID " + bookId + "?", "Confirm Request", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            requestDAO.sendRequest(bookId, loggedInUser.getUserId());
        }
    }
}