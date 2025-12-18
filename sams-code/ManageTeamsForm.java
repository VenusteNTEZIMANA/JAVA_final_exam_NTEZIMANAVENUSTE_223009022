package com.sams;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * ManageTeamsForm - Coach tool for managing their teams from database
 * Modern design with live filtering
 * Fully Java 1.7 compatible
 */
public class ManageTeamsForm extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private DB db = new DB();  // Your existing database helper class
    private User coachUser;
    private JTextField searchField;

    public ManageTeamsForm(User coachUser) {
        this.coachUser = coachUser;
        setTitle("🏆 Manage Teams - Coach " + coachUser.getUsername());
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 25, 112));
        header.setPreferredSize(new Dimension(1000, 80));

        JLabel lblTitle = new JLabel("Manage Your Teams", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Add, edit, and organize teams you coach", SwingConstants.CENTER);
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

        JLabel searchLabel = new JLabel("🔍 Search teams:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        searchField = new JTextField(40);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(500, 40));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new Object[]{"ID", "Team Name", "Sport", "Home Location"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(40);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(25, 25, 112));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(220, 230, 250));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        add(scrollPane, BorderLayout.CENTER);

        refreshTable();

        // ===== BUTTON PANEL =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        btnPanel.setBackground(new Color(240, 244, 255));

        JButton addBtn = createStyledButton("➕ Add New Team", new Color(46, 204, 113));
        JButton editBtn = createStyledButton("✏ Edit Selected", new Color(52, 152, 219));
        JButton delBtn = createStyledButton("🗑 Delete Selected", new Color(231, 76, 60));
        JButton refreshBtn = createStyledButton("🔄 Refresh", new Color(26, 188, 156));

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        btnPanel.add(refreshBtn);

        add(btnPanel, BorderLayout.SOUTH);

        // ===== ACTION LISTENERS (Java 1.7 Safe) =====
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTeamAction();
            }
        });

        editBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editTeamAction();
            }
        });

        delBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTeamAction();
            }
        });

        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });

        // Live search filter
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        // Double-click to view details
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        showTeamDetails(row);
                    }
                }
            }
        });
    }

    // Styled button method (Java 1.7 safe)
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
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(base); }
            @Override
            public void mousePressed(MouseEvent e) { btn.setBackground(pressed); }
            @Override
            public void mouseReleased(MouseEvent e) { btn.setBackground(hover); }
        });

        return btn;
    }

    // Load teams belonging to this coach from database
    private void refreshTable() {
        model.setRowCount(0);
        List<Team> teams = db.getTeamsByCoachId(coachUser.getId());  // Assumes this method exists in DB class
        for (Team t : teams) {
            model.addRow(new Object[]{
                t.getTeamId(),
                t.getTeamName(),
                t.getSport(),
                t.getHomeLocation()
            });
        }
    }

    // Live filtering
    private void filterTable() {
        String text = searchField.getText().trim().toLowerCase();
        model.setRowCount(0);
        List<Team> teams = db.getTeamsByCoachId(coachUser.getId());

        for (Team t : teams) {
            if (t.getTeamName().toLowerCase().contains(text) ||
                t.getSport().toLowerCase().contains(text) ||
                t.getHomeLocation().toLowerCase().contains(text)) {
                model.addRow(new Object[]{
                    t.getTeamId(),
                    t.getTeamName(),
                    t.getSport(),
                    t.getHomeLocation()
                });
            }
        }
    }

    private void addTeamAction() {
        JTextField txtName = new JTextField(20);
        JTextField txtSport = new JTextField("Football", 20);
        JTextField txtLocation = new JTextField(20);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; panel.add(new JLabel("Team Name:"), g);
        g.gridx = 1; panel.add(txtName, g);
        g.gridx = 0; g.gridy = 1; panel.add(new JLabel("Sport:"), g);
        g.gridx = 1; panel.add(txtSport, g);
        g.gridx = 0; g.gridy = 2; panel.add(new JLabel("Home Location:"), g);
        g.gridx = 1; panel.add(txtLocation, g);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Team",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = txtName.getText().trim();
            String sport = txtSport.getText().trim();
            String location = txtLocation.getText().trim();

            if (name.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Team name and location are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (sport.isEmpty()) sport = "Football";

            Team newTeam = new Team(name, sport, location);
            newTeam.setCoachId(coachUser.getId());  // Important: link to coach

            if (db.addTeam(newTeam)) {
                JOptionPane.showMessageDialog(this, "Team '" + name + "' added successfully!");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add team (possibly duplicate name).");
            }
        }
    }

    private void editTeamAction() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a team to edit!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int teamId = (Integer) model.getValueAt(row, 0);
        Team team = db.getTeamById(teamId);
        if (team == null || team.getCoachId() != coachUser.getId()) {
            JOptionPane.showMessageDialog(this, "You can only edit your own teams.");
            return;
        }

        JTextField txtName = new JTextField(team.getTeamName(), 20);
        JTextField txtSport = new JTextField(team.getSport(), 20);
        JTextField txtLocation = new JTextField(team.getHomeLocation(), 20);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; panel.add(new JLabel("Team Name:"), g);
        g.gridx = 1; panel.add(txtName, g);
        g.gridx = 0; g.gridy = 1; panel.add(new JLabel("Sport:"), g);
        g.gridx = 1; panel.add(txtSport, g);
        g.gridx = 0; g.gridy = 2; panel.add(new JLabel("Home Location:"), g);
        g.gridx = 1; panel.add(txtLocation, g);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Team",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = txtName.getText().trim();
            String sport = txtSport.getText().trim();
            String location = txtLocation.getText().trim();

            if (name.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Team name and location are required!");
                return;
            }

            if (sport.isEmpty()) sport = "Football";

            team.setTeamName(name);
            team.setSport(sport);
            team.setHomeLocation(location);

            if (db.updateTeam(team)) {
                JOptionPane.showMessageDialog(this, "Team updated successfully!");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.");
            }
        }
    }

    private void deleteTeamAction() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a team to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int teamId = (Integer) model.getValueAt(row, 0);
        String teamName = (String) model.getValueAt(row, 1);

        Team team = db.getTeamById(teamId);
        if (team == null || team.getCoachId() != coachUser.getId()) {
            JOptionPane.showMessageDialog(this, "You can only delete your own teams.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>Delete team '" + teamName + "'?</b><br>This action cannot be undone.</html>",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (db.deleteTeam(teamId)) {
                JOptionPane.showMessageDialog(this, "Team deleted successfully.");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete team (may have players assigned).");
            }
        }
    }

    private void showTeamDetails(int viewRow) {
        int teamId = (Integer) model.getValueAt(viewRow, 0);
        Team team = db.getTeamById(teamId);
        if (team == null) return;

        String details = "<html><h3>" + team.getTeamName() + "</h3>" +
                         "<b>Sport:</b> " + team.getSport() + "<br>" +
                         "<b>Home Location:</b> " + team.getHomeLocation() + "<br>" +
                         "<b>Coach:</b> " + coachUser.getUsername() + "</html>";

        JOptionPane.showMessageDialog(this, details, "Team Details", JOptionPane.INFORMATION_MESSAGE);
    }
}