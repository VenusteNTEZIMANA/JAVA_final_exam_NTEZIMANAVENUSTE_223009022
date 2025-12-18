package com.sams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * PlayerDashboard - Modern dashboard for players in SAMS
 * Clean card-based UI | Hover effects | Safe fallbacks
 * FULLY Java 1.7 compatible - NO lambdas used
 */
public class PlayerDashboard extends JFrame {

    private final PlayerService service;
    private final User user;

    public PlayerDashboard(User user) {
        this.user = user;
        this.service = new PlayerService();

        setTitle("SAMS - Player Dashboard");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 25, 112));
        header.setPreferredSize(new Dimension(1200, 90));

        // Left: Title + Welcome
        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setOpaque(false);
        leftHeader.setBorder(BorderFactory.createEmptyBorder(20, 40, 15, 20));

        JLabel lblTitle = new JLabel("Player Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblWelcome = new JLabel("Keep Pushing, " + user.getUsername() + "!");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        lblWelcome.setForeground(new Color(180, 210, 255));

        leftHeader.add(lblTitle, BorderLayout.NORTH);
        leftHeader.add(lblWelcome, BorderLayout.SOUTH);
        header.add(leftHeader, BorderLayout.WEST);

        // Right: Date + Avatar + Logout
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 25));
        rightHeader.setOpaque(false);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
        JLabel lblDate = new JLabel(sdf.format(new Date()));
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblDate.setForeground(new Color(220, 240, 255));
        rightHeader.add(lblDate);

        JLabel avatar = new JLabel("⚽");
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        avatar.setOpaque(true);
        avatar.setBackground(new Color(255, 255, 255, 50));
        avatar.setForeground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(64, 64));
        avatar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        rightHeader.add(avatar);

        JButton btnLogout = new JButton("Logout");
        stylePrimaryButton(btnLogout, new Color(231, 76, 60));
        btnLogout.setPreferredSize(new Dimension(130, 48));
        rightHeader.add(btnLogout);

        header.add(rightHeader, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ===== CARD GRID =====
        JPanel grid = new JPanel(new GridLayout(2, 3, 35, 35));
        grid.setBorder(BorderFactory.createEmptyBorder(50, 70, 60, 70));
        grid.setOpaque(false);

        grid.add(createElevatedCard("My Profile", "View your player information", new Color(52, 152, 219),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) { showMyInfo(); }
                }));

        grid.add(createElevatedCard("My Matches", "Upcoming & past games", new Color(230, 126, 34),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) { showMyMatches(); }
                }));

        grid.add(createElevatedCard("Training Schedule", "View training sessions", new Color(46, 204, 113),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) { showTrainings(); }
                }));

        grid.add(createElevatedCard("Performance Stats", "Track your progress", new Color(155, 89, 182),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) { showPerformance(); }
                }));

        grid.add(createElevatedCard("Message Coach", "Send or read messages", new Color(41, 128, 185),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) { messageCoach(); }
                }));

        grid.add(createElevatedCard("Logout", "End your session securely", new Color(231, 76, 60),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) { logout(); }
                }));

        add(grid, BorderLayout.CENTER);

        // Logout button in header
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true); // Ensure LoginFrame exists
        }
    }

    // ===== ELEVATED CARD WITH HOVER EFFECT =====
    private JPanel createElevatedCard(String title, String subtitle, Color baseColor, final ActionListener action) {
        final JPanel card = new JPanel(new BorderLayout(10, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 250), 2),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(300, 200));

        // Extract icon from first word
        String iconText = title.substring(0, title.indexOf(" ") > 0 ? title.indexOf(" ") : title.length()).trim();
        JLabel icon = new JLabel(iconText, SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        icon.setForeground(baseColor.darker());

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(baseColor.darker().darker());

        JLabel lblSub = new JLabel("<html><center>" + subtitle + "</center></html>", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(lblTitle, BorderLayout.NORTH);
        content.add(lblSub, BorderLayout.CENTER);

        card.add(icon, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        // Hover effect
        final Color hoverBg = new Color(248, 251, 255);
        final Color shadow = new Color(100, 150, 255, 70);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(hoverBg);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(shadow, 4),
                        BorderFactory.createEmptyBorder(28, 28, 28, 28)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 230, 250), 2),
                        BorderFactory.createEmptyBorder(30, 30, 30, 30)
                ));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, ""));
            }
        });

        return card;
    }

    private void stylePrimaryButton(final JButton btn, final Color baseColor) {
        final Color hover = baseColor.brighter();
        final Color pressed = baseColor.darker();

        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(baseColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(baseColor); }
            @Override
            public void mousePressed(MouseEvent e) { btn.setBackground(pressed); }
            @Override
            public void mouseReleased(MouseEvent e) { btn.setBackground(hover); }
        });
    }

    // ===== DASHBOARD ACTIONS =====
    private void showMyInfo() {
        Player p = service.getProfile(user);
        if (p == null) {
            JOptionPane.showMessageDialog(this,
                    "Your player profile is not linked yet.\nContact your coach to complete registration.",
                    "My Profile", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String msg = "<html><h2>" + p.getFullName() + "</h2>" +
                     "<b>Date of Birth:</b> " + (p.getDob() != null ? p.getDob() : "Not set") + "<br>" +
                     "<b>Position:</b> " + (p.getPosition() != null ? p.getPosition() : "Not assigned") + "</html>";
        JOptionPane.showMessageDialog(this, msg, "My Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMyMatches() {
        List<Match> matches = service.getMyMatches(user);
        if (matches == null || matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No matches scheduled yet.", "My Matches", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultListModel<String> lm = new DefaultListModel<String>();
        for (Match m : matches) {
            String date = m.getMatchDate() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(m.getMatchDate()) : "TBD";
            lm.addElement(m.getTeamHome() + " vs " + m.getTeamAway() +
                          " | " + date + " | " + m.getVenue());
        }

        JList<String> list = new JList<String>(lm);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JScrollPane sp = new JScrollPane(list);
        sp.setPreferredSize(new Dimension(700, 400));
        JOptionPane.showMessageDialog(this, sp, "My Matches", JOptionPane.PLAIN_MESSAGE);
    }

    private void showTrainings() {
        JOptionPane.showMessageDialog(this,
                "Training schedule will be available once your coach adds sessions.",
                "Training Schedule", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showPerformance() {
        JOptionPane.showMessageDialog(this,
                "Performance tracking coming soon!",
                "Performance Stats", JOptionPane.INFORMATION_MESSAGE);
    }

    private void messageCoach() {
        List<Message> messages = service.getMessages(user);
        if (messages == null || messages.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No messages yet. Contact your coach to get started!",
                    "Messages", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultListModel<String> lm = new DefaultListModel<String>();
        for (Message m : messages) {
            String dir = m.getSenderId() == user.getId() ? "You → Coach" : "Coach → You";
            lm.addElement("[" + m.getSentAt() + "] " + dir);
            lm.addElement("Subject: " + m.getSubject());
            lm.addElement(m.getBody());
            lm.addElement("——————————————————");
        }

        JList<String> list = new JList<String>(lm);
        list.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane sp = new JScrollPane(list);
        sp.setPreferredSize(new Dimension(700, 450));
        JOptionPane.showMessageDialog(this, sp, "Messages with Coach", JOptionPane.PLAIN_MESSAGE);
    }

    // Test main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                User u = new User(2, "player1", "pass", User.Role.PLAYER, "Active", "John Doe");
                new PlayerDashboard(u).setVisible(true);
            }
        });
    }
}