package com.sams;

public class Performance {
    private int id;
    private int userId;
    private String perfDate;
    private String metric;
    private String value;

    public Performance() {}

    public Performance(int id, int userId, String perfDate, String metric, String value) {
        this.id = id;
        this.userId = userId;
        this.perfDate = perfDate;
        this.metric = metric;
        this.value = value;
    }

    public Performance(int int1, String date, String string, String string2,
			String string3) {
		// TODO Auto-generated constructor stub
	}

	// --- Getters & Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getPerfDate() { return perfDate; }
    public void setPerfDate(String perfDate) { this.perfDate = perfDate; }

    public String getMetric() { return metric; }
    public void setMetric(String metric) { this.metric = metric; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    @Override
    public String toString() {
        return "Performance{" +
                "id=" + id +
                ", userId=" + userId +
                ", perfDate='" + perfDate + '\'' +
                ", metric='" + metric + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
