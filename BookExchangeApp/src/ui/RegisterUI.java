package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;

public class RegisterUI extends JFrame {
    private JTextField nameField, emailField, locationField;
    private JPasswordField passwordField;
    private UserDAO userDAO = new UserDAO();

    public RegisterUI() {
        setTitle("Book Exchange - Register");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("📝 Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(15);
        panel.add(nameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(15);
        panel.add(emailField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Location (City):"), gbc);
        gbc.gridx = 1;
        locationField = new JTextField(15);
        panel.add(locationField, gbc);

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back to Login");

        registerBtn.addActionListener(e -> register());
        backBtn.addActionListener(e -> {
            new LoginUI();
            dispose();
        });

        gbc.gridy = 5;
        gbc.gridx = 0;
        panel.add(registerBtn, gbc);
        gbc.gridx = 1;
        panel.add(backBtn, gbc);

        add(panel);
        setVisible(true);
    }

    private void register() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String location = locationField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = new User(name, email, password, location);
        if (userDAO.registerUser(user)) {
            JOptionPane.showMessageDialog(this, "✅ Registration successful! Please login.");
            new LoginUI();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Registration failed. Email might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}