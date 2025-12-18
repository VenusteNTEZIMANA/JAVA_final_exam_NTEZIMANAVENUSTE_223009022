package com.sams;

public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private String subject;
    private String body;
    private String sentAt;

    public Message() {}

    public Message(int senderId, int receiverId, String subject, String body) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.subject = subject;
        this.body = body;
    }

    public Message(int int1, int int2, String string, int userId,
			String string2, String string3, String sentAt2, boolean boolean1) {
		// TODO Auto-generated constructor stub
	}

	public Message(int int1, int int2, int int3, String string, String string2,
			String string3) {
		// TODO Auto-generated constructor stub
	}

	// --- Getters & Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }  // ✅ this fixes your error
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", sentAt='" + sentAt + '\'' +
                '}';
    }
}
