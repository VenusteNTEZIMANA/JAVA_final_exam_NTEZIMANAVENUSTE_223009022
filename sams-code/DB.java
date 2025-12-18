package com.sams;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class DB {
    private static final String URL = "jdbc:sqlite:sams.db";  // Change if using MySQL, etc.

    private Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
            return null;
        }
    }

    // Get teams for a specific coach
    public List<Team> getTeamsByCoachId(int coachId) {
        List<Team> teams = new ArrayList<Team>();
        String sql = "SELECT team_id, team_name, sport, home_location, coach_id FROM teams WHERE coach_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, coachId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Team t = new Team();
                t.setTeamId(rs.getInt("team_id"));
                t.setTeamName(rs.getString("team_name"));
                t.setSport(rs.getString("sport"));
                t.setHomeLocation(rs.getString("home_location"));
                t.setCoachId(rs.getInt("coach_id"));
                teams.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return teams;
    }

    public Team getTeamById(int teamId) {
        String sql = "SELECT team_id, team_name, sport, home_location, coach_id FROM teams WHERE team_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Team t = new Team();
                t.setTeamId(rs.getInt("team_id"));
                t.setTeamName(rs.getString("team_name"));
                t.setSport(rs.getString("sport"));
                t.setHomeLocation(rs.getString("home_location"));
                t.setCoachId(rs.getInt("coach_id"));
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addTeam(Team team) {
        String sql = "INSERT INTO teams(team_name, sport, home_location, coach_id) VALUES(?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, team.getTeamName());
            pstmt.setString(2, team.getSport());
            pstmt.setString(3, team.getHomeLocation());
            pstmt.setInt(4, team.getCoachId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Add error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTeam(Team team) {
        String sql = "UPDATE teams SET team_name = ?, sport = ?, home_location = ? WHERE team_id = ? AND coach_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, team.getTeamName());
            pstmt.setString(2, team.getSport());
            pstmt.setString(3, team.getHomeLocation());
            pstmt.setInt(4, team.getTeamId());
            pstmt.setInt(5, team.getCoachId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTeam(int teamId) {
        String sql = "DELETE FROM teams WHERE team_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
            return false;
        }
    }
}
