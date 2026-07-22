package ui;

import dao.BookDAO;
import dao.SwapRequestDAO;
import model.SwapRequest;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RequestsUI extends JFrame {
    private DefaultTableModel incomingModel, outgoingModel;
    private SwapRequestDAO requestDAO = new SwapRequestDAO();
    private BookDAO bookDAO = new BookDAO();
    private User loggedInUser;

    public RequestsUI(User user) {
        this.loggedInUser = user;
        setTitle("📩 Requests");
        setSize(800, 500);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Incoming Tab
        tabbedPane.addTab("📥 Incoming", createIncomingPanel());

        // Outgoing Tab
        tabbedPane.addTab("📤 Outgoing", createOutgoingPanel());

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel createIncomingPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Req ID", "Book Title", "Requester ID", "Status"};
        incomingModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(incomingModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();

        JButton acceptBtn = new JButton("✅ Accept");
        JButton rejectBtn = new JButton("❌ Reject");
        JButton refreshBtn = new JButton("🔄 Refresh");

        acceptBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a request.");
                return;
            }
            int reqId = (int) incomingModel.getValueAt(row, 0);
            requestDAO.acceptRequest(reqId);
            loadIncomingRequests();
        });

        rejectBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a request.");
                return;
            }
            int reqId = (int) incomingModel.getValueAt(row, 0);
            requestDAO.rejectRequest(reqId);
            loadIncomingRequests();
        });

        refreshBtn.addActionListener(e -> loadIncomingRequests());

        btnPanel.add(acceptBtn);
        btnPanel.add(rejectBtn);
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        loadIncomingRequests();
        return panel;
    }

    private void loadIncomingRequests() {
        incomingModel.setRowCount(0);
        List<SwapRequest> requests = requestDAO.getIncomingRequests(loggedInUser.getUserId());
        for (SwapRequest req : requests) {
            String bookTitle = "Unknown";
            var book = bookDAO.getBookById(req.getBookId());
            if (book != null) bookTitle = book.getTitle();
            incomingModel.addRow(new Object[]{
                    req.getRequestId(),
                    bookTitle,
                    req.getRequesterId(),
                    req.getStatus()
            });
        }
    }

    private JPanel createOutgoingPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Req ID", "Book Title", "Owner ID", "Status"};
        outgoingModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(outgoingModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> loadOutgoingRequests());

        JPanel btnPanel = new JPanel();
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        loadOutgoingRequests();
        return panel;
    }

    private void loadOutgoingRequests() {
        outgoingModel.setRowCount(0);
        List<SwapRequest> requests = requestDAO.getOutgoingRequests(loggedInUser.getUserId());
        for (SwapRequest req : requests) {
            String bookTitle = "Unknown";
            var book = bookDAO.getBookById(req.getBookId());
            if (book != null) bookTitle = book.getTitle();
            outgoingModel.addRow(new Object[]{
                    req.getRequestId(),
                    bookTitle,
                    req.getOwnerId(),
                    req.getStatus()
            });
        }
    }
}