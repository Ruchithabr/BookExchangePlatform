package ui;

import dao.BookDAO;
import model.Book;
import model.User;

import javax.swing.*;
import java.awt.*;

public class AddBookUI extends JDialog {
    private JTextField titleField, authorField, genreField;
    private JComboBox<String> conditionCombo;
    private BookDAO bookDAO = new BookDAO();
    private User user;
    private JFrame parent;

    public AddBookUI(User user, JFrame parent) {
        super(parent, "Add Book", true);
        this.user = user;
        this.parent = parent;
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel titleLabel = new JLabel("📖 Add a New Book");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(15);
        panel.add(titleField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        authorField = new JTextField(15);
        panel.add(authorField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Genre:"), gbc);
        gbc.gridx = 1;
        genreField = new JTextField(15);
        panel.add(genreField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Condition:"), gbc);
        gbc.gridx = 1;
        conditionCombo = new JComboBox<>(new String[]{"New", "Good", "Fair", "Poor"});
        panel.add(conditionCombo, gbc);

        JButton saveBtn = new JButton("Save Book");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> saveBook());
        cancelBtn.addActionListener(e -> dispose());

        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(saveBtn, gbc);
        gbc.gridx = 1;
        panel.add(cancelBtn, gbc);

        add(panel);
        setVisible(true);
    }

    private void saveBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        String genre = genreField.getText();
        String condition = (String) conditionCombo.getSelectedItem();

        if (title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Author are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Book book = new Book(user.getUserId(), title, author, genre, condition);
        if (bookDAO.addBook(book)) {
            JOptionPane.showMessageDialog(this, "✅ Book added successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Failed to add book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}