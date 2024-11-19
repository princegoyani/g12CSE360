package com.educationCenter.User_Database_Handler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MessageDatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_NAME = "messageDatabase";
	static final String DB_URL = "jdbc:h2:file:./database/" + DB_NAME;
	//static final String DB_URL = "jdbc:h2:~/messageDatabase";
	//  Database credentials
	static final String USER = "sa";
	static final String PASS = "";
	private Connection connection = null;
	private Statement statement = null;
	//	PreparedStatement
	public boolean messageConnectToDatabase() throws SQLException {
		try {
			System.out.println("Checking for driver: ");
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to message database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
			messageCreateTables();  // Create the necessary tables if they don't exist
			return true;
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
			return false;
		}
	}

	private void messageCreateTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS messageDatabase ("
				+ "messageId INT AUTO_INCREMENT PRIMARY KEY, "	//unique ID of message
				+ "userID INT , "					// userID of student
				+ "messageType INT DEFAULT '0', "	// 0 is generic, 1 is specific
				+ "helpMessage VARCHAR(8000), "		// The help message
				+ "searches VARCHAR(8000)"			// The student's past searches
				+")";

		statement.execute(userTable);
	}
	public String getUserId(String username){
		String query = "SELECT id FROM messageDatabase WHERE username = ?";

		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, username);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getString("id");
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM messageDatabase";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}


	public void addMessage(int userID, int messageType, String helpMessage, String searches) throws SQLException {
		String insertMessage = "INSERT INTO messageDatabase (userID, messageType, helpMessage, searches ) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertMessage)) {

			pstmt.setInt(1, userID);
			pstmt.setInt(2, messageType);
			pstmt.setString(3, helpMessage);
			pstmt.setString(4, searches);

			pstmt.executeUpdate();
		}
	}

	public boolean deleteMessage(int ID) throws SQLException{
		String query = "DELETE FROM messageDatabase WHERE messageId = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, ID);
			int rs = stmt.executeUpdate();

			if (rs > 0) {
				return true;
			}
		}
		return false;
	}
	/*
	public boolean studentRegister(String username, String password,String email) throws SQLException {
			String sql = "UPDATE messageDatabase SET username = ?, password  = ? WHERE email = ?";

			// Set values for the placeholders
			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

				pstmt.setString(1, username);  // New password
				pstmt.setString(2, password);   // New role
				pstmt.setString(3, email);


				int row = pstmt.executeUpdate();
				return row > 0;
			}

			// Execute the update statement


	}

	 */
	/*
	public void adminRegister(String username, String password, String[] role) throws SQLException {
		String insertUser = "INSERT INTO messageDatabase (username, password, roles ) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			Array sqlArray = connection.createArrayOf("text", role);

			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setArray(3, sqlArray);

			pstmt.executeUpdate();
		}
	}

	 */
	/*
	public void resetbyemail(String username, String codeDetail) throws SQLException{
		String sql = "UPDATE messageDatabase SET oneTimePassword = ?, password  = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, codeDetail);
			pstmt.setArray(2, null);
			pstmt.setString(3, username);
			pstmt.executeUpdate();
		}
	}

	 */
	/*
	public void generateUserByAdmin(String email,String[] role,String codeDetail) throws SQLException{
		String sql = "INSERT INTO messageDatabase (email,roles,oneTimePassword) VALUES (?,?,?)";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			Array sqlArray = connection.createArrayOf("text", role);
			pstmt.setString(1, email);
			pstmt.setArray(2, sqlArray);
			pstmt.setString(3, codeDetail);
			pstmt.executeUpdate();
		}
	}

	 */
	/*
	public String[] login(String username, String password) throws SQLException {

		displayUsersByAdmin();
		String query = "SELECT * FROM messageDatabase WHERE username = ? AND password = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return getStringArrayFromResult(rs);
			}
			System.out.println("NO USER");;
			return null;

		}
	}

	 */

	public String[] getStringArrayFromResult(ResultSet rs) throws SQLException {
		String[] values = new String[8];

		for (int i = 0; i < 5; i++) {
			values[i] = rs.getString(i+1);
		}

		return values;
	}

	public String[][] getAllMessages() throws SQLException {
		String sql = "SELECT * FROM messageDatabase";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		List<String[]> messageList = new ArrayList<>();

		while (rs.next()) {
			System.out.println("Inside while rs.next()");
			// Retrieve data from ResultSet and store it in a String array
			String[] data = getStringArrayFromResult(rs);
			messageList.add(data);
		}

		// Convert the list to a 2D array
		String[][] messages = new String[messageList.size()][];
		messages = messageList.toArray(messages);

		return messages;
	}

	public void clearAllMessages() throws SQLException{
		String query = "DELETE FROM messageDatabase";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.executeUpdate();
			System.out.println("Cleared all messages");
		}

	}
	/*
	public String[] doesUserExist(String username) {
		System.out.println(username);
	    String query = "SELECT * FROM messageDatabase WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {

	        pstmt.setString(1,username);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return getStringArrayFromResult(rs);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
			return null;
	    }
	    return null; // If an error occurs, assume user doesn't exist
	}

	 */
	/*
	public String[] doesEmailExist(String email) {
		System.out.println(email);
		String query = "SELECT * FROM messageDatabase WHERE email = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {

			pstmt.setString(1,email);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				// If the count is greater than 0, the user exists
				return getStringArrayFromResult(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return null; // If an error occurs, assume user doesn't exist
	}

	 */
	/*
	public void displayUsersByAdmin() throws SQLException{
		String sql = "SELECT * FROM messageDatabase";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		while(rs.next()) {
			// Retrieve
			String[] data = getStringArrayFromResult(rs);
			System.out.print("ID: " + data[0]);
			System.out.print(", username: " + data[1]);
			System.out.print(", Role: " + data[3]);
			System.out.println(", Email: " + data[4]);
			System.out.println(", Frist Name: " + data[6]);
		}
	}

	 */

	/*
	// note: updating roles
	public boolean updateUserInformation(String username,String[] adminData) throws SQLException {
		String sql = "UPDATE messageDatabase SET firstName = ?, middleName = ?, lastName = ? WHERE username = ?";

        // Set values for the placeholders
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

	        pstmt.setString(1, adminData[0]);  // New password
	        pstmt.setString(2, adminData[1]);   // New role
	        pstmt.setString(3, adminData[2]);
	        pstmt.setString(4, username);


			int row = pstmt.executeUpdate();
	        return row > 0;
			}

        // Execute the update statement

	}

	 */
	/*
	public boolean updatePassword(String email, String password)throws SQLException {
		String sql = "UPDATE messageDatabase SET password = ?,oneTimePassword = ? WHERE email = ? ";

        // Set values for the placeholders
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

	        pstmt.setString(1, password);  // New password
	        pstmt.setString(2, null);
			pstmt.setString(3, email);   // New role

	        //add check data statement
	        int rs = pstmt.executeUpdate();
	        return true;
		}

	}

	 */
	/*
	public boolean updateRole(String username, String updateStringRole) throws SQLException{

		String sql = "UPDATE messageDatabase SET roles = ? WHERE username = ? ";

        // Set values for the placeholders
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

	        pstmt.setString(1, updateStringRole);  // New password
			pstmt.setString(2, username);   // New role

	        //add check data statement
	        int rs = pstmt.executeUpdate();
	        return true;
		}

	}

	 */
	/*
	public String[] getRoleArray(String username,String password) throws SQLException{
		String query = "SELECT roles FROM messageDatabase WHERE username = ? AND password =?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				String roles = rs.getString("roles");
				String fliter = roles.substring(1, roles.length()-1);
				System.out.println(roles);
				return fliter.split(",");
			}
		}
		return null;
	}

	 */
	/*
	public String checkInvitedUser(String email) throws SQLException{
		String query = "SELECT oneTimePassword FROM messageDatabase WHERE email = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, email);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getString("oneTimePassword");
			}
		}
		return null;
	}

	 */

	public void closeConnection() {
		try{
			if(statement!=null) statement.close();
		} catch(SQLException se2) {
			se2.printStackTrace();
		}
		try {
			if(connection!=null) connection.close();
		} catch(SQLException se){
			se.printStackTrace();
		}
	}

}
