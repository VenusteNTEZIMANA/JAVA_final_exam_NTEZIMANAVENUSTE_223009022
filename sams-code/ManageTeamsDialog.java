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
 * ManageTeamsDialog - Admin tool for managing sports teams
 * Modern design matching other SAMS dialogs
 * Fully Java 1.7 compatible
 */
public class ManageTeamsDialog extends JDialog {

    private JTable table;
    private DefaultTableModel model;
    private DBHelper db;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public ManageTeamsDialog(JFrame parent) {
        super(parent, "🏆 Manage Teams", true);
        setSize(1000, 650);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setResizable(false);

        db = DBHelper.getInstance();

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 25, 112));
        header.setPreferredSize(new Dimension(1000, 80));

        JLabel lblTitle = new JLabel("Manage Teams", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Create and organize your sports teams", SwingConstants.CENTER);
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

        JLabel searchLabel = new JLabel("🔍 Search by team name:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        searchField = new JTextField(35);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(400, 40));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new Object[]{"ID", "Team Name", "Home Location"}, 0
        ) {
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

        sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);

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
        JButton closeBtn = createStyledButton("❌ Close", new Color(149, 165, 166));

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        btnPanel.add(closeBtn);

        add(btnPanel, BorderLayout.SOUTH);

        // ===== ACTION LISTENERS (Java 1.7 Safe) =====
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addTeam();
            }
        });

        editBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editTeam();
            }
        });

        delBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteTeam();
            }
        });

        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Live search
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        // Double-click to view details
        table.addMouseListener(new MouseAdapter() {
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

    // FIXED: Java 1.7 compatible button styling
    private JButton createStyledButton(String text, Color baseColor) {
        final Color base = baseColor;                    // ← Final copy
        final Color hover = base.brighter();             // ← Final copy
        final Color pressed = base.darker();             // ← Final copy

        final JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(base);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(base);  // ← Now works perfectly
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
        List<Team> teams = db.getAllTeams();
        for (Team t : teams) {
            model.addRow(new Object[]{
                t.getTeamId(),
                t.getTeamName(),
               
                t.getHomeLocation()
            });
        }
    }

    private void filterTable() {
        String text = searchField.getText().trim();
        if (text.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1)); // Search team name only
        }
    }

    private void addTeam() {
        JTextField txtName = new JTextField(20);
        JTextField txtSport = new JTextField("Football", 20);
        JTextField txtLocation = new JTextField(20);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; panel.add(new JLabel("Team Name:"), g);
        g.gridx = 1; panel.add(txtName, g);

       
        g.gridx = 0; g.gridy = 2; panel.add(new JLabel("Home Location:"), g);
        g.gridx = 1; panel.add(txtLocation, g);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Team",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = txtName.getText().trim();
            String sport = txtSport.getText().trim();
            String location = txtLocation.getText().trim();

            if (name.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Team name and location are required!");
                return;
            }

            // db.addTeam(new Team(name, sport.isEmpty() ? "Football" : sport, location));
            refreshTable();
            JOptionPane.showMessageDialog(this, "Team '" + name + "' added successfully!");
        }
    }

    private void editTeam() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a team to edit!");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int teamId = (Integer) model.getValueAt(modelRow, 0);
        String oldName = (String) model.getValueAt(modelRow, 1);

        JTextField txtName = new JTextField((String) model.getValueAt(modelRow, 1), 20);
        JTextField txtSport = new JTextField((String) model.getValueAt(modelRow, 2), 20);
        JTextField txtLocation = new JTextField((String) model.getValueAt(modelRow, 3), 20);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; panel.add(new JLabel("Team Name:"), g);
        g.gridx = 1; panel.add(txtName, g);
       g.gridx = 0; g.gridy = 2; panel.add(new JLabel("Home Location:"), g);
        g.gridx = 1; panel.add(txtLocation, g);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Team",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = txtName.getText().trim();
            String location = txtLocation.getText().trim();

            if (name.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Team name and location are required!");
                return;
            }

            // db.updateTeam(new Team(teamId, name, location));
            refreshTable();
            JOptionPane.showMessageDialog(this, "Team '" + oldName + "' updated!");
        }
    }

    private void deleteTeam() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a team to delete!");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        String teamName = (String) model.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>Delete team '" + teamName + "'?</b><br>This cannot be undone.</html>",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int teamId = (Integer) model.getValueAt(modelRow, 0);
            // db.deleteTeam(teamId);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Team deleted successfully.");
        }
    }

    private void showTeamDetails(int viewRow) {
        int modelRow = table.convertRowIndexToModel(viewRow);
        String name = (String) model.getValueAt(modelRow, 1);
    
        String location = (String) model.getValueAt(modelRow, 3);

        String details = "<html><h3>" + name + "</h3>" +
                         
                         "<b>Home Location:</b> " + location + "</html>";

        JOptionPane.showMessageDialog(this, details, "Team Details", JOptionPane.INFORMATION_MESSAGE);
    }
}