package com.sams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class RegisterFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JComboBox<String> cmbRole;
    private JToggleButton showPasswordButton;
    private final AuthService auth = AuthService.getInstance(); // Not used directly, but kept for consistency

    public RegisterFrame() {
        setTitle("🏆 SAMS - Register");
        setSize(1200, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // ===== LEFT PANEL (IMAGE SIDE) =====
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(25, 25, 112)); // Midnight blue
        JLabel imgLabel = new JLabel();
        imgLabel.setIcon(new ImageIcon("C:/Users/YEHOSI/Desktop/sports_login_bg.jpg"));
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(imgLabel, BorderLayout.CENTER);

        JLabel caption = new JLabel("Sports Assistant Management System", SwingConstants.CENTER);
        caption.setFont(new Font("Segoe UI", Font.BOLD, 22));
        caption.setForeground(Color.WHITE);
        caption.setBorder(BorderFactory.createEmptyBorder(15, 10, 25, 10));
        leftPanel.add(caption, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
        leftPanel.setPreferredSize(new Dimension(500, 550));

        // ===== RIGHT PANEL (FORM SIDE) =====
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTitle = new JLabel("Create Your Account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(25, 25, 112));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(lblTitle, gbc);

        JLabel lblSubtitle = new JLabel("Join the team and start managing sports like a pro!", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.GRAY);
        gbc.gridy = 1;
        rightPanel.add(lblSubtitle, gbc);

        // Username
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        rightPanel.add(lblUser, gbc);

        txtUser = new JTextField();
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUser.setPreferredSize(new Dimension(280, 38));
        txtUser.setToolTipText("Choose a unique username");
        gbc.gridx = 1;
        rightPanel.add(txtUser, gbc);

        // Password
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        rightPanel.add(lblPass, gbc);

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setPreferredSize(new Dimension(280, 38));

        txtPass = new JPasswordField();
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPass.setToolTipText("Enter a strong password");
        passwordPanel.add(txtPass, BorderLayout.CENTER);

        showPasswordButton = new JToggleButton("👁");
        showPasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        showPasswordButton.setPreferredSize(new Dimension(45, 38));
        showPasswordButton.setFocusPainted(false);
        showPasswordButton.setContentAreaFilled(false);
        showPasswordButton.setBorder(BorderFactory.createEmptyBorder());
        showPasswordButton.setToolTipText("Show/Hide password");
        showPasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (showPasswordButton.isSelected()) {
                    txtPass.setEchoChar((char) 0);
                } else {
                    txtPass.setEchoChar('*');
                }
            }
        });
        passwordPanel.add(showPasswordButton, BorderLayout.EAST);

        gbc.gridx = 1;
        rightPanel.add(passwordPanel, gbc);

        // Role
        JLabel lblRole = new JLabel("Role:");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        rightPanel.add(lblRole, gbc);

        cmbRole = new JComboBox<String>(new String[]{"ADMIN", "COACH", "PLAYER"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cmbRole.setPreferredSize(new Dimension(280, 38));
        gbc.gridx = 1;
        rightPanel.add(cmbRole, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 20, 15, 20);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton btnReg = new JButton("Register Now");
        JButton btnCancel = new JButton("Cancel");

        styleButton(btnReg, new Color(0, 153, 51));    // Green
        styleButton(btnCancel, new Color(176, 0, 32)); // Deep red

        btnPanel.add(btnReg);
        btnPanel.add(btnCancel);
        rightPanel.add(btnPanel, gbc);

        // Back to Login
        final JLabel backLabel = new JLabel("🔙 Back to Login");
        backLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        backLabel.setForeground(new Color(25, 118, 210));
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 20, 20, 20);
        rightPanel.add(backLabel, gbc);

        // Creative Touch: Motivational Sports Quote
        String[] quotes = {
            "Success isn't given. It's earned on the track, on the field, in the gym.",
            "Believe in yourself and all that you are capable of.",
            "Every champion was once a contender that refused to give up.",
            "The difference between try and triumph is a little 'umph'."
        };
        Random rand = new Random();
        JLabel quoteLabel = new JLabel("<html><i>\"" + quotes[rand.nextInt(quotes.length)] + "\"</i></html>", SwingConstants.CENTER);
        quoteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        quoteLabel.setForeground(new Color(100, 100, 100));
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 20, 30, 20);
        rightPanel.add(quoteLabel, gbc);

        add(rightPanel, BorderLayout.CENTER);

        // ====== ACTIONS (Java 1.7 Compatible - No Lambdas) ======
        btnReg.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtUser.getText().trim();
                String password = new String(txtPass.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                        "⚠ Username and password cannot be empty!",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                        "⚠ Password must be at least 6 characters long!",
                        "Weak Password", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String roleStr = (String) cmbRole.getSelectedItem();
                try {
                    User.Role role = User.Role.valueOf(roleStr);
                    User newUser = new User(username, password, role);

                    DBHelper.getInstance().addUser(newUser);

                    JOptionPane.showMessageDialog(RegisterFrame.this,
                        "🎉 Registration successful!\nWelcome to SAMS, " + username + "!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                    dispose();
                    new LoginFrame().setVisible(true);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                        "❌ Registration failed!\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        backLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }

            public void mouseEntered(MouseEvent e) {
                backLabel.setText("<html><u>🔙 Back to Login</u></html>");
            }

            public void mouseExited(MouseEvent e) {
                backLabel.setText("🔙 Back to Login");
            }
        });
    }

    private void styleButton(final JButton btn, final Color color) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        final Color base = color;
        final Color hover = base.brighter();
        final Color pressed = base.darker();

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(base);
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
                new RegisterFrame().setVisible(true);
            }
        });
    }
}