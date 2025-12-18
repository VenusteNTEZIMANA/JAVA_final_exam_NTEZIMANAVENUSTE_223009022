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

/**
 * ReportsDialog - Admin view for system-wide reports
 * Tabbed interface with search/filter on each tab
 * Java 1.7 compatible
 */
public class ReportsDialog extends JDialog {

    private DBHelper db;
    private User user;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public ReportsDialog(JFrame parent, User usr) {
        super(parent, "Reports", true);
        this.user = usr;
        this.db = DBHelper.getInstance();

        setSize(1100, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(255, 87, 34));
        header.setPreferredSize(new Dimension(1100, 80));

        JLabel lblTitle = new JLabel("System Reports", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Overview of users, teams, matches, and equipment", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(new Color(255, 200, 180));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle);
        titlePanel.add(lblSubtitle);

        header.add(titlePanel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ===== TABBED PANE =====
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabs.setBackground(Color.WHITE);

        tabs.addTab("Users", createUsersPanel());
        tabs.addTab("Teams", createTeamsPanel());
        tabs.addTab("Matches", createMatchesPanel());
        tabs.addTab("Equipment", createEquipmentPanel());

        add(tabs, BorderLayout.CENTER);

        // ===== FOOTER =====
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 20));
        footer.setBackground(new Color(240, 248, 255));

        JButton closeBtn = createStyledButton("Close", new Color(158, 158, 158));
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
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
        btn.setPreferredSize(new Dimension(150, 50));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(base); }
            public void mousePressed(MouseEvent e) { btn.setBackground(pressed); }
            public void mouseReleased(MouseEvent e) { btn.setBackground(hover); }
        });

        return btn;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Username", "Role"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(255, 87, 34));
        table.getTableHeader().setForeground(Color.WHITE);

        final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        final JTextField searchField = new JTextField(30);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        panel.add(searchPanel, BorderLayout.NORTH);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        List<User> users = db.getAllUsers(); // Use correct method
        for (User u : users) {
            model.addRow(new Object[]{
                u.getUserId(),
                u.getUsername(),
                u.getRole()
            });
        }

        return panel;
    }

    private JPanel createTeamsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Team Name", "Home Location"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(255, 87, 34));
        table.getTableHeader().setForeground(Color.WHITE);

        final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        final JTextField searchField = new JTextField(30);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        panel.add(searchPanel, BorderLayout.NORTH);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        List<Team> teams = db.getAllTeams();
        for (Team t : teams) {
            model.addRow(new Object[]{
                t.getTeamId(),
                t.getTeamName(),
                t.getHomeLocation()
            });
        }

        return panel;
    }

    private JPanel createMatchesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Home Team", "Away Team", "Date & Time", "Venue", "Score", "Winner"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(255, 87, 34));
        table.getTableHeader().setForeground(Color.WHITE);

        final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        final JTextField searchField = new JTextField(30);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        panel.add(searchPanel, BorderLayout.NORTH);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        List<Match> matches = db.getAllMatches();
        for (Match m : matches) {
            String score = (m.getScoreHome() != null && m.getScoreAway() != null)
                    ? m.getScoreHome() + " - " + m.getScoreAway()
                    : "Not played";

            String dateStr = m.getMatchDate() != null ? dateFormat.format(m.getMatchDate()) : "TBD";

            model.addRow(new Object[]{
                m.getMatchId(),
                db.getTeamNameById(m.getHomeTeamId()),
                db.getTeamNameById(m.getAwayTeamId()),
                dateStr,
                m.getVenue(),
                score,
                m.getWinner() != null ? m.getWinner() : "-"
            });
        }

        return panel;
    }

    private JPanel createEquipmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Name", "Quantity", "Status", "Location"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(255, 87, 34));
        table.getTableHeader().setForeground(Color.WHITE);

        final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(sorter);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        final JTextField searchField = new JTextField(30);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        panel.add(searchPanel, BorderLayout.NORTH);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        List<Equipment> equipments = db.getAllEquipments();
        for (Equipment eq : equipments) {
            model.addRow(new Object[]{
                eq.getEquipmentId(),
                eq.getName(),
                eq.getQuantity(),
                eq.getStatus() != null ? eq.getStatus() : "Unknown",
                eq.getLocation() != null ? eq.getLocation() : "Not set"
            });
        }

        return panel;
    }
}