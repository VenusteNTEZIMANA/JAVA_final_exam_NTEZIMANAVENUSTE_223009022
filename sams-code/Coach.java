package com.sams;

import java.util.Date;

public class Coach {
    private Integer coachId;
    private Integer teamId; // optional
    private String name;
    private String identifier;
    private String status;
    private String location;
    private String contact;
    private Date assignedSince; // optional

    // Constructor for adding a new coach (no ID yet)
    public Coach(String name, String identifier, String status, String location, String contact) {
        this.name = name;
        this.identifier = identifier;
        this.status = status;
        this.location = location;
        this.contact = contact;
    }

    // Constructor for editing or loading from DB (with ID)
    public Coach(Integer coachId, String name, String identifier, String status, String location, String contact) {
        this.coachId = coachId;
        this.name = name;
        this.identifier = identifier;
        this.status = status;
        this.location = location;
        this.contact = contact;
    }

    // Constructor for full DB load (with TeamID and AssignedSince)
    public Coach(Integer coachId, Integer teamId, String name, String identifier, String status,
                 String location, String contact, Date assignedSince) {
        this.coachId = coachId;
        this.teamId = teamId;
        this.name = name;
        this.identifier = identifier;
        this.status = status;
        this.location = location;
        this.contact = contact;
        this.assignedSince = assignedSince;
    }

    // ================= GETTERS =================
    public Integer getCoachId() { return coachId; }
    public Integer getTeamId() { return teamId; }
    public String getName() { return name; }
    public String getIdentifier() { return identifier; }
    public String getStatus() { return status; }
    public String getLocation() { return location; }
    public String getContact() { return contact; }
    public Date getAssignedSince() { return assignedSince; }

    // ================= SETTERS =================
    public void setCoachId(Integer coachId) { this.coachId = coachId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }
    public void setName(String name) { this.name = name; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public void setStatus(String status) { this.status = status; }
    public void setLocation(String location) { this.location = location; }
    public void setContact(String contact) { this.contact = contact; }
    public void setAssignedSince(Date assignedSince) { this.assignedSince = assignedSince; }
}
