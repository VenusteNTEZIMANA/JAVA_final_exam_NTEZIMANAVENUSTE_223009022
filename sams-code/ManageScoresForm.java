package com.sams;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageScoresForm extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox cmbFilter;
    private Connection conn;
    private User coachUser;

    public ManageScoresForm(User coachUser) {
        this.coachUser = coachUser;
        this.conn = DBHelper.getInstance().getConnection();

        setTitle("🎯 Manage Scores - " + coachUser.getUsername());
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 247, 255));

        // ===== HEADER =====
        JLabel lblTitle = new JLabel("Manage Scores", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(25, 25, 112));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setPreferredSize(new Dimension(100, 55));
        add(lblTitle, BorderLayout.NORTH);

        // ===== SEARCH + FILTER PANEL =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBackground(new Color(235, 238, 250));

        JLabel lblSearch = new JLabel("Search:");
        txtSearch = new JTextField(20);
        JLabel lblFilter = new JLabel("Filter by Sport:");
        cmbFilter = new JComboBox(new String[]{"All", "Football", "Basketball", "Volleyball"});
        JButton btnSearch = new JButton("🔍 Search");
        JButton btnClear = new JButton("Clear");

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(lblFilter);
        searchPanel.add(cmbFilter);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClear);
        add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

        // ===== TABLE =====
        model = new DefaultTableModel(new Object[]{"ID", "Player Name", "Match", "Score", "Sport", "Date"}, 0);
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(22);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAdd = new JButton("➕ Add");
        JButton btnEdit = new JButton("✏ Edit");
        JButton btnDelete = new JButton("🗑 Delete");
        JButton btnRefresh = new JButton("🔄 Refresh");

        JButton[] buttons = {btnAdd, btnEdit, btnDelete, btnRefresh};
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setFont(new Font("Segoe UI", Font.BOLD, 13));
            buttons[i].setBackground(Color.WHITE);
            buttons[i].setFocusPainted(false);
            btnPanel.add(buttons[i]);
        }
        add(btnPanel, BorderLayout.SOUTH);

        // ===== INITIAL LOAD =====
        refreshTable(null, "All");

        // ===== ACTIONS =====
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String keyword = txtSearch.getText().trim();
                String sport = cmbFilter.getSelectedItem().toString();
                refreshTable(keyword, sport);
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText("");
                cmbFilter.setSelectedIndex(0);
                refreshTable(null, "All");
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addScore();
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editScore();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteScore();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshTable(null, "All");
            }
        });
    }

    // ===== REFRESH TABLE =====
    private void refreshTable(String keyword, String sportFilter) {
        try {
            model.setRowCount(0);
            String sql = "SELECT id, player_name, match_name, score, sport, date FROM scores WHERE 1=1";
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql += " AND (player_name LIKE ? OR match_name LIKE ?)";
            }
            if (!"All".equalsIgnoreCase(sportFilter)) {
                sql += " AND sport = ?";
            }
            PreparedStatement ps = conn.prepareStatement(sql);

            int index = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(index++, "%" + keyword + "%");
                ps.setString(index++, "%" + keyword + "%");
            }
            if (!"All".equalsIgnoreCase(sportFilter)) {
                ps.setString(index++, sportFilter);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("player_name"),
                    rs.getString("match_name"),
                    rs.getInt("score"),
                    rs.getString("sport"),
                    rs.getString("date")
                });
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Load error: " + ex.getMessage());
        }
    }

    // ===== ADD SCORE =====
    private void addScore() {
        try {
            String player = JOptionPane.showInputDialog(this, "Player Name:");
            if (player == null || player.trim().isEmpty()) return;
            String match = JOptionPane.showInputDialog(this, "Match:");
            if (match == null || match.trim().isEmpty()) return;
            String scoreStr = JOptionPane.showInputDialog(this, "Score:");
            if (scoreStr == null || scoreStr.trim().isEmpty()) return;
            int score = Integer.parseInt(scoreStr);
            String sport = JOptionPane.showInputDialog(this, "Sport (Football/Basketball/Volleyball):");
            if (sport == null || sport.trim().isEmpty()) return;
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO scores (player_name, match_name, score, sport, date) VALUES (?, ?, ?, ?, NOW())"
            );
            ps.setString(1, player);
            ps.setString(2, match);
            ps.setInt(3, score);
            ps.setString(4, sport);
            ps.executeUpdate();
            ps.close();
            JOptionPane.showMessageDialog(this, "✅ Score added successfully!");
            refreshTable(null, "All");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Add error: " + ex.getMessage());
        }
    }

    // ===== EDIT SCORE =====
    private void editScore() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a score to edit!");
            return;
        }
        try {
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            String oldPlayer = model.getValueAt(row, 1).toString();
            String oldMatch = model.getValueAt(row, 2).toString();
            int oldScore = Integer.parseInt(model.getValueAt(row, 3).toString());
            String oldSport = model.getValueAt(row, 4).toString();

            String player = JOptionPane.showInputDialog(this, "Player Name:", oldPlayer);
            if (player == null || player.trim().isEmpty()) return;
            String match = JOptionPane.showInputDialog(this, "Match:", oldMatch);
            if (match == null || match.trim().isEmpty()) return;
            String scoreStr = JOptionPane.showInputDialog(this, "Score:", oldScore);
            if (scoreStr == null || scoreStr.trim().isEmpty()) return;
            int score = Integer.parseInt(scoreStr);
            String sport = JOptionPane.showInputDialog(this, "Sport:", oldSport);
            if (sport == null || sport.trim().isEmpty()) return;

            PreparedStatement ps = conn.prepareStatement(
                "UPDATE scores SET player_name=?, match_name=?, score=?, sport=? WHERE id=?"
            );
            ps.setString(1, player);
            ps.setString(2, match);
            ps.setInt(3, score);
            ps.setString(4, sport);
            ps.setInt(5, id);
            ps.executeUpdate();
            ps.close();
            JOptionPane.showMessageDialog(this, "✅ Score updated!");
            refreshTable(null, "All");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Edit error: " + ex.getMessage());
        }
    }

    // ===== DELETE SCORE =====
    private void deleteScore() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a score to delete!");
            return;
        }
        try {
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this score?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            PreparedStatement ps = conn.prepareStatement("DELETE FROM scores WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(this, "✅ Score deleted!");
            refreshTable(null, "All");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Delete error: " + ex.getMessage());
        }
    }

    // ===== MAIN FOR TEST =====
    public static void main(String[] args) {
        User dummy = new User(1, "coach1", "1234", User.Role.COACH);
        new ManageScoresForm(dummy).setVisible(true);
    }
}

