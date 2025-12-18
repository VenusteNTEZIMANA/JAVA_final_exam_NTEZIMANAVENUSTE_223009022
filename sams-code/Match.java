package com.sams;

import java.util.Date;

public class Match {

    private int matchId;
    private int homeTeamId;      // References teams.TeamID
    private int awayTeamId;      // References teams.TeamID
    private Date matchDate;      // Corresponds to MatchDate DATETIME in DB
    private String venue;        // Corresponds to Venue VARCHAR(100) in DB

    private Integer scoreHome;
    private Integer scoreAway;
    private String winner;
    private String remarks;

    // === Constructor matching the new structure ===
    public Match(int matchId, int homeTeamId, int awayTeamId, Date matchDate, String venue,
                 Integer scoreHome, Integer scoreAway, String winner, String remarks) {
        this.matchId = matchId;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.matchDate = matchDate;
        this.venue = venue;
        this.scoreHome = scoreHome;
        this.scoreAway = scoreAway;
        this.winner = winner;
        this.remarks = remarks;
    }

    // === Alternative constructor if you still need one with team names (for display only) ===
    // You would populate team names separately by joining with the teams table
    // public Match(int matchId, String teamHome, String teamAway, Date matchDate, String venue, ...

    public Match(int int1, String string, String string2, String dateStr,
			String string3, Integer homeScore, Integer awayScore, String string4) {
		// TODO Auto-generated constructor stub
	}

	// === Getters ===
    public int getMatchId() {
        return matchId;
    }

    public int getHomeTeamId() {
        return homeTeamId;
    }

    public int getAwayTeamId() {
        return awayTeamId;
    }

    public Date getMatchDate() {
        return matchDate;
    }

    public String getVenue() {
        return venue;
    }

    public Integer getScoreHome() {
        return scoreHome;
    }

    public Integer getScoreAway() {
        return scoreAway;
    }

    public String getWinner() {
        return winner;
    }

    public String getRemarks() {
        return remarks;
    }

    // === Setters ===
    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public void setHomeTeamId(int homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public void setAwayTeamId(int awayTeamId) {
        this.awayTeamId = awayTeamId;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public void setScoreHome(Integer scoreHome) {
        this.scoreHome = scoreHome;
    }

    public void setScoreAway(Integer scoreAway) {
        this.scoreAway = scoreAway;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

	public String getTeamAway() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTeamHome() {
		// TODO Auto-generated method stub
		return null;
	}
}
