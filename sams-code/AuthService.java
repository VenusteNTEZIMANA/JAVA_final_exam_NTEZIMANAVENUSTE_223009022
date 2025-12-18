package com.sams;

import java.sql.*;

/**
 * AuthService - Handles user authentication and registration
 * Singleton pattern | Java 7 compatible | Matches 'users' table schema
 * 
 * ⚠️ SECURITY NOTE: Passwords are stored and compared in PLAIN TEXT.
 *     In production, you MUST hash passwords (e.g., BCrypt) before storing!
 */
public class AuthService {

    private static AuthService instance;
    private final DBHelper db = DBHelper.getInstance();

    private AuthService() {
        // Private constructor for singleton
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /**
     * Validates login credentials against the 'users' table
     * @param username the username
     * @param password the PLAIN TEXT password (insecure - for demo only)
     * @return User object if login successful, null otherwise
     */
    public User login(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty()) {
            System.err.println("Login attempt with empty credentials.");
            return null;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = db.getConnection();
            if (conn == null) {
                System.err.println("Database connection is null!");
                return null;
            }

            String sql = "SELECT UserID, Username, Password, Role FROM users WHERE Username = ? AND Password = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username.trim());
            ps.setString(2, password); // Plain text comparison - NOT secure!

            rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("UserID");
                String dbUsername = rs.getString("Username");
                String dbRoleStr = rs.getString("Role");

                User.Role role = User.Role.PLAYER; // default fallback
                if (dbRoleStr != null) {
                    try {
                        role = User.Role.valueOf(dbRoleStr.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid role in DB for user " + dbUsername + ": " + dbRoleStr);
                        // fallback to PLAYER
                    }
                }

                user = new User(userId, dbUsername, password, role); // password passed for session use only
                System.out.println("Login successful: " + dbUsername + " (Role: " + role + ")");
            } else {
                System.out.println("Login failed: Invalid username or password for '" + username + "'");
            }

        } catch (SQLException e) {
            System.err.println("SQL Error during login: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.closeQuiet(rs);
            db.closeQuiet(ps);
            // Connection is managed by DBHelper - do NOT close here
        }

        return user;
    }

    /**
     * Registers a new user in the database
     * @param username desired username
     * @param password plain text password (insecure)
     * @param role user role (ADMIN, COACH, PLAYER)
     * @return true if registration successful
     */
    public boolean register(String username, String password, User.Role role) {
        if (username == null || password == null || role == null) {
            System.err.println("Registration failed: null parameters.");
            return false;
        }

        if (username.trim().isEmpty() || password.isEmpty()) {
            System.err.println("Registration failed: empty username or password.");
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = db.getConnection();
            if (conn == null) {
                System.err.println("Cannot register: database connection failed.");
                return false;
            }

            String sql = "INSERT INTO users (Username, Password, Role) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username.trim());
            ps.setString(2, password); // WARNING: Storing plain text password!
            ps.setString(3, role.name()); // e.g., "ADMIN", "COACH", "PLAYER"

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("New user registered successfully: " + username.trim() + " (Role: " + role + ")");
                return true;
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.err.println("Registration failed: Username '" + username + "' already exists.");
            } else {
                System.err.println("Registration error: " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
            db.closeQuiet(ps);
            // Connection remains open via DBHelper pool
        }

        return false;
    }

    /**
     * Optional: Change password (for future use)
     */
    public boolean changePassword(int userId, String newPassword) {
        // Implement later if needed
        return false;
    }
}