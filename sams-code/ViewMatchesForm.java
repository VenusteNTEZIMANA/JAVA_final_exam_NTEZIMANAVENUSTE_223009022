package com.sams;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * ViewMatchesForm - View-only match list for Coaches and Players
 * Updated for new Match class structure
 * Java 1.7 compatible
 */
public class ViewMatchesForm extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilter;
    private User currentUser;
    private DBHelper db = DBHelper.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public ViewMatchesForm(final User currentUser) {
        this.currentUser = currentUser;
        setTitle("View Matches - " + currentUser.getUsername());
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 25, 112));
        header.setPreferredSize(new Dimension(1100, 80));
        JLabel lblTitle = new JLabel("Match Schedule & Results", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblUser = new JLabel(" " + currentUser.getDisplayName(), SwingConstants.RIGHT);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblUser.setForeground(new Color(200, 220, 255));
        lblUser.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));

        header.add(lblTitle, BorderLayout.CENTER);
        header.add(lblUser, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ===== FILTER PANEL =====
        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel lblSearch = new JLabel("Search:");
        txtSearch = new JTextField(30);
        JLabel lblFilter = new JLabel("   Filter:");
        cmbFilter = new JComboBox<String>(new String[]{"All Matches", "Upcoming", "Completed"});

        JButton btnBack = createStyledButton("Back", new Color(149, 165, 166));

        filterPanel.add(lblSearch);
        filterPanel.add(txtSearch);
        filterPanel.add(lblFilter);
        filterPanel.add(cmbFilter);
        filterPanel.add(Box.createHorizontalStrut(50));
        filterPanel.add(btnBack);
        add(filterPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new Object[]{"Home Team", "Away Team", "Date & Time", "Venue", "Score", "Winner"}, 0
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

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        loadMatchesFromDB();

        // ===== LISTENERS =====
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterMatches(); }
            public void removeUpdate(DocumentEvent e) { filterMatches(); }
            public void changedUpdate(DocumentEvent e) { filterMatches(); }
        });

        cmbFilter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                filterMatches();
            }
        });

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                if (currentUser.isCoach()) {
                    new CoachDashboard(currentUser).setVisible(true);
                } else {
                    new PlayerDashboard(currentUser).setVisible(true);
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        showMatchDetails(row);
                    }
                }
            }
        });
    }

    private JButton createStyledButton(String text, Color baseColor) {
        final Color base = baseColor;
        final Color hover = base.brighter();
        final JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(base);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 40));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(base); }
        });
        return btn;
    }

    private void loadMatchesFromDB() {
        model.setRowCount(0);
        List<Match> matches = db.getAllMatches();
        for (Match m : matches) {
            String homeTeamName = db.getTeamNameById(m.getHomeTeamId());
            String awayTeamName = db.getTeamNameById(m.getAwayTeamId());

            String score = (m.getScoreHome() != null && m.getScoreAway() != null)
                    ? m.getScoreHome() + " - " + m.getScoreAway()
                    : "Not played";

            String dateStr = m.getMatchDate() != null ? dateFormat.format(m.getMatchDate()) : "TBD";

            String status = (m.getScoreHome() != null) ? "Completed" : "Upcoming";

            model.addRow(new Object[]{
                homeTeamName != null ? homeTeamName : "Team " + m.getHomeTeamId(),
                awayTeamName != null ? awayTeamName : "Team " + m.getAwayTeamId(),
                dateStr,
                m.getVenue() != null ? m.getVenue() : "-",
                score,
                m.getWinner() != null ? m.getWinner() : status
            });
        }
    }

    private void filterMatches() {
        // Simple client-side filtering (can be enhanced)
        loadMatchesFromDB(); // Reload full data
        String searchText = txtSearch.getText().trim().toLowerCase();
        String filter = (String) cmbFilter.getSelectedItem();

        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            boolean remove = false;

            String home = ((String) model.getValueAt(i, 0)).toLowerCase();
            String away = ((String) model.getValueAt(i, 1)).toLowerCase();
            String venue = ((String) model.getValueAt(i, 3)).toLowerCase();
            String score = (String) model.getValueAt(i, 4);

            if (searchText.length() > 0 &&
                !(home.contains(searchText) || away.contains(searchText) || venue.contains(searchText))) {
                remove = true;
            }

            if (!remove && filter.equals("Upcoming") && !score.equals("Not played")) {
                remove = true;
            }

            if (!remove && filter.equals("Completed") && score.equals("Not played")) {
                remove = true;
            }

            if (remove) {
                model.removeRow(i);
            }
        }
    }

    private void showMatchDetails(int row) {
        String home = (String) model.getValueAt(row, 0);
        String away = (String) model.getValueAt(row, 1);
        String date = (String) model.getValueAt(row, 2);
        String venue = (String) model.getValueAt(row, 3);
        String score = (String) model.getValueAt(row, 4);
        String winner = (String) model.getValueAt(row, 5);

        String details = "<html><h2>" + home + " vs " + away + "</h2>" +
                         "<b>Date:</b> " + date + "<br>" +
                         "<b>Venue:</b> " + venue + "<br>" +
                         "<b>Score:</b> " + score + "<br>" +
                         "<b>Winner:</b> " + winner + "</html>";

        JOptionPane.showMessageDialog(this, details, "Match Details", JOptionPane.INFORMATION_MESSAGE);
    }
}