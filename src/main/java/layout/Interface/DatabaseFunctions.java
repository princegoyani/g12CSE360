package layout.Interface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseFunctions {
    
    private static Connection connection;

    public static void connectToDatabase() throws SQLException {
        // Database connection logic
        String url = "jdbc:sqlite:help_system.db";
        connection = DriverManager.getConnection(url);
        System.out.println("Connected to database.");
    }

    public static void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
            System.out.println("Connection closed.");
        }
    }

    // Add functions for CRUD operations
}
