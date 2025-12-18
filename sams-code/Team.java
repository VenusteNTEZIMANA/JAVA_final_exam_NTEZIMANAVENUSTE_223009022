package com.sams;

public class Team {
    private Integer teamId;
    private String teamName;
    private String sport;        // optional
    private String homeLocation;
    private Integer coachId;     // optional

    /* ================= CONSTRUCTORS ================= */

    // No-argument constructor
    public Team() {
    }

    // Constructor for DB retrieval (TeamID, TeamName, HomeLocation)
    public Team(int teamId, String teamName, String homeLocation) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.homeLocation = homeLocation;
    }

    // Constructor for full DB retrieval (all fields)
    public Team(int teamId, String teamName, String sport, String homeLocation, Integer coachId) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.sport = sport;
        this.homeLocation = homeLocation;
        this.coachId = coachId;
    }

    // Constructor for inserting new team (without ID)
    public Team(String teamName, String sport, String homeLocation) {
        this.teamName = teamName;
        this.sport = sport;
        this.homeLocation = homeLocation;
    }

    /* ================= GETTERS ================= */
    public Integer getTeamId() { return teamId; }
    public String getTeamName() { return teamName; }
    public String getSport() { return sport; }
    public String getHomeLocation() { return homeLocation; }
    public Integer getCoachId() { return coachId; }

    /* ================= SETTERS ================= */
    public void setTeamId(Integer teamId) { this.teamId = teamId; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public void setSport(String sport) { this.sport = sport; }
    public void setHomeLocation(String homeLocation) { this.homeLocation = homeLocation; }
    public void setCoachId(Integer coachId) { this.coachId = coachId; }

    /* ================= OPTIONAL: toString ================= */
    @Override
    public String toString() {
        return "Team{" +
                "teamId=" + teamId +
                ", teamName='" + teamName + '\'' +
                ", sport='" + sport + '\'' +
                ", homeLocation='" + homeLocation + '\'' +
                ", coachId=" + coachId +
                '}';
    }
}
