package com.sams;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {
    private static DBHelper instance;
    private Connection conn;

    // Database Configuration
    private static final String URL = "jdbc:mysql://localhost:3306/samsdb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    private DBHelper() {
        connect();
    }

    public static synchronized DBHelper getInstance() {
        if (instance == null) {
            instance = new DBHelper();
        }
        return instance;
    }

    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connected to SAMS database successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Add mysql-connector-java to classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println("Connection lost. Attempting to reconnect...");
            connect();
        }
        return conn;
    }

    // ========================================================
    // UTILITIES
    // ========================================================
    void closeQuiet(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception ignored) {}
        }
    }

    public void close() {
        closeConnection();
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    // ========================================================
    // TEAM UTILITIES
    // ========================================================
    public String getTeamNameById(int teamId) {
        String sql = "SELECT TeamName FROM teams WHERE TeamID = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, teamId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("TeamName");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching team name for ID " + teamId + ": " + e.getMessage());
        } finally {
            closeQuiet(rs);
            closeQuiet(ps);
        }
        return "Unknown Team";
    }

    // ========================================================
    // MATCH MANAGEMENT
    // ========================================================
    public boolean deleteMatch(int matchId) {
        String sql = "DELETE FROM matches WHERE MatchID = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, matchId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Failed to delete match ID " + matchId + ": " + e.getMessage());
            return false;
        } finally {
            closeQuiet(ps);
        }
    }

    public List<Match> getAllMatches() {
        List<Match> list = new ArrayList<Match>();
        String sql = "SELECT m.MatchID, m.HomeTeamID, m.AwayTeamID, m.MatchDate, m.Venue, " +
                     "m.ScoreHome, m.ScoreAway, m.Winner " +
                     "FROM matches m ORDER BY m.MatchDate DESC";
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = getConnection().createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Integer scoreHome = rs.getObject("ScoreHome") != null ? rs.getInt("ScoreHome") : null;
                Integer scoreAway = rs.getObject("ScoreAway") != null ? rs.getInt("ScoreAway") : null;

                list.add(new Match(
                    rs.getInt("MatchID"),
                    rs.getInt("HomeTeamID"),
                    rs.getInt("AwayTeamID"),
                    rs.getTimestamp("MatchDate"),
                    rs.getString("Venue"),
                    scoreHome,
                    scoreAway,
                    rs.getString("Winner"),
                    null // remarks
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to load matches: " + e.getMessage());
        } finally {
            closeQuiet(rs);
            closeQuiet(stmt);
        }
        return list;
    }

    // ========================================================
    // USER MANAGEMENT
    // ========================================================
    public void addUser(User user) {
        String sql = "INSERT INTO users (Username, Password, Role) VALUES (?, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole().name());
            ps.executeUpdate();
            System.out.println("User added: " + user.getUsername());
        } catch (SQLException e) {
            System.err.println("Failed to add user: " + e.getMessage());
        } finally {
            closeQuiet(ps);
        }
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<User>();
        String sql = "SELECT UserID, Username, Role FROM users ORDER BY Role, Username";
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = getConnection().createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                User.Role role = User.Role.PLAYER;
                try {
                    role = User.Role.valueOf(rs.getString("Role").toUpperCase());
                } catch (Exception ignored) {}
                list.add(new User(
                    rs.getInt("UserID"),
                    rs.getString("Username"),
                    null,
                    role
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to load users: " + e.getMessage());
        } finally {
            closeQuiet(rs);
            closeQuiet(stmt);
        }
        return list;
    }

    public List<User> getUsers() {
        return getAllUsers();
    }

    public void deleteUser(String username) {
        String sql = "DELETE FROM users WHERE Username = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, username);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "User deleted: " + username : "No user found: " + username);
        } catch (SQLException e) {
            System.err.println("Failed to delete user " + username + ": " + e.getMessage());
        } finally {
            closeQuiet(ps);
        }
    }

    // ========================================================
    // MESSAGING
    // ========================================================
    public void sendMessage(int senderId, int receiverId, String subject, String body) {
        String sql = "INSERT INTO messages (SenderID, ReceiverID, Subject, Body) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setString(3, subject);
            ps.setString(4, body);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to send message: " + e.getMessage());
        } finally {
            closeQuiet(ps);
        }
    }

    public List<Message> getMessagesForUser(int id) {
        List<Message> list = new ArrayList<Message>();
        String sql = "SELECT MessageID, SenderID, ReceiverID, Subject, Body, SentAt " +
                     "FROM messages WHERE ReceiverID = ? OR SenderID = ? ORDER BY SentAt DESC";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.setInt(2, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Message(
                    rs.getInt("MessageID"),
                    rs.getInt("SenderID"),
                    rs.getInt("ReceiverID"),
                    rs.getString("Subject"),
                    rs.getString("Body"),
                    rs.getTimestamp("SentAt") != null ? rs.getTimestamp("SentAt").toString() : ""
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to load messages: " + e.getMessage());
        } finally {
            closeQuiet(rs);
            closeQuiet(ps);
        }
        return list;
    }

    // ========================================================
    // TEAM MANAGEMENT
    // ========================================================
 // ================= GET ALL TEAMS =================
    public List<Team> getAllTeams() {
        List<Team> list = new ArrayList<>();
        String sql = "SELECT TeamID, TeamName, HomeLocation FROM teams ORDER BY TeamName";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(new Team(
                    rs.getInt("TeamID"),             // teamId
                    rs.getString("TeamName"),        // teamName
                               // sport (can be null)
                    rs.getString("HomeLocation")   // homeLocation
                    
                ));
            }
            
        } catch (SQLException e) {
            System.err.println("Failed to load teams: " + e.getMessage());
        }
        
        return list;
    }

    // ================= GET TEAM BY ID =================
    public Team getTeamById(int teamId) {
        String sql = "SELECT TeamID, TeamName, HomeLocation FROM teams WHERE TeamID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Team(
                    rs.getInt("TeamID"),
                    rs.getString("TeamName"),
                    rs.getString("HomeLocation")
                    
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Failed to get team by ID: " + e.getMessage());
        }
        
        return null;
    }

    // ================= ADD TEAM =================
    public boolean addTeam(Team t) {
        String sql = "INSERT INTO teams (TeamName, sport, HomeLocation) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, t.getTeamName());
            pstmt.setString(2, t.getSport());
            pstmt.setString(3, t.getHomeLocation());
            if (t.getCoachId() != null)
                pstmt.setInt(4, t.getCoachId());
            else
                pstmt.setNull(4, java.sql.Types.INTEGER);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Failed to add team: " + e.getMessage());
            return false;
        }
    }

    // ================= UPDATE TEAM =================
    public boolean updateTeam(Team t) {
        String sql = "UPDATE teams SET TeamName = ?, sport = ?, HomeLocation = ?, CoachID = ? WHERE TeamID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, t.getTeamName());
            pstmt.setString(2, t.getSport());
            pstmt.setString(3, t.getHomeLocation());
            if (t.getCoachId() != null)
                pstmt.setInt(4, t.getCoachId());
            else
                pstmt.setNull(4, java.sql.Types.INTEGER);
            
            pstmt.setInt(5, t.getTeamId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Failed to update team: " + e.getMessage());
            return false;
        }
    }

    // ================= DELETE TEAM =================
    public boolean deleteTeam(int teamId) {
        String sql = "DELETE FROM teams WHERE TeamID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teamId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Failed to delete team: " + e.getMessage());
            return false;
        }
    }

    

    // ========================================================
    // EQUIPMENT MANAGEMENT
    // ========================================================
    public List<Equipment> getAllEquipments() {
        List<Equipment> list = new ArrayList<Equipment>();
        String sql = "SELECT EquipmentID, Name, Quantity, Status, Location FROM equipment ORDER BY Name";
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = getConnection().createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new Equipment(
                    rs.getInt("EquipmentID"),
                    rs.getString("Name"),
                    rs.getInt("Quantity"),
                    rs.getString("Status"),
                    rs.getString("Location")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to load equipment: " + e.getMessage());
        } finally {
            closeQuiet(rs);
            closeQuiet(stmt);
        }
        return list;
    }

    public boolean addEquipment(Equipment newEq) {
        String sql = "INSERT INTO equipment (Name, Quantity, Status, Location) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, newEq.getName());
            ps.setInt(2, newEq.getQuantity());
            ps.setString(3, newEq.getStatus());
            ps.setString(4, newEq.getLocation());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to add equipment: " + e.getMessage());
            return false;
        } finally {
            closeQuiet(ps);
        }
    }

    public boolean updateEquipment(Equipment updated) {
        String sql = "UPDATE equipment SET Name = ?, Quantity = ?, Status = ?, Location = ? WHERE EquipmentID = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, updated.getName());
            ps.setInt(2, updated.getQuantity());
            ps.setString(3, updated.getStatus());
            ps.setString(4, updated.getLocation());
            ps.setInt(5, updated.getEquipmentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to update equipment: " + e.getMessage());
            return false;
        } finally {
            closeQuiet(ps);
        }
    }

    public boolean deleteEquipment(int id) {
        String sql = "DELETE FROM equipment WHERE EquipmentID = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to delete equipment: " + e.getMessage());
            return false;
        } finally {
            closeQuiet(ps);
        }
    }

    public Equipment getEquipmentById(int id) {
        String sql = "SELECT EquipmentID, Name, Quantity, Status, Location FROM equipment WHERE EquipmentID = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Equipment(
                    rs.getInt("EquipmentID"),
                    rs.getString("Name"),
                    rs.getInt("Quantity"),
                    rs.getString("Status"),
                    rs.getString("Location")
                );
                
            }
        } catch (SQLException e) {
            System.err.println("Failed to load equipment ID " + id + ": " + e.getMessage());
        } finally {
            closeQuiet(rs);
            closeQuiet(ps);
        }
        return null;
    }
    public List<Coach> getAllCoaches() {
        List<Coach> list = new ArrayList<Coach>();

        String sql = "SELECT CoachID, TeamID, Name, Identifier, Status, Location, Contact, AssignedSince " +
                     "FROM coaches ORDER BY Name";

        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = getConnection().createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Coach c = new Coach(
                    rs.getInt("CoachID"),
                    rs.getObject("TeamID") != null ? rs.getInt("TeamID") : null,
                    rs.getString("Name"),
                    rs.getString("Identifier"),
                    rs.getString("Status"),
                    rs.getString("Location"),
                    rs.getString("Contact"),
                    rs.getDate("AssignedSince")
                );
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Failed to load coaches: " + e.getMessage());
        } finally {
            closeQuiet(rs);
            closeQuiet(stmt);
        }

        return list;
    }
 // Add new coach
    public void addCoach(Coach c) {
        String sql = "INSERT INTO coaches (Name, Identifier, Status, Location, Contact,AssignedSince) VALUES (?, ?, ?, ?, ?,?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, c.getName());
            ps.setString(2, c.getIdentifier());
            ps.setString(3, c.getStatus());
            ps.setString(4, c.getLocation());
            ps.setString(5, c.getContact());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Failed to add coach: " + e.getMessage());
        } finally {
            closeQuiet(ps);
        }
    }

    // Update existing coach
    public void updateCoach(Coach c) {
        String sql = "UPDATE coaches SET Name=?, Identifier=?, Status=?, Location=?, Contact=? WHERE CoachID=?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, c.getName());
            ps.setString(2, c.getIdentifier());
            ps.setString(3, c.getStatus());
            ps.setString(4, c.getLocation());
            ps.setString(5, c.getContact());
            ps.setInt(6, c.getCoachId());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Failed to update coach: " + e.getMessage());
        } finally {
            closeQuiet(ps);
        }
    }


    public void deleteCoach(int id) {
        String sql = "DELETE FROM coaches WHERE CoachID = ?";
        PreparedStatement ps = null;

        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Coach deleted (ID=" + id + ")");
        } catch (SQLException e) {
            System.err.println("Failed to delete coach: " + e.getMessage());
        } finally {
            closeQuiet(ps);
        }
    }
    


	public void sendMessage(Message m) {
		// TODO Auto-generated method stub
		
	}

	public List<Performance> getPerformanceByUserId(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addTraining(Training t) {
		// TODO Auto-generated method stub
		
	}

	public List<Training> getTrainingsByUserId(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Match> getMatches() {
		// TODO Auto-generated method stub
		return null;
	}
	

	public Player getPlayerByUserId(int id) {
		// TODO Auto-generated method stub
		return null;
	}}

	



  
  

 