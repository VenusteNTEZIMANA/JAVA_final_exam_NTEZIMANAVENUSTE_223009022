package com.sams;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

/**
 * ManageMatchesDialog - Admin tool for managing matches
 * Updated to use new Match class with Team IDs and proper Date handling
 * Java 1.7 compatible
 */
public class ManageMatchesDialog extends JDialog {
    private JTable table;
    private DefaultTableModel model;
    private DBHelper db;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private User currentUser;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public ManageMatchesDialog(JFrame parent) {
        this(parent, null);
    }

    public ManageMatchesDialog(JFrame parent, User user) {
        super(parent, "Manage Matches", true);
        this.currentUser = user;
        this.db = DBHelper.getInstance();
        setSize(1100, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setResizable(false);

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 25, 112));
        header.setPreferredSize(new Dimension(1100, 80));
        JLabel lblTitle = new JLabel("Manage Matches", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(Color.WHITE);
        JLabel lblSubtitle = new JLabel("Schedule, update, and track all matches", SwingConstants.CENTER);
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
        JLabel searchLabel = new JLabel("Search matches:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        searchField = new JTextField(40);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(500, 40));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new Object[]{"ID", "Home Team", "Away Team", "Date & Time", "Venue", "Score", "Winner"}, 0
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

        sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        add(scrollPane, BorderLayout.CENTER);

        refreshTable();

        // ===== BUTTON PANEL =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        btnPanel.setBackground(new Color(240, 244, 255));

        JButton addBtn = createStyledButton("Add Match", new Color(46, 204, 113));
        JButton editBtn = createStyledButton("Edit Selected", new Color(52, 152, 219));
        JButton delBtn = createStyledButton("Delete Selected", new Color(231, 76, 60));
        JButton closeBtn = createStyledButton("Close", new Color(149, 165, 166));

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // ===== ROLE-BASED ACCESS =====
        applyRoleAccess();

        // ===== ACTION LISTENERS =====
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addMatch();
            }
        });

        editBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editMatch();
            }
        });

        delBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteMatch();
            }
        });

        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        row = table.convertRowIndexToModel(row); // Important for sorted tables
                        showMatchDetails(row);
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
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(base); }
            public void mousePressed(MouseEvent e) { btn.setBackground(pressed); }
            public void mouseReleased(MouseEvent e) { btn.setBackground(hover); }
        });
        return btn;
    }

    private void applyRoleAccess() {
        if (currentUser != null && currentUser.isPlayer()) {
            JOptionPane.showMessageDialog(this, "Players have view-only access to matches.", "Info", JOptionPane.INFORMATION_MESSAGE);
            table.setEnabled(false);
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Match> matches = db.getAllMatches();
        for (Match m : matches) {
            String homeTeamName = db.getTeamNameById(m.getHomeTeamId());
            String awayTeamName = db.getTeamNameById(m.getAwayTeamId());

            String score = (m.getScoreHome() != null && m.getScoreAway() != null)
                    ? m.getScoreHome() + " - " + m.getScoreAway()
                    : "Not played";

            String dateStr = m.getMatchDate() != null ? dateFormat.format(m.getMatchDate()) : "TBD";

            model.addRow(new Object[]{
                m.getMatchId(),
                homeTeamName != null ? homeTeamName : "Unknown (" + m.getHomeTeamId() + ")",
                awayTeamName != null ? awayTeamName : "Unknown (" + m.getAwayTeamId() + ")",
                dateStr,
                m.getVenue() != null ? m.getVenue() : "-",
                score,
                m.getWinner() != null ? m.getWinner() : "-"
            });
        }
    }

    private void filterTable() {
        String text = searchField.getText().trim();
        if (text.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void addMatch() {
        // TODO: Open Add/Edit dialog and call db.addMatch(newMatch)
        JOptionPane.showMessageDialog(this, "Add Match - Implement dialog form and DBHelper.addMatch()", "Feature", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editMatch() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a match to edit!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        int matchId = (Integer) model.getValueAt(modelRow, 0);
        // TODO: Load match and open edit dialog
        JOptionPane.showMessageDialog(this, "Edit Match ID: " + matchId + " - Implement edit form!", "Feature", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteMatch() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a match to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        int matchId = (Integer) model.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Match ID " + matchId + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = db.deleteMatch(matchId);
            JOptionPane.showMessageDialog(this, success ? "Match deleted successfully." : "Failed to delete match.",
                    "Delete Result", success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (success) refreshTable();
        }
    }

    private void showMatchDetails(int modelRow) {
        String home = (String) model.getValueAt(modelRow, 1);
        String away = (String) model.getValueAt(modelRow, 2);
        String date = (String) model.getValueAt(modelRow, 3);
        String venue = (String) model.getValueAt(modelRow, 4);
        String score = (String) model.getValueAt(modelRow, 5);
        String winner = (String) model.getValueAt(modelRow, 6);

        String details = "<html><h2>" + home + " vs " + away + "</h2>" +
                         "<b>Date & Time:</b> " + date + "<br>" +
                         "<b>Venue:</b> " + venue + "<br>" +
                         "<b>Score:</b> " + score + "<br>" +
                         "<b>Winner:</b> " + winner + "</html>";

        JOptionPane.showMessageDialog(this, details, "Match Details", JOptionPane.INFORMATION_MESSAGE);
    }
}