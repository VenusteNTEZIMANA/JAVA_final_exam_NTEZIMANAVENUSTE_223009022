package com.sams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CoachDashboard extends JFrame {
    private User coachUser;
    private JPanel contentPanel;

    public CoachDashboard(final User coachUser) {
        this.coachUser = coachUser;
        setTitle("🏆 SAMS - Coach Dashboard");
        setSize(2200, 1200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // Modern background
        getContentPane().setBackground(new Color(245, 249, 255));

        // ===== HEADER PANEL =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 25, 112));
        header.setPreferredSize(new Dimension(1200, 90));

        // Left side: Title + Welcome
        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setOpaque(false);
        leftHeader.setBorder(BorderFactory.createEmptyBorder(20, 40, 15, 20));

        JLabel lblTitle = new JLabel("Coach Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblWelcome = new JLabel("Welcome back, Coach " + coachUser.getUsername() + " 👋");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        lblWelcome.setForeground(new Color(200, 220, 255));

        leftHeader.add(lblTitle, BorderLayout.NORTH);
        leftHeader.add(lblWelcome, BorderLayout.SOUTH);
        header.add(leftHeader, BorderLayout.WEST);

        // Right side: Date/Time + Avatar + Logout
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 25));
        rightHeader.setOpaque(false);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy | hh:mm a");
        JLabel lblDateTime = new JLabel(sdf.format(new Date()));
        lblDateTime.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblDateTime.setForeground(new Color(220, 240, 255));
        rightHeader.add(lblDateTime);

        JLabel avatar = new JLabel("🏃");
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        avatar.setOpaque(true);
        avatar.setBackground(new Color(255, 255, 255, 50));
        avatar.setForeground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(60, 60));
        avatar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        rightHeader.add(avatar);

        JButton btnLogout = new JButton("Logout");
        stylePrimaryButton(btnLogout, new Color(231, 76, 60));
        btnLogout.setPreferredSize(new Dimension(120, 45));
        rightHeader.add(btnLogout);

        header.add(rightHeader, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ===== SIDE MENU =====
        JPanel sideMenu = new JPanel();
        sideMenu.setBackground(new Color(30, 35, 85));
        sideMenu.setLayout(new GridBagLayout());
        sideMenu.setPreferredSize(new Dimension(280, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.weightx = 1.0;

        final String[][] menuData = {
            {"🏃 Manage Players", "View and manage your players"},
            {"👥 Manage Teams", "Organize team assignments"},
            {"💬 Messages", "Communicate with players & staff"},
            {"⚽ View Matches", "Upcoming and past games"},
            {"📊 Statistics", "Performance analytics"},
            {"🎯 Manage Scores", "Record match results"}
        };

        // We will store every menu item here so we can attach listener safely
        final JPanel[] menuItems = new JPanel[menuData.length + 1]; // +1 for Logout

        for (int i = 0; i < menuData.length; i++) {
            gbc.gridy = i;
            menuItems[i] = createMenuItem(menuData[i][0], menuData[i][1]);
            sideMenu.add(menuItems[i], gbc);

            final int index = i;
            menuItems[i].addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    handleMenuClick(menuData[index][0], contentPanel);
                }
            });
        }

        // Logout at bottom
        gbc.gridy = menuData.length + 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        menuItems[menuData.length] = createMenuItem("🚪 Logout", "End your session securely");
        sideMenu.add(menuItems[menuData.length], gbc);

        menuItems[menuData.length].addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleMenuClick("🚪 Logout", contentPanel);
            }
        });

        add(sideMenu, BorderLayout.WEST);

        // ===== MAIN CONTENT AREA =====
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel lblBigWelcome = new JLabel("Ready to Lead Today?");
        lblBigWelcome.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblBigWelcome.setForeground(new Color(25, 25, 112));
        lblBigWelcome.setHorizontalAlignment(SwingConstants.CENTER);

        String[] quotes = {
            "Great coaches don't just teach skills — they inspire greatness.",
            "Success is where preparation and opportunity meet.",
            "Champions are made when no one is watching.",
            "The best teams play as one."
        };
        java.util.Random rand = new java.util.Random();
        JLabel lblQuote = new JLabel("<html><i>\"" + quotes[rand.nextInt(quotes.length)] + "\"</i></html>");
        lblQuote.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        lblQuote.setForeground(Color.GRAY);
        lblQuote.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel welcomePanel = new JPanel(new GridLayout(2, 1, 20, 20));
        welcomePanel.setOpaque(false);
        welcomePanel.add(lblBigWelcome);
        welcomePanel.add(lblQuote);

        contentPanel.add(welcomePanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // ===== LOGOUT BUTTON ACTION (top right) =====
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(CoachDashboard.this,
                        "Are you sure you want to logout?", "Confirm Logout",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginFrame().setVisible(true);
                }
            }
        });
    }

    private JPanel createMenuItem(String title, String subtitle) {
        final JPanel item = new JPanel(new BorderLayout());
        item.setBackground(new Color(30, 35, 85));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        item.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(180, 200, 255));

        textPanel.add(lblTitle, BorderLayout.NORTH);
        textPanel.add(lblSub, BorderLayout.SOUTH);

        item.add(textPanel, BorderLayout.WEST);

        final Color hoverBg = new Color(50, 60, 120);
        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                item.setBackground(hoverBg);
            }
            public void mouseExited(MouseEvent e) {
                item.setBackground(new Color(30, 35, 85));
            }
        });

        return item;
    }

    private void handleMenuClick(String title, JPanel contentPanel) {
        try {
            if (title.contains("Manage Players")) {
                new ManagePlayersForm(coachUser).setVisible(true);
            } else if (title.contains("Manage Teams")) {
                new ManageTeamsForm(coachUser).setVisible(true);
            } else if (title.contains("Messages")) {
                new ManageMessagesForm(coachUser).setVisible(true);
            } else if (title.contains("View Matches")) {
                new ViewMatchesForm(coachUser).setVisible(true);
            } else if (title.contains("Statistics")) {
                new ViewStatisticsForm(coachUser).setVisible(true);
            } else if (title.contains("Manage Scores")) {
                new ManageScoresForm(coachUser).setVisible(true);
            } else if (title.contains("Logout")) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to logout?", "Confirm Logout",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginFrame().setVisible(true);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Unable to open module: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void stylePrimaryButton(final JButton btn, final Color baseColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(baseColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        final Color hover = baseColor.brighter();
        final Color pressed = baseColor.darker();

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor);
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
                User dummy = new User(1, "coach1", "1234", User.Role.COACH);
                new CoachDashboard(dummy).setVisible(true);
            }
        });
    }
}