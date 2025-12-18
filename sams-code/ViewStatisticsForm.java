package com.sams;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ViewStatisticsForm extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private Connection conn;
    private User coachUser;

    private JLabel lblTotalEvents;
    private JLabel lblAvgPerPlayer;
    private JLabel lblHighestAmount;
    private JLabel lblMostCommonStat;

    public ViewStatisticsForm(final User coachUser) {
        this.coachUser = coachUser;
        this.conn = DBHelper.getInstance().getConnection();

        setTitle("📊 View Player Statistics - " + coachUser.getUsername());
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 247, 255));
        setResizable(false);

        // ===== HEADER =====
        JLabel lblTitle = new JLabel("Player Performance Statistics Dashboard", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(25, 25, 112));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setPreferredSize(new Dimension(1000, 60));
        add(lblTitle, BorderLayout.NORTH);

        // ===== FILTER PANEL =====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        topPanel.setBackground(new Color(235, 238, 250));

        JLabel lblFilter = new JLabel("Filter by Statistic Type:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 16));

        final JComboBox<String> cmbStatType = new JComboBox<String>(
                new String[]{"All", "Goal", "Assist", "YellowCard", "RedCard", "Rebound", "Foul"}
        );
        cmbStatType.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cmbStatType.setPreferredSize(new Dimension(200, 35));

        JButton btnLoad = new JButton("📈 Load Statistics");
        btnLoad.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLoad.setBackground(new Color(46, 204, 113));
        btnLoad.setForeground(Color.WHITE);
        btnLoad.setFocusPainted(false);
        btnLoad.setCursor(new Cursor(Cursor.HAND_CURSOR));

        topPanel.add(lblFilter);
        topPanel.add(cmbStatType);
        topPanel.add(btnLoad);

        add(topPanel, BorderLayout.PAGE_START); // Java 7 compatible

        // ===== SUMMARY STATISTICS PANEL =====
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 15));
        statsPanel.setBackground(new Color(245, 247, 255));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        lblTotalEvents = createStatLabel("Total Events Recorded", "0");
        lblAvgPerPlayer = createStatLabel("Average per Player", "0.00");
        lblHighestAmount = createStatLabel("Highest Single Amount", "0");
        lblMostCommonStat = createStatLabel("Most Common Stat", "-");

        statsPanel.add(lblTotalEvents);
        statsPanel.add(lblAvgPerPlayer);
        statsPanel.add(lblHighestAmount);
        statsPanel.add(lblMostCommonStat);

        add(statsPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new Object[]{"Player Name", "Match Date", "Venue", "Stat Type", "Amount"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(25, 25, 112));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scroll, BorderLayout.CENTER);

        // ===== BOTTOM BUTTONS =====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setBackground(new Color(245, 247, 255));

        JButton btnRefresh = new JButton("🔄 Refresh All");
        JButton btnBack = new JButton("⬅ Back to Dashboard");

        styleButton(btnRefresh, new Color(52, 152, 219));
        styleButton(btnBack, new Color(231, 76, 60));

        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);

        // ===== INITIAL LOAD =====
        loadStatistics("All");

        // ===== ACTION LISTENERS (Java 7 - no lambdas) =====
        btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = (String) cmbStatType.getSelectedItem();
                loadStatistics(selected);
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cmbStatType.setSelectedIndex(0);
                loadStatistics("All");
            }
        });

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new CoachDashboard(coachUser).setVisible(true);
            }
        });
    }

    private void styleButton(JButton btn, Color color) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JLabel createStatLabel(String title, String initialValue) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 255), 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(25, 25, 112));

        JLabel lblValue = new JLabel(initialValue, SwingConstants.CENTER);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValue.setForeground(new Color(0, 102, 204));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblValue, BorderLayout.CENTER);

        // Return the value label so we can update it later
        JPanel wrapper = new JPanel();
        wrapper.setBackground(new Color(245, 247, 255));
        wrapper.add(panel);

        // Hack: store the value label in component map for later update
        wrapper.putClientProperty("valueLabel", lblValue);
        add(wrapper); // No, don't add here — we'll add panels separately

        return lblValue; // We return the value label directly
    }

    private void loadStatistics(String statFilter) {
        model.setRowCount(0);

        int totalEvents = 0;
        int highest = 0;
        double sumPerPlayer = 0;
        int playerCount = 0;
        String mostCommon = "-";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            // Combine goals from 'scores' table and other stats from 'statistics' table
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT p.FullName AS playerName, ");
            sql.append("       m.MatchDate, m.Venue, ");
            sql.append("       'Goal' AS statType, s.Amount ");
            sql.append("FROM scores s ");
            sql.append("JOIN players p ON s.PlayerID = p.PlayerID ");
            sql.append("JOIN matches m ON s.MatchID = m.MatchID ");
            sql.append("WHERE s.Type = 'Goal' AND s.Status = 'Valid' ");

            if (!"All".equals(statFilter) && "Goal".equals(statFilter)) {
                // Already filtered
            } else if (!"All".equals(statFilter)) {
                sql.append(" UNION ALL ");
                sql.append("SELECT p.FullName, m.MatchDate, m.Venue, st.Type, st.Amount ");
                sql.append("FROM statistics st ");
                sql.append("JOIN players p ON st.PlayerID = p.PlayerID ");
                sql.append("JOIN matches m ON st.MatchID = m.MatchID ");
                sql.append("WHERE st.Type = ? AND st.Status = 'Valid' ");
            } else {
                sql.append(" UNION ALL ");
                sql.append("SELECT p.FullName, m.MatchDate, m.Venue, st.Type, st.Amount ");
                sql.append("FROM statistics st ");
                sql.append("JOIN players p ON st.PlayerID = p.PlayerID ");
                sql.append("JOIN matches m ON st.MatchID = m.MatchID ");
                sql.append("WHERE st.Status = 'Valid' ");
            }

            sql.append(" ORDER BY m.MatchDate DESC");

            PreparedStatement ps = conn.prepareStatement(sql.toString());

            if (!"All".equals(statFilter) && !"Goal".equals(statFilter)) {
                ps.setString(1, statFilter);
            }

            ResultSet rs = ps.executeQuery();

            java.util.Map<String, Integer> statCount = new java.util.HashMap<String, Integer>();
            java.util.Map<String, Integer> playerTotal = new java.util.HashMap<String, Integer>();

            while (rs.next()) {
                String player = rs.getString("playerName");
                Timestamp matchDate = rs.getTimestamp("MatchDate");
                String venue = rs.getString("Venue");
                String type = rs.getString("statType");
                int amount = rs.getInt("Amount");

                String displayDate = matchDate != null ? dateFormat.format(matchDate) : "Unknown";

                model.addRow(new Object[]{player, displayDate, venue, type, amount});

                totalEvents += amount;

                if (amount > highest) highest = amount;

                // Count stat types
                int cnt = statCount.containsKey(type) ? statCount.get(type) : 0;
                statCount.put(type, cnt + 1);

                // Sum per player
                int pTotal = playerTotal.containsKey(player) ? playerTotal.get(player) : 0;
                playerTotal.put(player, pTotal + amount);
            }

            playerCount = playerTotal.size();
            if (playerCount > 0) {
                for (int val : playerTotal.values()) {
                    sumPerPlayer += val;
                }
            }

            // Find most common stat
            int maxCount = 0;
            for (java.util.Map.Entry<String, Integer> entry : statCount.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostCommon = entry.getKey();
                }
            }

            // Update labels
            lblTotalEvents.setText(String.valueOf(totalEvents));
            lblHighestAmount.setText(String.valueOf(highest));
            lblMostCommonStat.setText(mostCommon + " (" + maxCount + ")");
            if (playerCount > 0) {
                double avg = sumPerPlayer / playerCount;
                lblAvgPerPlayer.setText(String.format("%.2f", avg));
            } else {
                lblAvgPerPlayer.setText("0.00");
            }

            rs.close();
            ps.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Error loading statistics: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                User dummy = new User(1, "coach1", "1234", User.Role.COACH);
                new ViewStatisticsForm(dummy).setVisible(true);
            }
        });
    }
}