package edu.asu.DatabasePart1;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/databaseTrial5";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "username VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "roles VARCHAR(255), "
				+ "email VARCHAR(50), "
				+ "firstName VARCHAR(20), "
				+ "middleName VARCHAR(20), "
				+ "lastName VARCHAR(20), "
				+ "oneTimePassword VARCHAR(100), "
				+")";
		statement.execute(userTable);
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	public void register(String username, String password, String[] role) throws SQLException {
		String insertUser = "INSERT INTO cse360users (username, password, roles ) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			Array sqlArray = connection.createArrayOf("text", role);

			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setArray(3, sqlArray);

			pstmt.executeUpdate();
		}
	}

	public String[] login(String username, String password) throws SQLException {
		displayUsersByAdmin();
		String query = "SELECT * FROM cse360users WHERE username = ? AND password = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				return getStringArrayFromResult(rs);
			}
			
			return null;
		
		}
	}
	
	public String[] getStringArrayFromResult(ResultSet rs) throws SQLException {
		String[] values = new String[8];
				
		for (int i = 0; i < 8; i++) {
			values[i] = rs.getString(i+1);
		}
		
		return values;
		
	}
	public String[] doesUserExist(String username) {
	    String query = "SELECT * FROM cse360users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, username);
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

	public void displayUsersByAdmin() throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String username = rs.getString("username"); 
			String password = rs.getString("password"); 
			String role = rs.getString("roles");  

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", username: " + username); 
			System.out.print(", password: " + password); 
			System.out.println(", Role: " + role); 
		} 
	}
	
	public void displayUsersByUser() throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String  email = rs.getString("email"); 
			String password = rs.getString("password"); 
			String role = rs.getString("role");  

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Age: " + email); 
			System.out.print(", First: " + password); 
			System.out.println(", Last: " + role); 
		} 
	}
	// note: updating roles
	public boolean updateAdminUser(String username,String password,String[] adminData) throws SQLException {
		String sql = "UPDATE cse360users SET email = ?, firstName = ?, middleName = ?, lastName = ? WHERE username = ? AND password = ?";
        
        // Set values for the placeholders
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			
			
	        pstmt.setString(1, adminData[0]);  // New password
	        pstmt.setString(2, adminData[1]);   // New role
	        pstmt.setString(3, adminData[2]);
	        pstmt.setString(4, adminData[3]);
	        pstmt.setString(5, username);
	        pstmt.setString(6, password);
	        
			int row = pstmt.executeUpdate();
	        return row >0;
			}
		
        // Execute the update statement

	}
	public boolean updatePassword(String username, String password, String role)throws SQLException {
		String sql = "UPDATE cse360users SET password = ? WHERE username = ? , role = ? ";
        
        // Set values for the placeholders
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			
	        pstmt.setString(1, password);  // New password
	        pstmt.setString(2, username);   // New role
	        pstmt.setString(3, role);
	        
	        //add check data statement
	        int rs = pstmt.executeUpdate();
	        return true;
		}
	}

	public void generateUserByAdmin(String username,String role,String codeDetail) throws SQLException{
		String sql = "INSERT INTO cse360users (username,role,oneTimePassword) VALUES (?,?,?)";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, username);
			pstmt.setString(2, role);
			pstmt.setString(3, codeDetail);
			pstmt.executeUpdate();
		}
	}

	public String[] getRoleArray(String username,String password) throws SQLException{
		String query = "SELECT roles FROM cse360users WHERE username = ? AND password =?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				String[] roles = rs.getString("roles").split(",");
				return  roles;
			}
			
		}
		return null;
	} 

	public String checkInvitedUser(String username) throws SQLException{
		String query = "SELECT oneTimePassword FROM cse360users WHERE username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getString("oneTimePassword");
			}
		}
		return null;
	}


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
