package com.sams;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ManageMessagesForm extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private User coachUser;
    private Connection conn;
    private JTextField txtSearch;
    private JComboBox<String> cmbSenderFilter;

    public ManageMessagesForm(User coachUser) {
        this.coachUser = coachUser;
        conn = DBHelper.getInstance().getConnection();

        setTitle("💬 Manage Messages - Coach " + coachUser.getUsername());
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(new Color(240, 244, 255));

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 25, 112));
        header.setPreferredSize(new Dimension(1100, 80));

        JLabel lblTitle = new JLabel("Manage Messages", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Send, view, edit and delete team communications", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(new Color(180, 200, 255));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle);
        titlePanel.add(lblSubtitle);
        header.add(titlePanel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ===== SEARCH & FILTER PANEL =====
        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(new Color(255, 255, 255));
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 250), 1),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)));
        filterPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblSearch = new JLabel("🔍 Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 15));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        filterPanel.add(lblSearch, gbc);

        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtSearch.setPreferredSize(new Dimension(300, 40));
        gbc.gridx = 1; gbc.weightx = 1.0;
        filterPanel.add(txtSearch, gbc);

        JLabel lblSender = new JLabel("👤 Filter by Sender:");
        lblSender.setFont(new Font("Segoe UI", Font.BOLD, 15));
        gbc.gridx = 2; gbc.weightx = 0;
        filterPanel.add(lblSender, gbc);

        cmbSenderFilter = new JComboBox<String>();
        cmbSenderFilter.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cmbSenderFilter.setPreferredSize(new Dimension(200, 40));
        cmbSenderFilter.addItem("All Senders");
        loadSenderIds();
        gbc.gridx = 3;
        filterPanel.add(cmbSenderFilter, gbc);

        JButton btnApply = new JButton("Apply Filter");
        stylePrimaryButton(btnApply, new Color(46, 204, 113));
        gbc.gridx = 4; gbc.insets = new Insets(8, 20, 8, 10);
        filterPanel.add(btnApply, gbc);

        JButton btnClear = new JButton("Clear");
        styleSecondaryButton(btnClear, new Color(149, 165, 166));
        gbc.gridx = 5;
        filterPanel.add(btnClear, gbc);

        add(filterPanel, BorderLayout.PAGE_START); // Fixed for Java 7 compatibility

        // ===== TABLE =====
        model = new DefaultTableModel(
                new Object[]{"Sender ID", "Receiver ID", "Subject", "Message Body", "Sent At"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(25, 25, 112));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(220, 230, 250));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        add(scroll, BorderLayout.CENTER);

        // Initial load
        refreshTable(null, "All Senders");

        // ===== ACTION BUTTON PANEL =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnPanel.setBackground(new Color(240, 244, 255));

        JButton btnAdd = new JButton("➕ Send New Message");
        JButton btnEdit = new JButton("✏ Edit Selected");
        JButton btnDelete = new JButton("🗑 Delete Selected");
        JButton btnRefresh = new JButton("🔄 Refresh List");

        stylePrimaryButton(btnAdd, new Color(52, 152, 219));
        stylePrimaryButton(btnEdit, new Color(243, 156, 18));
        stylePrimaryButton(btnDelete, new Color(231, 76, 60));
        stylePrimaryButton(btnRefresh, new Color(26, 188, 156));

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);

        add(btnPanel, BorderLayout.SOUTH);

        // ===== ACTION LISTENERS (No lambdas - Java 7 compatible) =====
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { addMessage(); }
        });

        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { editMessage(); }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteMessage(); }
        });

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshTable(null, "All Senders");
            }
        });

        btnApply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String keyword = txtSearch.getText().trim();
                String sender = (String) cmbSenderFilter.getSelectedItem();
                refreshTable(keyword.isEmpty() ? null : keyword, sender);
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText("");
                cmbSenderFilter.setSelectedIndex(0);
                refreshTable(null, "All Senders");
            }
        });

        // Double-click to view full message
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        showFullMessage(row);
                    }
                }
            }
        });
    }

    private void stylePrimaryButton(final JButton btn, Color baseColor) {
        final Color base = baseColor;
        final Color hover = base.brighter();
        final Color pressed = base.darker();

        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(base);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(base); }
            public void mousePressed(MouseEvent e) { btn.setBackground(pressed); }
            public void mouseReleased(MouseEvent e) { btn.setBackground(hover); }
        });
    }

    private void styleSecondaryButton(final JButton btn, Color baseColor) {
        final Color base = baseColor;
        final Color hover = base.brighter();
        final Color pressed = base.darker();

        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(base);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(base); }
            public void mousePressed(MouseEvent e) { btn.setBackground(pressed); }
            public void mouseReleased(MouseEvent e) { btn.setBackground(hover); }
        });
    }

    private void loadSenderIds() {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT DISTINCT SenderID FROM messages WHERE SenderID IS NOT NULL ORDER BY SenderID");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cmbSenderFilter.addItem(String.valueOf(rs.getInt("SenderID")));
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void refreshTable(String keyword, String senderFilter) {
        try {
            model.setRowCount(0);
            String sql = "SELECT SenderID, ReceiverID, Subject, Body, SentAt FROM messages WHERE 1=1";
            if (keyword != null && !keyword.isEmpty()) {
                sql += " AND (Subject LIKE ? OR Body LIKE ?)";
            }
            if (!senderFilter.equals("All Senders")) {
                sql += " AND SenderID = ?";
            }
            sql += " ORDER BY SentAt DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            int paramIndex = 1;
            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            if (!senderFilter.equals("All Senders")) {
                ps.setInt(paramIndex++, Integer.parseInt(senderFilter));
            }

            ResultSet rs = ps.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("SenderID"),
                        rs.getInt("ReceiverID"),
                        rs.getString("Subject"),
                        truncateText(rs.getString("Body"), 80),
                        sdf.format(rs.getTimestamp("SentAt"))
                });
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading messages: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }

    private void showFullMessage(int row) {
        String subject = (String) model.getValueAt(row, 2);
        String body = getFullBodyFromDB(
                (Integer) model.getValueAt(row, 0),
                (Integer) model.getValueAt(row, 1),
                subject);

        JTextArea textArea = new JTextArea(body);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scroll,
                "Full Message: " + subject, JOptionPane.INFORMATION_MESSAGE);
    }

    private String getFullBodyFromDB(int senderId, int receiverId, String subject) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT Body FROM messages WHERE SenderID = ? AND ReceiverID = ? AND Subject = ?");
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setString(3, subject);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String body = rs.getString("Body");
                rs.close();
                ps.close();
                return body == null ? "" : body;
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Message body not found.";
    }

    private void addMessage() {
        JTextField txtReceiver = new JTextField(10);
        JTextField txtSubject = new JTextField(20);
        JTextArea txtBody = new JTextArea(8, 30);
        txtBody.setLineWrap(true);
        txtBody.setWrapStyleWord(true);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; panel.add(new JLabel("Receiver ID:"), g);
        g.gridx = 1; panel.add(txtReceiver, g);
        g.gridx = 0; g.gridy = 1; panel.add(new JLabel("Subject:"), g);
        g.gridx = 1; panel.add(txtSubject, g);
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2; panel.add(new JScrollPane(txtBody), g);

        int result = JOptionPane.showConfirmDialog(this, panel, "Send New Message",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int receiverId = Integer.parseInt(txtReceiver.getText().trim());
                String subject = txtSubject.getText().trim();
                String body = txtBody.getText().trim();

                if (subject.isEmpty() || body.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Subject and message body are required!");
                    return;
                }

                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO messages (SenderID, ReceiverID, Subject, Body) VALUES (?, ?, ?, ?)");
                ps.setInt(1, coachUser.getId());
                ps.setInt(2, receiverId);
                ps.setString(3, subject);
                ps.setString(4, body);

                int rows = ps.executeUpdate();
                ps.close();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "✅ Message sent successfully!");
                    refreshTable(null, "All Senders");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Receiver ID!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error sending message: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void editMessage() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a message to edit!");
            return;
        }

        int senderId = (Integer) model.getValueAt(row, 0);
        int receiverId = (Integer) model.getValueAt(row, 1);
        String oldSubject = (String) model.getValueAt(row, 2);
        String oldBody = getFullBodyFromDB(senderId, receiverId, oldSubject);

        JTextField txtSubject = new JTextField(oldSubject, 30);
        JTextArea txtBody = new JTextArea(oldBody, 10, 40);
        txtBody.setLineWrap(true);
        txtBody.setWrapStyleWord(true);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; panel.add(new JLabel("Subject:"), g);
        g.gridx = 1; panel.add(txtSubject, g);
        g.gridx = 0; g.gridy = 1; g.gridwidth = 2; panel.add(new JScrollPane(txtBody), g);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Message",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String newSubject = txtSubject.getText().trim();
                String newBody = txtBody.getText().trim();

                if (newSubject.isEmpty() || newBody.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Subject and body cannot be empty!");
                    return;
                }

                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE messages SET Subject = ?, Body = ? " +
                        "WHERE SenderID = ? AND ReceiverID = ? AND Subject = ?");
                ps.setString(1, newSubject);
                ps.setString(2, newBody);
                ps.setInt(3, senderId);
                ps.setInt(4, receiverId);
                ps.setString(5, oldSubject);

                int rows = ps.executeUpdate();
                ps.close();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "✅ Message updated successfully!");
                    refreshTable(null, "All Senders");
                } else {
                    JOptionPane.showMessageDialog(this, "No changes made or message not found.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating message: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void deleteMessage() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a message to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>Delete this message permanently?</b><br>This action cannot be undone.</html>",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int senderId = (Integer) model.getValueAt(row, 0);
            String subject = (String) model.getValueAt(row, 2);

            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM messages WHERE SenderID = ? AND Subject = ?");
            ps.setInt(1, senderId);
            ps.setString(2, subject);

            int rows = ps.executeUpdate();
            ps.close();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "🗑 Message deleted successfully!");
                refreshTable(null, "All Senders");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete message.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting message: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                User dummy = new User(1, "coach1", "1234", User.Role.COACH);
                new ManageMessagesForm(dummy).setVisible(true);
            }
        });
    }
}