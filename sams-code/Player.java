package com.sams;

import java.util.Date;

/**
 * Player - Represents a row from the 'players' table in SAMS database
 * 
 * Database columns:
 * - PlayerID (INT AUTO_INCREMENT PRIMARY KEY)
 * - FullName (VARCHAR(100) NOT NULL)
 * - DOB (DATE)
 * - Position (VARCHAR(30))
 * 
 * Java 7 compatible | No lambdas
 */
public class Player {

    private int playerId;
    private String fullName;
    private String dob;        // Date of Birth as string "YYYY-MM-DD" from DB
    private String position;

    /**
     * Full constructor - used when loading from database
     */
    public Player(int playerId, String fullName, String dob, String position) {
        this.playerId = playerId;
        this.fullName = fullName != null ? fullName : "";
        this.dob = dob;  // Can be null or formatted date string
        this.position = position != null ? position : "";
    }

    /**
     * Constructor for creating a new player (ID will be auto-generated)
     */
    public Player(String fullName, String dob, String position) {
        this(0, fullName, dob, position);
    }

    public Player(int int1, String string, int int2, String string2,
			String string3) {
		// TODO Auto-generated constructor stub
	}

	// === Getters ===
    public int getPlayerId() {
        return playerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDob() {
        return dob;
    }

    public String getPosition() {
        return position;
    }

    // === Setters ===
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * Useful for displaying in JComboBox, JList, tables, etc.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fullName);
        if (position != null && !position.isEmpty()) {
            sb.append(" - ").append(position);
        }
        if (dob != null && !dob.isEmpty()) {
            sb.append(" (Born: ").append(dob).append(")");
        }
        return sb.toString();
    }

    /**
     * Optional: equals and hashCode based on playerId (useful for collections)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player other = (Player) obj;
        return playerId == other.playerId;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(playerId).hashCode();
    }
}