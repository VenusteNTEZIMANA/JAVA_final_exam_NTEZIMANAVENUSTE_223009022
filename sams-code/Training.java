package com.sams;

public class Training {
    private int id;
    private int playerId; // or userId, depending on your schema
    private String trainingDate;
    private String activity;
    private String notes;

    public Training() {}

    public Training(int id, int playerId, String trainingDate, String activity, String notes) {
        this.id = id;
        this.playerId = playerId;
        this.trainingDate = trainingDate;
        this.activity = activity;
        this.notes = notes;
    }

    public Training(int int1, String date, String string, String string2) {
		// TODO Auto-generated constructor stub
	}

	// --- Getters & Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPlayerId() { return playerId; } // ✅ fixes your error
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public String getTrainingDate() { return trainingDate; }
    public void setTrainingDate(String trainingDate) { this.trainingDate = trainingDate; }

    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", playerId=" + playerId +
                ", trainingDate='" + trainingDate + '\'' +
                ", activity='" + activity + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
