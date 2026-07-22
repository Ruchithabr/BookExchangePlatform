package ui;

import model.User;

import javax.swing.*;
import java.awt.*;

public class DashboardUI extends JFrame {
    private User loggedInUser;

    public DashboardUI(User user) {
        this.loggedInUser = user;
        setTitle("Book Exchange - Dashboard");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Welcome message
        JLabel welcomeLabel = new JLabel("👋 Welcome, " + user.getName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(welcomeLabel, gbc);

        // Buttons
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JButton addBookBtn = createButton("📖 Add Book");
        panel.add(addBookBtn, gbc);
        gbc.gridx = 1;
        JButton myBooksBtn = createButton("📚 My Books");
        panel.add(myBooksBtn, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        JButton searchBtn = createButton("🔍 Search Books");
        panel.add(searchBtn, gbc);
        gbc.gridx = 1;
        JButton requestsBtn = createButton("📩 Requests");
        panel.add(requestsBtn, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton logoutBtn = new JButton("🚪 Logout");
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.setBackground(new Color(220, 80, 80));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> logout());
        panel.add(logoutBtn, gbc);

        // Button actions
        addBookBtn.addActionListener(e -> new AddBookUI(user, this));
        myBooksBtn.addActionListener(e -> new MyBooksUI(user));
        searchBtn.addActionListener(e -> new SearchBooksUI(user));
        requestsBtn.addActionListener(e -> new RequestsUI(user));

        add(panel);
        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setPreferredSize(new Dimension(150, 40));
        return btn;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginUI();
            dispose();
        }
    }
}