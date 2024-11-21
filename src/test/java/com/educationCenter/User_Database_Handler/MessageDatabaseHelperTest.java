package com.educationCenter.User_Database_Handler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MessageDatabaseHelperTest {

    private MessageDatabaseHelper dbHelper;

    @BeforeEach
    void setUp() throws SQLException {
        dbHelper = new MessageDatabaseHelper();
        assertTrue(dbHelper.messageConnectToDatabase(), "Database connection should be successful.");
    }

    @AfterEach
    void tearDown() {
        dbHelper.closeConnection();
    }

    @Test
    void messageConnectToDatabase() throws SQLException {
        assertTrue(dbHelper.messageConnectToDatabase(), "Database connection should be successful.");
    }

    @Test
    void isDatabaseEmpty() throws SQLException {
        dbHelper.clearAllMessages(); // Ensure the database is empty for the test
        assertTrue(dbHelper.isDatabaseEmpty(), "Database should be empty.");
    }

    @Test
    void addMessage() throws SQLException {
        dbHelper.clearAllMessages(); // Clear the database before testing
        dbHelper.addMessage(1, 0, "Test help message", "Test search history");
        assertFalse(dbHelper.isDatabaseEmpty(), "Database should not be empty after adding a message.");
    }

    @Test
    void getUserId() throws SQLException {
        dbHelper.clearAllMessages();
        int userId = 1;
        dbHelper.addMessage(userId, 0, "User-specific message", "Search history for user");

        assertNotNull(userId, "User ID should not be null for an existing user.");
    }

    @Test
    void deleteMessage() throws SQLException {
        dbHelper.clearAllMessages();
        dbHelper.addMessage(10000, 0, "Message to delete", "Search history for deletion");
        String[] messages = dbHelper.getAllMessages()[0];
        assertFalse(dbHelper.isDatabaseEmpty(), "Database should not be empty after adding a message.");
        assertTrue(dbHelper.deleteMessage(Integer.parseInt(messages[0])), "Message should be successfully deleted.");
        assertTrue(dbHelper.isDatabaseEmpty(), "Database should be empty after deleting the message.");
    }

    @Test
    void getStringArrayFromResult() throws SQLException {
        dbHelper.clearAllMessages();

        dbHelper.addMessage(1, 0, "Message to test string array", "Search history to test string array");
        assertEquals(1,dbHelper.getAllMessages().length);
        String[][] messages = dbHelper.getAllMessages();

        assertNotNull(messages, "Messages should not be null.");
        assertEquals(1, messages.length, "There should be one message in the database.");

    }

    @Test
    void getAllMessages() throws SQLException {
        dbHelper.clearAllMessages();
        dbHelper.addMessage(1, 0, "First test message", "First search history");
        dbHelper.addMessage(2, 1, "Second test message", "Second search history");
        String[][] messages = dbHelper.getAllMessages();
        assertEquals(2, messages.length, "There should be two messages in the database.");
    }

    @Test
    void clearAllMessages() throws SQLException {
        dbHelper.addMessage(1, 0, "Message to clear", "Search history to clear");
        assertFalse(dbHelper.isDatabaseEmpty(), "Database should not be empty after adding a message.");
        dbHelper.clearAllMessages();
        assertTrue(dbHelper.isDatabaseEmpty(), "Database should be empty after clearing all messages.");
    }

    @Test
    void closeConnection() {
        assertDoesNotThrow(() -> dbHelper.closeConnection(), "Closing the connection should not throw an exception.");
    }
}
