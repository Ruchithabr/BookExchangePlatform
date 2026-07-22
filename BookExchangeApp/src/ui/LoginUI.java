package ui;

import dao.UserDAO;
import model.User;
import utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUI extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private UserDAO userDAO = new UserDAO();

    public LoginUI() {
        setTitle("Book Exchange - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("📚 Book Exchange Platform");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Email
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(15);
        panel.add(emailField, gbc);

        // Password
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Buttons
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> {
            new RegisterUI();
            dispose();
        });

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(loginBtn, gbc);
        gbc.gridx = 1;
        panel.add(registerBtn, gbc);

        add(panel);
        setVisible(true);
    }

    private void login() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        User user = userDAO.loginUser(email, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "✅ Login successful! Welcome, " + user.getName());
            new DashboardUI(user);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        DatabaseConnection.getConnection();
        SwingUtilities.invokeLater(LoginUI::new);
    }
}