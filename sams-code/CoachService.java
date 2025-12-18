package com.sams;

import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * CoachService - Business logic for coach operations (mainly player management)
 * 
 * Works with current simplified Player class:
 * - PlayerID, FullName, DOB (as String), Position
 * 
 * Java 1.7 compatible | No lambdas
 */
public class CoachService {

    private final DBHelper db = DBHelper.getInstance();

    /**
     * Get all players from the database
     */
    public List<Player> getAllPlayers() {
        List<Player> list = new ArrayList<Player>();
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            conn = db.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("SELECT PlayerID, FullName, DOB, Position FROM players ORDER BY FullName");

            while (rs.next()) {
                String dob = null;
                Date dobDate = rs.getDate("DOB");
                if (dobDate != null) {
                    dob = dobDate.toString(); // Converts to "YYYY-MM-DD"
                }

                Player p = new Player(
                    rs.getInt("PlayerID"),
                    rs.getString("FullName"),
                    dob,
                    rs.getString("Position")
                );
                list.add(p);
            }
        } catch (SQLException ex) {
            System.err.println("Error loading players: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            // Safe closing
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (st != null) try { st.close(); } catch (SQLException ignored) {}
            // Connection is managed by DBHelper - do not close here
        }
        return list;
    }

    /**
     * Add a new player to the database
     */
    public boolean addPlayer(Player p) {
        if (p == null || p.getFullName() == null || p.getFullName().trim().isEmpty()) {
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = db.getConnection();
            ps = conn.prepareStatement(
                "INSERT INTO players (FullName, DOB, Position) VALUES (?, ?, ?)"
            );

            ps.setString(1, p.getFullName().trim());

            String dob = p.getDob();
            if (dob != null && !dob.trim().isEmpty()) {
                ps.setDate(2, java.sql.Date.valueOf(dob.trim())); // Expects "YYYY-MM-DD"
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }

            ps.setString(3, p.getPosition());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (Exception ex) {
            System.err.println("Error adding player: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Update an existing player
     */
    public boolean updatePlayer(Player p) {
        if (p == null || p.getPlayerId() <= 0 || p.getFullName() == null || p.getFullName().trim().isEmpty()) {
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = db.getConnection();
            ps = conn.prepareStatement(
                "UPDATE players SET FullName = ?, DOB = ?, Position = ? WHERE PlayerID = ?"
            );

            ps.setString(1, p.getFullName().trim());

            String dob = p.getDob();
            if (dob != null && !dob.trim().isEmpty()) {
                ps.setDate(2, java.sql.Date.valueOf(dob.trim()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }

            ps.setString(3, p.getPosition());
            ps.setInt(4, p.getPlayerId());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (Exception ex) {
            System.err.println("Error updating player ID " + p.getPlayerId() + ": " + ex.getMessage());
            ex.printStackTrace();
            return false;
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Delete a player by ID
     */
    public boolean deletePlayer(int playerId) {
        if (playerId <= 0) {
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = db.getConnection();
            ps = conn.prepareStatement("DELETE FROM players WHERE PlayerID = ?");
            ps.setInt(1, playerId);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (Exception ex) {
            System.err.println("Error deleting player ID " + playerId + ": " + ex.getMessage());
            ex.printStackTrace();
            return false;
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        }
    }
}