package com.sams;

/**
 * Equipment - Represents a row from the 'equipment' table
 * Matches DB columns: EquipmentID, Name, Quantity, Status, Location
 * Java 7 compatible
 */
public class Equipment {

    private int equipmentId;
    private String name;
    private int quantity;
    private String status;
    private String location;

    /**
     * Constructor used when creating a new equipment item (ID = 0)
     */
    public Equipment(String name, int quantity, String status, String location) {
        this(0, name, quantity, status, location);
    }

    /**
     * Full constructor - used when loading from database
     */
    public Equipment(int equipmentId, String name, int quantity, String status, String location) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.quantity = quantity;
        this.status = status != null ? status : "Unknown";
        this.location = location != null ? location : "Not specified";
    }

    public Equipment(int int1, String string, String string2, int int2,
			int int3, String string3) {
		// TODO Auto-generated constructor stub
	}

	// === Getters ===
    public int getEquipmentId() {
        return equipmentId;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    // === Setters (needed for editing/updating) ===
    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Useful for logging and debugging
     */
    @Override
    public String toString() {
        return "Equipment{" +
                "equipmentId=" + equipmentId +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}

