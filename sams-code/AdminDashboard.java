package com.sams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminDashboard extends JFrame {
    private final DBHelper db = DBHelper.getInstance();
    private final User user;

    public AdminDashboard(User u) {
        this.user = u;
        setTitle("🏆 SAMS - Admin Dashboard");
        setSize(2200, 1200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // Modern light background
        getContentPane().setBackground(new Color(240, 244, 255));

        // ===== HEADER PANEL =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 25, 112)); // Midnight Blue
        header.setPreferredSize(new Dimension(1100, 80));

        // Left: Title + Welcome
        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setOpaque(false);
        leftHeader.setBorder(BorderFactory.createEmptyBorder(15, 30, 10, 20));

        JLabel lblMainTitle = new JLabel("Administrator Dashboard");
        lblMainTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblMainTitle.setForeground(Color.WHITE);

        JLabel lblWelcome = new JLabel("Welcome back, " + user.getUsername() + " 👋");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblWelcome.setForeground(new Color(200, 220, 255));

        leftHeader.add(lblMainTitle, BorderLayout.NORTH);
        leftHeader.add(lblWelcome, BorderLayout.SOUTH);

        header.add(leftHeader, BorderLayout.WEST);

        // Right: Date/Time + User Avatar + Logout
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        rightHeader.setOpaque(false);

        // Current Date & Time (Live feel)
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy | hh:mm a");
        JLabel lblDateTime = new JLabel(sdf.format(new Date()));
        lblDateTime.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDateTime.setForeground(new Color(220, 240, 255));
        rightHeader.add(lblDateTime);

        // Simple Avatar Circle
        JLabel avatar = new JLabel("👤");
        avatar.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        avatar.setOpaque(true);
        avatar.setBackground(new Color(255, 255, 255, 40));
        avatar.setForeground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(50, 50));
        avatar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        rightHeader.add(avatar);

        // Logout Button
        JButton logout = new JButton("Logout");
        styleButton(logout, new Color(231, 76, 60), Color.WHITE); // Red accent
        logout.setPreferredSize(new Dimension(110, 40));
        rightHeader.add(logout);

        header.add(rightHeader, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ===== MAIN GRID - 2x3 CARDS =====
        JPanel grid = new JPanel(new GridLayout(2, 3, 30, 30));
        grid.setBorder(BorderFactory.createEmptyBorder(40, 60, 50, 60));
        grid.setOpaque(false);

        grid.add(createModernCard("👥 Manage Users", "Add, edit, or remove system users", new Color(52, 152, 219),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        new ManageUsersDialog(AdminDashboard.this).setVisible(true);
                    }
                }));

        grid.add(createModernCard("🏟 Manage Teams", "Create and organize sports teams", new Color(46, 204, 113),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        new ManageTeamsDialog(AdminDashboard.this).setVisible(true);
                    }
                }));

        grid.add(createModernCard("🏅 Manage Matches", "Schedule, track & update matches", new Color(243, 156, 18),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        new ManageMatchesDialog(AdminDashboard.this, user).setVisible(true);
                    }
                }));

        grid.add(createModernCard("⚙ Manage Equipment", "Track inventory & assignments", new Color(155, 89, 182),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        new ManageEquipmentDialog(AdminDashboard.this, user).setVisible(true);
                    }
                }));

        grid.add(createModernCard("📊 View Reports", "Analytics & performance insights", new Color(231, 76, 60),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        new ReportsDialog(AdminDashboard.this, user).setVisible(true);
                    }
                }));

        grid.add(createModernCard("⚽ Coach Manager", "Manage coaches and their details", 
                new Color(26, 188, 156),
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new ManageCoachDialog(AdminDashboard.this).setVisible(true);
                    }
                }));

        add(grid, BorderLayout.CENTER);

        // ===== LOGOUT ACTION =====
        logout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(AdminDashboard.this,
                        "Are you sure you want to logout?", "Confirm Logout",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginFrame().setVisible(true);
                }
            }
        });
    }

    private JPanel createModernCard(final String title, final String subtitle, final Color baseColor, final ActionListener action) {
        final JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 250), 2),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(300, 180));

        // Icon at top
        JLabel iconLabel = new JLabel(title.substring(0, title.indexOf(" ")), SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        card.add(iconLabel, BorderLayout.NORTH);

        // Title
        JLabel titleLabel = new JLabel(title.substring(title.indexOf(" ")).trim(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(baseColor.darker());

        // Subtitle
        JLabel subLabel = new JLabel("<html><center>" + subtitle + "</center></html>", SwingConstants.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLabel.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(subLabel, BorderLayout.CENTER);

        card.add(textPanel, BorderLayout.CENTER);

        // Hover Effect with Shadow-like Elevation
        final Color hoverBg = new Color(248, 250, 255);
        final Color shadowColor = new Color(100, 150, 255, 60);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(hoverBg);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(shadowColor, 3),
                        BorderFactory.createEmptyBorder(24, 24, 24, 24)));
            }

            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 230, 250), 2),
                        BorderFactory.createEmptyBorder(25, 25, 25, 25)));
            }

            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }
        });

        return card;
    }

    private void styleButton(final JButton btn, final Color bg, Color fg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        final Color hover = bg.brighter();
        final Color pressed = bg.darker();

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }

            public void mousePressed(MouseEvent e) {
                btn.setBackground(pressed);
            }

            public void mouseReleased(MouseEvent e) {
                btn.setBackground(hover);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                User admin = new User(1, "admin", "admin123", User.Role.ADMIN);
                new AdminDashboard(admin).setVisible(true);
            }
        });
    }
}