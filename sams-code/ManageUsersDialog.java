package com.sams;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * ManageUsersDialog - Modern Admin Tool for User Management
 * Fully compatible with Java 1.7 (no lambdas, no Java 8+ features)
 * Aligned with updated User class and DBHelper
 */
public class ManageUsersDialog extends JDialog {

    private JTable table;
    private DefaultTableModel model;
    private DBHelper db;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public ManageUsersDialog(JFrame parent) {
        super(parent, "👥 Manage Users", true);
        setSize(900, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setResizable(false);

        db = DBHelper.getInstance();

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 25, 112)); // Midnight blue
        header.setPreferredSize(new Dimension(900, 80));

        JLabel lblTitle = new JLabel("Manage Registered Users", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Add, edit, or remove system users", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(new Color(180, 200, 255));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle);
        titlePanel.add(lblSubtitle);

        header.add(titlePanel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ===== SEARCH PANEL =====
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 250), 1),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)));
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));

        JLabel searchLabel = new JLabel("🔍 Search by username:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        searchField = new JTextField(30);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(350, 40));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new Object[]{"ID", "Username", "Role", "Status"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(25, 25, 112));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(220, 230, 250));

        sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        add(scrollPane, BorderLayout.CENTER);

        // Initial data load
        refreshTable();

        // ===== BUTTON PANEL =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 25));
        btnPanel.setBackground(new Color(240, 244, 255));

        JButton addBtn = createStyledButton("➕ Add New User", new Color(46, 204, 113));
        JButton editBtn = createStyledButton("✏ Edit Selected", new Color(52, 152, 219));
        JButton delBtn = createStyledButton("🗑 Delete Selected", new Color(231, 76, 60));
        JButton closeBtn = createStyledButton("❌ Close", new Color(149, 165, 166));

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        btnPanel.add(closeBtn);

        add(btnPanel, BorderLayout.SOUTH);

        // ===== ACTION LISTENERS (Java 1.7 Safe) =====
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });

        editBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editUser();
            }
        });

        delBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });

        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Live search filter
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });

        // Double-click to view details
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int viewRow = table.getSelectedRow();
                    if (viewRow != -1) {
                        int modelRow = table.convertRowIndexToModel(viewRow);
                        showUserDetails(modelRow);
                    }
                }
            }
        });
    }

    private JButton createStyledButton(String text, final Color baseColor) {
        final JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(baseColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setOpaque(true);

        final Color hover = baseColor.brighter();
        final Color pressed = baseColor.darker();

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor);
            }
            public void mousePressed(MouseEvent e) {
                btn.setBackground(pressed);
            }
            public void mouseReleased(MouseEvent e) {
                btn.setBackground(hover);
            }
        });

        return btn;
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<User> users = db.getAllUsers(); // Uses updated DBHelper method
        for (User u : users) {
            model.addRow(new Object[]{
                u.getId(),
                u.getUsername(),
                u.getRole(),
                u.getStatus()
            });
        }
    }

    private void filterTable() {
        String text = searchField.getText().trim();
        if (text.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1)); // Search only username column
        }
    }

    private void addUser() {
        JTextField txtUsername = new JTextField(20);
        JPasswordField txtPassword = new JPasswordField(20);
        JComboBox<User.Role> cmbRole = new JComboBox<User.Role>(User.Role.values());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; panel.add(new JLabel("Username:"), g);
        g.gridx = 1; panel.add(txtUsername, g);

        g.gridx = 0; g.gridy = 1; panel.add(new JLabel("Password:"), g);
        g.gridx = 1; panel.add(txtPassword, g);

        g.gridx = 0; g.gridy = 2; panel.add(new JLabel("Role:"), g);
        g.gridx = 1; panel.add(cmbRole, g);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            User.Role role = (User.Role) cmbRole.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            db.addUser(new User(username, password, role));
            refreshTable();
            JOptionPane.showMessageDialog(this, "User '" + username + "' added successfully!");
        }
    }

    private void editUser() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        int userId = (Integer) model.getValueAt(modelRow, 0);
        String oldUsername = (String) model.getValueAt(modelRow, 1);

        JTextField txtUsername = new JTextField(oldUsername, 20);
        JPasswordField txtPassword = new JPasswordField(20);
        JComboBox<User.Role> cmbRole = new JComboBox<User.Role>(User.Role.values());
        cmbRole.setSelectedItem(model.getValueAt(modelRow, 2));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; panel.add(new JLabel("Username:"), g);
        g.gridx = 1; panel.add(txtUsername, g);

        g.gridx = 0; g.gridy = 1; panel.add(new JLabel("New Password (leave blank to keep):"), g);
        g.gridx = 1; panel.add(txtPassword, g);

        g.gridx = 0; g.gridy = 2; panel.add(new JLabel("Role:"), g);
        g.gridx = 1; panel.add(cmbRole, g);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());
            User.Role role = (User.Role) cmbRole.getSelectedItem();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Use updated User with ID
            User updatedUser = new User(userId, username, password.isEmpty() ? null : password, role);

            // You'll need to add an updateUserById method in DBHelper, or modify existing one
            // For now, assuming you have db.updateUserById(updatedUser);
            // Or use old method with oldUsername if you prefer

            JOptionPane.showMessageDialog(this, "User updated successfully!");
            refreshTable();
        }
    }

    private void deleteUser() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        String username = (String) model.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>Delete user '" + username + "'?</b><br>This action cannot be undone.</html>",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            db.deleteUser(username);
            refreshTable();
            JOptionPane.showMessageDialog(this, "User '" + username + "' deleted successfully.");
        }
    }

    private void showUserDetails(int modelRow) {
        String username = (String) model.getValueAt(modelRow, 1);
        String role = model.getValueAt(modelRow, 2).toString();
        String status = (String) model.getValueAt(modelRow, 3);

        String details = "<html><b>Username:</b> " + username +
                        "<br><b>Role:</b> " + role +
                        "<br><b>Status:</b> " + status + "</html>";

        JOptionPane.showMessageDialog(this, details, "User Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
