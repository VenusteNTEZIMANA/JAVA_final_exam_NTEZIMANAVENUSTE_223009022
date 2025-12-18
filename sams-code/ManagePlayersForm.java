package com.sams;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;  // <-- THIS WAS MISSING! Fixes "List is not generic" error

/**
 * ManagePlayersForm - Coach tool to manage players
 * Modern UI | Search & Filter | Add/Edit/Delete
 * Matches current Player class (PlayerID, FullName, DOB, Position)
 * Java 1.7 compatible
 */
public class ManagePlayersForm extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilter;
    private PlayerService service = new PlayerService();
    private User coachUser;

    public ManagePlayersForm(User coachUser) {
        this.coachUser = coachUser;
        setTitle("Manage Players - " + coachUser.getUsername());
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 25, 112));
        header.setPreferredSize(new Dimension(1000, 80));

        JLabel lblTitle = new JLabel("Manage Players", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Add, edit, and view player profiles", SwingConstants.CENTER);
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
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel lblFilter = new JLabel("   Position:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cmbFilter = new JComboBox<String>(new String[]{"All", "Forward", "Midfielder", "Defender", "Goalkeeper"});

        JButton btnSearch = createStyledButton("Search", new Color(52, 152, 219));
        JButton btnClear = createStyledButton("Clear", new Color(149, 165, 166));

        filterPanel.add(lblSearch);
        filterPanel.add(txtSearch);
        filterPanel.add(lblFilter);
        filterPanel.add(cmbFilter);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(btnSearch);
        filterPanel.add(btnClear);

        add(filterPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new Object[]{"ID", "Full Name", "Date of Birth", "Position"}, 0
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

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        add(scroll, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        btnPanel.setBackground(new Color(240, 244, 255));

        JButton btnAdd = createStyledButton("Add Player", new Color(46, 204, 113));
        JButton btnEdit = createStyledButton("Edit Selected", new Color(52, 152, 219));
        JButton btnDelete = createStyledButton("Delete Selected", new Color(231, 76, 60));
        JButton btnRefresh = createStyledButton("Refresh", new Color(155, 89, 182));

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);

        add(btnPanel, BorderLayout.SOUTH);

        // Initial load
        refreshTable();

        // ===== ACTION LISTENERS =====
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText("");
                cmbFilter.setSelectedIndex(0);
                refreshTable();
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addPlayer();
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editPlayer();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deletePlayer();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });

        // Double-click for details
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        showPlayerDetails(row);
                    }
                }
            }
        });
    }

    private JButton createStyledButton(String text, Color baseColor) {
        final Color base = baseColor;
        final Color hover = base.brighter();
        final Color pressed = base.darker();

        final JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(base);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 50));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(base); }
            public void mousePressed(MouseEvent e) { btn.setBackground(pressed); }
            public void mouseReleased(MouseEvent e) { btn.setBackground(hover); }
        });

        return btn;
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Player> players = service.getAllPlayers();

        String keyword = txtSearch.getText().trim().toLowerCase();
        String positionFilter = (String) cmbFilter.getSelectedItem();

        for (Player p : players) {
            boolean matchesSearch = keyword.isEmpty()
                    || p.getFullName().toLowerCase().contains(keyword)
                    || (p.getPosition() != null && p.getPosition().toLowerCase().contains(keyword))
                    || (p.getDob() != null && p.getDob().toLowerCase().contains(keyword));

            boolean matchesFilter = "All".equals(positionFilter)
                    || (p.getPosition() != null && p.getPosition().equalsIgnoreCase(positionFilter));

            if (matchesSearch && matchesFilter) {
                model.addRow(new Object[]{
                    p.getPlayerId(),
                    p.getFullName(),
                    p.getDob() != null ? p.getDob() : "Not set",
                    p.getPosition() != null ? p.getPosition() : "N/A"
                });
            }
        }
    }

    private void addPlayer() {
        JTextField nameField = new JTextField(20);
        JTextField dobField = new JTextField(12);
        JTextField positionField = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        panel.add(dobField);
        panel.add(new JLabel("Position:"));
        panel.add(positionField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Player",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String dob = dobField.getText().trim();
            String position = positionField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Full name is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Uses correct constructor: Player(String fullName, String dob, String position)
            Player newPlayer = new Player(name, dob.isEmpty() ? null : dob, position);

            // TODO: Implement in PlayerService & DBHelper
            // service.addPlayer(newPlayer);

            JOptionPane.showMessageDialog(this, "Player added successfully!\n(Implement addPlayer() in service)", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        }
    }

    private void editPlayer() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a player to edit!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) model.getValueAt(row, 0);
        Player current = service.getPlayerById(id);

        if (current == null) {
            JOptionPane.showMessageDialog(this, "Could not load player data.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField(current.getFullName(), 20);
        JTextField dobField = new JTextField(current.getDob() != null ? current.getDob() : "", 12);
        JTextField positionField = new JTextField(current.getPosition() != null ? current.getPosition() : "", 15);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Date of Birth:"));
        panel.add(dobField);
        panel.add(new JLabel("Position:"));
        panel.add(positionField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Player ID: " + id,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Uses correct constructor with ID
            Player updated = new Player(id, name, dobField.getText().trim(), positionField.getText().trim());

            // TODO: service.updatePlayer(updated);
            JOptionPane.showMessageDialog(this, "Player updated successfully!\n(Implement updatePlayer())", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        }
    }

    private void deletePlayer() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a player to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete player: " + name + " (ID: " + id + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: service.deletePlayer(id);
            JOptionPane.showMessageDialog(this, "Player deleted!\n(Implement deletePlayer())", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        }
    }

    private void showPlayerDetails(int row) {
        String details = "<html><h3>" + model.getValueAt(row, 1) + "</h3>" +
                         "<b>ID:</b> " + model.getValueAt(row, 0) + "<br>" +
                         "<b>Date of Birth:</b> " + model.getValueAt(row, 2) + "<br>" +
                         "<b>Position:</b> " + model.getValueAt(row, 3) + "</html>";

        JOptionPane.showMessageDialog(this, details, "Player Details", JOptionPane.INFORMATION_MESSAGE);
    }

    // Test main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                User dummyCoach = new User(1, "coach1", "pass", User.Role.COACH, "Active", "Head Coach");
                new ManagePlayersForm(dummyCoach).setVisible(true);
            }
        });
    }
}