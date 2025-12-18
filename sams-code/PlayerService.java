package com.sams;

import java.util.List;

/**
 * 🎯 PlayerService - Business logic layer for player-related operations.
 * Provides a clean separation between UI (PlayerDashboard) and database (DBHelper).
 * Java 1.7 compatible, designed for readability and future scalability.
 */
public class PlayerService {

    private final DBHelper db;

    public PlayerService() {
        db = DBHelper.getInstance();
    }

    // 🧍 Get Player Profile
    public Player getProfile(User user) {
        if (user == null) return null;
        try {
            return db.getPlayerByUserId(user.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ⚽ Get Player Matches
    public List<Match> getMyMatches(User user) {
        try {
            // If you later link players to teams, filter by player's team.
            return db.getMatches();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 🏋️‍♂️ Get Player Trainings
    public List<Training> getTrainings(User user) {
        try {
            return db.getTrainingsByUserId(user.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ➕ Add New Training
    public void addTraining(Training t) {
        try {
            db.addTraining(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 📊 Player Performance Stats
    public List<Performance> getPerformance(User user) {
        try {
            return db.getPerformanceByUserId(user.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 💬 Get Messages for Player
    public List<Message> getMessages(User user) {
        try {
            return db.getMessagesForUser(user.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 📤 Send Message to Coach
    public void sendMessage(Message m) {
        try {
            db.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public List<Player> getAllPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	public Player getPlayerById(int id) {
		// TODO Auto-generated method stub
		return null;
	}
}
