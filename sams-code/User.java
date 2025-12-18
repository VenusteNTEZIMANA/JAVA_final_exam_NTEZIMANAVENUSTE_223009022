package com.sams;

/**
 * User Entity Class for SAMS (Sports Assistant Management System)
 * Represents a authenticated user with role-based access
 * Fully compatible with Java 1.7
 */
public class User {

    /** Available user roles in the system */
    public enum Role {
        ADMIN, COACH, PLAYER
    }

    private int id;
    private String username;
    private String password;
    private Role role;
    private String status;     // e.g., Active, Inactive (from DB)
    private String lastLogin;  // Optional: timestamp of last login

    // ============================================================
    // Constructors
    // ============================================================

    /**
     * Constructor for creating a new user (registration) - ID not known yet
     */
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = "Active"; // Default status
    }

    /**
     * Constructor for loading user from database (after login)
     */
    public User(int id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = "Active";
    }

    /**
     * Full constructor - used when loading complete user data from DB
     */
    public User(int id, String username, String password, Role role, String status, String lastLogin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
        this.lastLogin = lastLogin;
    }

    // ============================================================
    // Getters
    // ============================================================

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    // ============================================================
    // Setters
    // ============================================================

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    // ============================================================
    // Convenience Methods
    // ============================================================

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isCoach() {
        return role == Role.COACH;
    }

    public boolean isPlayer() {
        return role == Role.PLAYER;
    }

    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }

    /**
     * Returns a user-friendly display string: username (role)
     */
    public String getDisplayName() {
        return username + " (" + role + ")";
    }

    // ============================================================
    // toString for debugging
    // ============================================================

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", status='" + status + '\'' +
                ", lastLogin='" + lastLogin + '\'' +
                '}';
    }

	public Object getUserId() {
		// TODO Auto-generated method stub
		return null;
	}
}