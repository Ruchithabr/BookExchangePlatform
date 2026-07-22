package ui;

import dao.BookDAO;
import model.Book;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyBooksUI extends JFrame {
    private DefaultTableModel tableModel;

    public MyBooksUI(User user) {
        setTitle("📚 My Books");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(true);

        // Table columns
        String[] columns = {"ID", "Title", "Author", "Genre", "Condition", "Available"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Load data
        BookDAO bookDAO = new BookDAO();
        List<Book> books = bookDAO.getBooksByOwner(user.getUserId());

        for (Book book : books) {
            tableModel.addRow(new Object[]{
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getGenre(),
                    book.getCondition(),
                    book.isAvailable() ? "✅ Yes" : "❌ No"
            });
        }

        add(scrollPane, BorderLayout.CENTER);

        // Close button
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(700, 400);
        setVisible(true);
    }
}