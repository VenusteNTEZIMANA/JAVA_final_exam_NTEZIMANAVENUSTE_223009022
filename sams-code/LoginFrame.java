package com.sams;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JButton loginButton;
    private JToggleButton showPasswordButton;
    private final AuthService auth = AuthService.getInstance();

    public LoginFrame() {
        setTitle("🏆 SAMS - Login");
        setSize(1200, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // ===== LEFT SIDE (IMAGE) =====
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(25, 25, 112)); // navy-blue tone
        JLabel imgLabel = new JLabel();
        imgLabel.setIcon(new ImageIcon("C:/Users/YEHOSI/Desktop/sports_login_bg.jpg")); // ✅ your local image
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(imgLabel, BorderLayout.CENTER);

        JLabel systemTitle = new JLabel("Sports Assistant Management System", SwingConstants.CENTER);
        systemTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        systemTitle.setForeground(Color.WHITE);
        systemTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        leftPanel.add(systemTitle, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
        leftPanel.setPreferredSize(new Dimension(500, 550));

        // ===== RIGHT SIDE (FORM) =====
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel welcome = new JLabel("Welcome Back!", SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(welcome, gbc);

        JLabel signIn = new JLabel("Sign in to continue", SwingConstants.CENTER);
        signIn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        signIn.setForeground(Color.GRAY);
        gbc.gridy = 1;
        rightPanel.add(signIn, gbc);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        rightPanel.add(userLabel, gbc);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(250, 35));
        usernameField.setToolTipText("Enter your username");
        gbc.gridx = 1;
        rightPanel.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        rightPanel.add(passLabel, gbc);

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setToolTipText("Enter your password");
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        showPasswordButton = new JToggleButton("👁");
        showPasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        showPasswordButton.setPreferredSize(new Dimension(40, 35));
        showPasswordButton.setFocusPainted(false);
        showPasswordButton.setContentAreaFilled(false);
        showPasswordButton.setBorder(BorderFactory.createEmptyBorder());
        showPasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (showPasswordButton.isSelected()) {
                    passwordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar('*');
                }
            }
        });
        passwordPanel.add(showPasswordButton, BorderLayout.EAST);

        gbc.gridx = 1;
        rightPanel.add(passwordPanel, gbc);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        rightPanel.add(roleLabel, gbc);

        roleBox = new JComboBox<String>(new String[]{"ADMIN", "COACH", "PLAYER"});
        roleBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        roleBox.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        rightPanel.add(roleBox, gbc);

        // === Login Button ===
        loginButton = new JButton("Login");
        styleButton(loginButton, new Color(25, 118, 210));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(loginButton, gbc);

        // === “Create Account” link instead of button ===
        final JLabel createAccountLabel = new JLabel("📝 Create Account");
        createAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        createAccountLabel.setForeground(new Color(25, 118, 210));
        createAccountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createAccountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 6;
        rightPanel.add(createAccountLabel, gbc);

        // === Inspirational Quote (Creative Addition) ===
        String[] quotes = {
            "The harder the battle, the sweeter the victory. - Les Brown",
            "Champions keep playing until they get it right. - Billie Jean King",
            "You miss 100% of the shots you don't take. - Wayne Gretzky",
            "It's not whether you get knocked down; it's whether you get up. - Vince Lombardi"
        };
        Random random = new Random();
        JLabel quoteLabel = new JLabel(quotes[random.nextInt(quotes.length)], SwingConstants.CENTER);
        quoteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        quoteLabel.setForeground(Color.GRAY);
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 20, 10, 20);
        rightPanel.add(quoteLabel, gbc);

        add(rightPanel, BorderLayout.CENTER);

        // ====== Actions ======
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });

        createAccountLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new RegisterFrame().setVisible(true);
            }
            public void mouseEntered(MouseEvent e) {
                createAccountLabel.setText("<html><u>📝 Create Account</u></html>");
            }
            public void mouseExited(MouseEvent e) {
                createAccountLabel.setText("📝 Create Account");
            }
        });
    }

    private void styleButton(final JButton btn, final Color color) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        final Color base = color;
        final Color hover = base.brighter();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(base); }
        });
    }

    // === SAME BACKEND ===
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in username and password.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User user = auth.login(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User.Role selectedRole = User.Role.valueOf(roleBox.getSelectedItem().toString());
        if (user.getRole() == null || !user.getRole().equals(selectedRole)) {
            JOptionPane.showMessageDialog(this, "Role mismatch! You are a " + user.getRole(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        dispose();
        if (user.getRole() == User.Role.ADMIN)
            new AdminDashboard(user).setVisible(true);
        else if (user.getRole() == User.Role.COACH)
            new CoachDashboard(user).setVisible(true);
        else
            new PlayerDashboard(user).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}