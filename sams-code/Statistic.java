package com.sams;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a player or team statistic (e.g., goals, assists, performance metric).
 * Fully compatible with Java 7.
 */
public class Statistic implements Serializable {

    private static final long serialVersionUID = 1L;

    // Thread-safe counter for generating unique IDs
    private static final AtomicInteger IDX = new AtomicInteger(1);

    private final String id;      // Auto-generated unique ID like "S1", "S2", etc.
    private final int value;     // The numeric value (e.g., number of goals)
    private final String type;   // Type of statistic (e.g., "Goal", "Assist", "Performance")
    private final String userId; // Reference to user/player ID (String for flexibility)

    /**
     * Constructor - automatically generates a unique ID.
     *
     * @param userId the ID of the user/player this statistic belongs to
     * @param value  the numeric value of the statistic
     * @param type   the type/name of the statistic
     */
    public Statistic(String userId, int value, String type) {
        this.id = "S" + IDX.getAndIncrement();
        this.userId = userId;
        this.value = value;
        this.type = type;
    }

    // Getters
    public String getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "Statistic[" + id + "] User: " + userId + " | " + type + " = " + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Statistic statistic = (Statistic) o;

        return id.equals(statistic.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
