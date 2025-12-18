package com.sams;

import java.sql.*;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("🔍 Testing SQLite database connection...");

        DBHelper db = DBHelper.getInstance();
        Connection conn = db.getConnection();

        if (conn == null) {
            System.out.println("❌ Connection failed!");
            return;
        }

        System.out.println("✅ Connection established successfully!");

    
        db.close();
        System.out.println("✅ Database test completed.");
    }
}
