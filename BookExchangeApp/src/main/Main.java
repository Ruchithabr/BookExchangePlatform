package main;

import ui.LoginUI;
import utils.DatabaseConnection;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Connect to database
        DatabaseConnection.getConnection();

        // Start GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(LoginUI::new);
    }
}