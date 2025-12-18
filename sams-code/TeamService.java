package com.sams;

import java.util.ArrayList;
import java.util.List;

/**
 * TeamService - In-memory service for managing teams
 * Used for testing/demo when no database is connected
 * Fully compatible with Java 1.7
 */
public class TeamService {

    private List<Team> teams = new ArrayList<Team>();
    private int nextId = 1; // Auto-increment ID

    /**
     * Add a new team
     * @param team Team object (ID will be ignored and auto-generated)
     * @return true if added successfully
     */
    public boolean addTeam(Team team) {
        if (team == null) {
            return false;
        }

        // Create a new Team instance using default constructor + setters
        Team newTeam = new Team();
        newTeam.setTeamId(nextId++);
        newTeam.setTeamName(team.getTeamName());

        // Default to "Football" if sport is null or empty
        String sport = team.getSport();
        if (sport == null || sport.trim().isEmpty()) {
            sport = "Football";
        }
        newTeam.setSport(sport);

        newTeam.setHomeLocation(team.getHomeLocation());
        newTeam.setCoachId(team.getCoachId()); // Preserve coach ID if set

        return teams.add(newTeam);
    }

    /**
     * Update an existing team
     * @param team Updated team object (must have valid ID)
     * @return true if team was found and updated
     */
    public boolean updateTeam(Team team) {
        if (team == null || team.getTeamId() <= 0) {
            return false;
        }

        for (int i = 0; i < teams.size(); i++) {
            Team existing = teams.get(i);
            if (existing.getTeamId() == team.getTeamId()) {
                // Replace with the updated team (it already has the correct ID)
                teams.set(i, team);
                return true;
            }
        }
        return false;
    }

    /**
     * Delete a team by ID
     * @param id Team ID
     * @return true if team was found and deleted
     */
    public boolean deleteTeam(int id) {
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).getTeamId() == id) {
                teams.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Get all teams
     * @return Unmodifiable copy of the team list for safety
     */
    public List<Team> getAllTeams() {
        return new ArrayList<Team>(teams);
    }

    /**
     * Get teams by sport (case-insensitive)
     * @param sport Sport name
     * @return List of matching teams
     */
    public List<Team> getTeamsBySport(String sport) {
        List<Team> result = new ArrayList<Team>();
        if (sport == null || sport.trim().isEmpty()) {
            return result;
        }
        String lowerSport = sport.toLowerCase();
        for (Team t : teams) {
            if (t.getSport() != null && t.getSport().toLowerCase().equals(lowerSport)) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Get teams by home location (partial case-insensitive match)
     * @param location Location keyword
     * @return List of matching teams
     */
    public List<Team> getTeamsByLocation(String location) {
        List<Team> result = new ArrayList<Team>();
        if (location == null || location.trim().isEmpty()) {
            return result;
        }
        String lowerLocation = location.toLowerCase();
        for (Team t : teams) {
            if (t.getHomeLocation() != null &&
                t.getHomeLocation().toLowerCase().contains(lowerLocation)) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Find team by ID
     * @param id Team ID
     * @return Team or null if not found
     */
    public Team getTeamById(int id) {
        for (Team t : teams) {
            if (t.getTeamId() == id) {
                return t;
            }
        }
        return null;
    }

    /**
     * Clear all teams (useful for testing)
     */
    public void clearAllTeams() {
        teams.clear();
        nextId = 1;
    }

    /**
     * Get total number of teams
     * @return count
     */
    public int getTeamCount() {
        return teams.size();
    }
}