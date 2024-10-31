package com.educationCenter.Articler_Database_Handler;
import java.sql.*; import java.sql.Connection; import java.sql.DriverManager;
import java.sql.SQLException; import java.sql.Statement; import java.util.Base64;

import org.h2.tools.Backup;
import org.h2.tools.Restore;
//import Encryption.EncryptionHelper;
//import Encryption.EncryptionUtils;
import com.educationCenter.Encryption.EncryptionHelper; import com.educationCenter.Encryption.EncryptionUtils;
/**
 * <p> DatabaseHelper Class </p>
 * <p> Description: This class is the helper for the ArticleDatabase class,
 *        here database functions are implemented, which can be called from ArticleDatabase.</p>
 * @author ,
 * @version 1.00		2024-10-14 Original version provided as CSE360 class materials.
 * @version 2.00		2024-10-20 Updated function as a database as articles, instead of a user login database.
 */
	/**********
	 * DatabaseHelper includes helper functions which allow direct interaction with the database.
	 *
	 * DatabaseHelper() 			: initializes db connection variables.
	 * connectToDatabase() 			: attempts to connect to db.
	 * connectToDatabase(String) 	: attempts to connect to user-defined db.
	 * createTables()				: sets data format for db.
	 * closeConnection()			:
	 *
	 * backupToFile(String)			: saves copy of current database to user-specified file (.zip).
	 * saveDatabase()				:
	 * loadFromFile(String)			:
	 * isDatabaseEmpty()			:
	 *
	 * createArticle()				: default with no sensitive information.
	 * createArticle() extended		: for when a sensitive key/title/abs is used.
	 */
class DatabaseHelper {
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_NAME = "Test2002";
	//static final String DB_URL = "jdbc:h2:~/articleDB/" + DB_NAME;
	//static final String DB_URL = "jdbc:h2:~/" + DB_NAME;
	static final String DB_URL = "jdbc:h2:file:./database/" + DB_NAME;
	//static final String DB_URL = "jdbc:h2:file:database/Test1005";
	static final String DB_ROOT = "./database/";
	static final String DB_BACKUP_ROOT = "\\backups";
	static final String USER = "sa"; static final String PASS = "";
	private Connection connection = null;
	private Statement statement = null;
	private EncryptionHelper encryptionHelper;
	public DatabaseHelper() { try {encryptionHelper = new EncryptionHelper();}catch (Exception e) {System.out.println(e);}}
	
	// connectToDatabase() allows connection with default article database file.
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); System.out.println("Connecting to Article Database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS); statement = connection.createStatement();
			createTables();
		} catch (ClassNotFoundException e) { System.err.println("JDBC Driver not found: " + e.getMessage()); }
	}
	// connectToDatabase(string) allows connection with a database other than default database.
	public void connectToDatabase(String loadFileURL) throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(loadFileURL, USER, PASS); statement = connection.createStatement();
			createTables();
		} catch (ClassNotFoundException e) { System.err.println("JDBC Driver not found: " + e.getMessage()); }
	}
	// createTables() creates the database correct form for article data.
	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360articles ("
				+ "id LONG AUTO_INCREMENT UNIQUE PRIMARY KEY, " + "creationID LONG, " // unique article ID thru backups
				+ "title VARCHAR(255), " + "body VARCHAR(255), "
				+ "author VARCHAR(255), " + "abstrac VARCHAR(255), " + "keywords VARCHAR(255) DEFAULT '0', "
				+ "difficulty INT DEFAULT '0', "				// 0=unassigned, 1=beginner, 2=intermediate, 3=advanced, 4=expert
				+ "grouping VARCHAR(255) DEFAULT '0', "						// " where @stringVar like '%thisstring%' "
				+ "nonSensKey VARCHAR(255) DEFAULT '0', " 		// user must have this key to view.. find this key in user: Keys
				+ "nonSensTitle VARCHAR(255) DEFAULT '0', " 	// optional, based on key
				+ "nonSensAbstrac VARCHAR(255) DEFAULT '0', " 	// optional, based on key
				+ "references VARCHAR(255))";
				// header = difficulty, grouping, restricted(?)
				// user can have VARCHAR with their sensitive-keys space separated
				//    can take value from article and try to find in user's account: sensitive-keys space
				//
				// backup: can be all, or ones in a group.
				//    can also: when restore command issued, option to remove all existing articles
				//    or merge backed-up copies with current articles (when ID matches, backed-up copy not added).
				//
				// if sensitive-keys *matches* then display normal title, otherwise display sensitive-title/des
		statement.execute(userTable);
	}

	// backupToFile() backup current database with user-specified filename.
	public void backupToFile(String backupFilename) throws SQLException {
		System.out.println("Attempting backup...");
		//String backupPath = "C:\\Users\\KT_Laptop\\eclipse-workspace\\HW6\\backups\\" + backupFilename + ".zip";
		String backupPath = "C:\\IntelliJ\\g12CSE360-main\\backups\\" + backupFilename + ".zip";
		closeConnection(); Backup.execute(backupPath, DB_ROOT, DB_NAME, false); connectToDatabase(); System.out.println("Backup completed.");
	}
	//
	public void backupToFile(String backupFilename, String groupingIDs) throws SQLException {

		// CREATE NEW DATABASE FILE, CONNECT TO IT, AND CREATE THAT NEW DATABASE ONLY WITH ENTRIES THAT PASS TEST

		System.out.println("Attempting backup...");
		String backupPath = "C:\\Users\\KT_Laptop\\eclipse-workspace\\HW6\\backups\\" + backupFilename + ".zip";
		closeConnection(); Backup.execute(backupPath, DB_ROOT, DB_NAME, false); connectToDatabase(); System.out.println("Backup completed.");
	}
	//
		//
	// saveDatabase() save backup of current database.
	public void saveDatabase() throws SQLException {
		String backupPath = "C:\\Users\\KT_Laptop\\eclipse-workspace\\HW6\\backups\\exitSave.zip";
		closeConnection(); Backup.execute(backupPath, DB_ROOT, DB_NAME, false);
	}
	// loadFromFile() load database from user-specified file.
	public void loadFromFile(String loadDb) throws Exception {
		String sql = "SELECT * FROM cse360articles"; Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql);
		System.out.println("Resetting current articles to empty before loading backup:"); // Before loading new database, current database set to empty.
		while( rs.next() ) {
			int id  = rs.getInt("id"); String sqlDel = "DELETE FROM cse360articles WHERE id = ?";
			PreparedStatement stmtDel = connection.prepareStatement(sqlDel);
			stmtDel.setInt(1, id); stmtDel.executeUpdate();
			System.out.printf("Article %2d set to empty.\n", id);
		} // Current database confirmed as empty, then new database will be loaded.
		displayArticles(); closeConnection(); Restore.execute(loadDb, DB_ROOT, DB_NAME);
		System.out.println("Existing database disconnected, backup database loaded from file."); connectToDatabase();
	}

	// isDatabaseEmpty() returns a boolean signifying whether the database is empty or not.
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360articles"; ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) { return resultSet.getInt("count") == 0; } else return true;
	}

	// createArticle - not sensitive - sensitive fields blank
	public void createArticle(String title, String body, String author, String abstrac, String keywords, String references, String difficulty, String grouping) throws Exception {
		// User input for each field is encrypted using EncryptionHelper.
		String encryptedBody = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(body.getBytes(), EncryptionUtils.getInitializationVector("body".toCharArray())) );
		String encryptedTitle = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(title.getBytes(), EncryptionUtils.getInitializationVector("title".toCharArray())) );
		String encryptedAuthor = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(author.getBytes(), EncryptionUtils.getInitializationVector("author".toCharArray())) );
		String encryptedAbstrac = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(abstrac.getBytes(), EncryptionUtils.getInitializationVector("abstrac".toCharArray())) );
		String encryptedKeywords = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(keywords.getBytes(), EncryptionUtils.getInitializationVector("keywords".toCharArray())) );
		String encryptedReferences = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(references.getBytes(), EncryptionUtils.getInitializationVector("references".toCharArray())) );
		// This now encrypted data is entered into the database as a new article.
		String insertUser = "INSERT INTO cse360articles (title, body, author, abstrac, keywords, references, difficulty, grouping) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, encryptedTitle); pstmt.setString(2, encryptedBody);
			pstmt.setString(3, encryptedAuthor); pstmt.setString(4, encryptedAbstrac);
			pstmt.setString(5, encryptedKeywords); pstmt.setString(6, encryptedReferences);
			pstmt.setInt(7, Integer.parseInt(difficulty) ); // DIFFICULTY LEVEL INT: 1-4
			pstmt.setString(8, grouping);
			// Execute insert
			int affectedRows = pstmt.executeUpdate();
			if (affectedRows == 0) { throw new SQLException("Inserting article failed, no rows affected.");	}

			// Retrieve generated keys
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					long autoIncrementedId = generatedKeys.getLong(1);

					// Create unique ID
					long hashValue = DB_NAME.hashCode(); // You can use a different hashing method if needed
					long uniqueId = (hashValue & 0xFFFFFFFFL) + autoIncrementedId; // Combine them

					// Update creationID for new article
					String updateCreationID = "UPDATE cse360articles SET creationID = ? WHERE id = ?";
					try (PreparedStatement updateStmt = connection.prepareStatement(updateCreationID)) {
						updateStmt.setLong(1, uniqueId); updateStmt.setLong(2, autoIncrementedId);
						updateStmt.executeUpdate();
					}
				} else { throw new SQLException("No ID obtained.");	}
			}
		} catch (SQLException e) { e.printStackTrace(); }
	}

	// createArticle - sensitive - sensitive fields populated
	public void createArticle(String title, String body, String author, String abstrac, String keywords, String references, String difficulty, String grouping, String nonSensTitle, String nonSensAbstrac, String sensKey) throws Exception {
		String encryptedBody = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(body.getBytes(), EncryptionUtils.getInitializationVector("body".toCharArray())) );
		String encryptedTitle = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(title.getBytes(), EncryptionUtils.getInitializationVector("title".toCharArray())) );
		String encryptedAuthor = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(author.getBytes(), EncryptionUtils.getInitializationVector("author".toCharArray())) );
		String encryptedAbstrac = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(abstrac.getBytes(), EncryptionUtils.getInitializationVector("abstrac".toCharArray())) );
		String encryptedKeywords = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(keywords.getBytes(), EncryptionUtils.getInitializationVector("keywords".toCharArray())) );
		String encryptedReferences = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(references.getBytes(), EncryptionUtils.getInitializationVector("references".toCharArray())) );
		String nonSensKey = sensKey;
		String encryptedNonSensTitle = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(nonSensTitle.getBytes(), EncryptionUtils.getInitializationVector("nonsenstitle".toCharArray())) );
		String encryptedNonSensAbstrac = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(nonSensAbstrac.getBytes(), EncryptionUtils.getInitializationVector("nonsensabstrac".toCharArray())));
		// This now encrypted data is entered into the database as a new article.
		String insertUser = "INSERT INTO cse360articles (title, body, author, abstrac, keywords, references, difficulty, grouping, nonSensTitle, nonSensAbstrac, nonSensKey) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, encryptedTitle); pstmt.setString(2, encryptedBody);
			pstmt.setString(3, encryptedAuthor); pstmt.setString(4, encryptedAbstrac);
			pstmt.setString(5, encryptedKeywords); pstmt.setString(6, encryptedReferences);
			pstmt.setInt(7, Integer.parseInt(difficulty) ); // set difficulty
			pstmt.setString(8, grouping); //grouping
			pstmt.setString(9, nonSensTitle); // NON-SENS FIELDS -> NOT ENCRYPTED YET
			pstmt.setString(10, nonSensAbstrac);
			pstmt.setString(11, nonSensKey);


			// Execute insert
			int affectedRows = pstmt.executeUpdate();
			if (affectedRows == 0) { throw new SQLException("Inserting article failed, no rows affected.");	}

			// Retrieve generated keys
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					long autoIncrementedId = generatedKeys.getLong(1);
					// Create uniqueID
					long hashValue = DB_NAME.hashCode();
					long uniqueId = (hashValue & 0xFFFFFFFFL) + autoIncrementedId;
					// Update creationID field new article
					String updateCreationID = "UPDATE cse360articles SET creationID = ? WHERE id = ?";
					try (PreparedStatement updateStmt = connection.prepareStatement(updateCreationID)) {
						updateStmt.setLong(1, uniqueId); updateStmt.setLong(2, autoIncrementedId);
						updateStmt.executeUpdate(); }
				} else { throw new SQLException("No ID obtained.");	}
			}
		} catch (SQLException e) { e.printStackTrace(); }
	}
	// displayArticles() displays entire article set.
	public void displayArticles() throws Exception{
		if (isDatabaseEmpty() == true) { System.out.println("There are no articles in the database."); return; }
		String sql = "SELECT * FROM cse360articles";
		Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()) {
			int id  = rs.getInt("id");
			// Decryption occurs before displaying data to the user.
			String encryptedTitle = rs.getString("title"); String encryptedAuthor = rs.getString("author");
			Long creationID = rs.getLong("creationID");

			char[] decryptedTitle = EncryptionUtils.toCharArray(
					encryptionHelper.decrypt( Base64.getDecoder().decode( encryptedTitle ),
							EncryptionUtils.getInitializationVector("title".toCharArray()) ) );
			char[] decryptedAuthor = EncryptionUtils.toCharArray(
					encryptionHelper.decrypt( Base64.getDecoder().decode( encryptedAuthor ),
							EncryptionUtils.getInitializationVector("author".toCharArray()) ) );

			// Decryption finished, data can now be displayed.
			if (id < 10) System.out.print("Sequence Number:  " + id);
				else System.out.print("Sequence Number: " + id);
			//
			System.out.printf(". UniqueID: %d", creationID);
			String difficulty = "undefined";
			if (rs.getInt("difficulty") < 1 || rs.getInt("difficulty") > 4) {
				difficulty = "Undefined";
			} else if (rs.getInt("difficulty") == 1) {
				difficulty = "Beginner";
			} else if (rs.getInt("difficulty") == 2) {
				difficulty = "Intermediate";
			} else if (rs.getInt("difficulty") == 3) {
				difficulty = "Advanced";
			} else if (rs.getInt("difficulty") == 4) {
				difficulty = "Expert";
			}
			System.out.printf(". Difficulty Level: %s", difficulty);
			//
			if ( !rs.getString("nonSensKey").equals("0") ) { // if sensitive
				System.out.print(". Sensitive?: Yes");
				System.out.printf(". Non-Sensitive Title: %s", rs.getString("nonSensTitle") );
				System.out.print(". Author: "); EncryptionUtils.printCharArray(decryptedAuthor);
				System.out.printf(". Non-Sens Abstract: %s", rs.getString("nonSensAbstrac") );
				System.out.printf(". Non-Sensitive Access-Key: %s", rs.getString("nonSensKey") );
			} else {
				System.out.print(". Sensitive?:  No");
				System.out.print(". Title: "); EncryptionUtils.printCharArray(decryptedTitle);
				System.out.print(". Author: "); EncryptionUtils.printCharArray(decryptedAuthor);
				char[] decryptedAbstrac = EncryptionUtils.toCharArray(
						encryptionHelper.decrypt( Base64.getDecoder().decode( rs.getString("abstrac") ),
								EncryptionUtils.getInitializationVector("abstrac".toCharArray()) ) );
				System.out.print(". Abstract: "); EncryptionUtils.printCharArray(decryptedAbstrac);
			}
			char[] decryptedKeywords = EncryptionUtils.toCharArray(
					encryptionHelper.decrypt( Base64.getDecoder().decode( rs.getString("keywords") ),
							EncryptionUtils.getInitializationVector("keywords".toCharArray()) ) );
			char[] decryptedReferences = EncryptionUtils.toCharArray(
					encryptionHelper.decrypt( Base64.getDecoder().decode( rs.getString("references") ),
							EncryptionUtils.getInitializationVector("references".toCharArray()) ) );

			System.out.print(". Keywords: "); EncryptionUtils.printCharArray(decryptedKeywords);
			System.out.print(". References: "); EncryptionUtils.printCharArray(decryptedReferences);
			System.out.printf(". Groupings: %s.", rs.getString("grouping")); //EncryptionUtils.printCharArray(decryptedReferences);
			System.out.print("\n");

//			Arrays.fill(decryptedTitle, '0'); Arrays.fill(decryptedAuthor, '0');
//			Arrays.fill(decryptedKeywords, '0'); Arrays.fill(decryptedReferences, '0');
		}
	}	

	// displayArticleByKey() takes an article's sequence number to display the full article data.
	public void displayArticleByKey(int key) throws Exception{
		String sql = "SELECT * FROM cse360articles";
		Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql);
		Boolean articleFound = false;
		while( rs.next() ) {
			int id  = rs.getInt("id"); 
			if (id == key) {
				// display "unique header"

				articleFound = true;
				String encryptedTitle = rs.getString("title"); String encryptedAuthor = rs.getString("author");
				String encryptedBody = rs.getString("body"); String encryptedAbstrac = rs.getString("abstrac");
				String encryptedKeywords = rs.getString("keywords"); String encryptedReferences = rs.getString("references");
				// Decryption occurs before displaying data to user.
				char[] decryptedBody = EncryptionUtils.toCharArray(
						encryptionHelper.decrypt( Base64.getDecoder().decode( encryptedBody ),
								EncryptionUtils.getInitializationVector("body".toCharArray()) ) );
				char[] decryptedTitle = EncryptionUtils.toCharArray(
						encryptionHelper.decrypt( Base64.getDecoder().decode( encryptedTitle ),
								EncryptionUtils.getInitializationVector("title".toCharArray()) ) );
				char[] decryptedAuthor = EncryptionUtils.toCharArray(
						encryptionHelper.decrypt( Base64.getDecoder().decode( encryptedAuthor ),
								EncryptionUtils.getInitializationVector("author".toCharArray()) ) );
				char[] decryptedAbstrac = EncryptionUtils.toCharArray(
						encryptionHelper.decrypt( Base64.getDecoder().decode( encryptedAbstrac ),
								EncryptionUtils.getInitializationVector("abstrac".toCharArray()) ) );
				char[] decryptedKeywords = EncryptionUtils.toCharArray(
						encryptionHelper.decrypt( Base64.getDecoder().decode( encryptedKeywords ),
								EncryptionUtils.getInitializationVector("keywords".toCharArray()) ) );
				char[] decryptedReferences = EncryptionUtils.toCharArray(
						encryptionHelper.decrypt( Base64.getDecoder().decode( encryptedReferences ),
								EncryptionUtils.getInitializationVector("references".toCharArray()) ) );
				// Decryption finished, data can now be displayed.

				String difficulty = "undefined";
				if (rs.getInt("difficulty") < 1 || rs.getInt("difficulty") > 4) {
					difficulty = "Undefined";
				} else if (rs.getInt("difficulty") == 1) {
					difficulty = "Beginner";
				} else if (rs.getInt("difficulty") == 2) {
					difficulty = "Intermediate";
				} else if (rs.getInt("difficulty") == 3) {
					difficulty = "Advanced";
				} else if (rs.getInt("difficulty") == 4) {
					difficulty = "Expert";
				}

				// unique header : level, grouping IDs, other sens info
				System.out.print("Unique Header: ");
				System.out.printf("Difficulty: %d | ", rs.getInt("difficulty") );
				System.out.printf("Groupings IDs: %s | ", rs.getString("grouping"));

				if ( !rs.getString("nonSensKey").equals("0") ) { // if sensitive
					System.out.printf("Sensitive?: Yes");
					System.out.printf(", Non-Sensitive Title: %s", rs.getString("nonSensTitle") );
					System.out.printf(", Non-Sensitive Abstract: %s", rs.getString("nonSensAbstrac") );
					System.out.printf(", Non-Sensitive Access-Key: %s", rs.getString("nonSensKey") );
				} else {
					System.out.printf("Sensitive?: No");
				}

				System.out.print("\nSequence Number: " + id);
				System.out.printf(". Unique ID: %d.", rs.getLong("creationID") );
				System.out.print("\nTitle: "); EncryptionUtils.printCharArray(decryptedTitle);
				System.out.print(".\nAuthor(s): "); EncryptionUtils.printCharArray(decryptedAuthor);
				System.out.print(".\nKeywords: "); EncryptionUtils.printCharArray(decryptedKeywords);
				System.out.print(".\nAbstract: "); EncryptionUtils.printCharArray(decryptedAbstrac);
				System.out.print(".\nBody: "); EncryptionUtils.printCharArray(decryptedBody);
				System.out.print(".\nReferences: "); EncryptionUtils.printCharArray(decryptedReferences);
				System.out.printf(".\nEnd Article %d Data.\n", id);

//				// Decrypted data when no longer needed is set to blanks.
//				Arrays.fill(decryptedBody, '0'); Arrays.fill(decryptedAuthor, '0');
//				Arrays.fill(decryptedTitle, '0'); Arrays.fill(decryptedAbstrac, '0');
//				Arrays.fill(decryptedKeywords, '0'); Arrays.fill(decryptedReferences, '0');
			}
		} if (articleFound == false) System.out.println("No article was found with that ID.");
	}	
	//
	//
	public void displayArticlesByGrouping(String grouping) throws Exception {
		String sql = "SELECT * FROM cse360articles";
		Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql);
		// User Input Of Groups Into Individual Groups
		String[] parsedGroups = grouping.split("\\s+");
		// Go Through Each Article And Check, If It Matches All Groups, Display
		while( rs.next() ) {
			int id  = rs.getInt("id");
			String articleGroupings = rs.getString("grouping");
			boolean allPresent = true;

			// For Each Article Entry, Check If Each Parsed Group Is Found
			for (String userString : parsedGroups) {
				//System.out.printf("if articleGrouping contains %s \n", userString);
				if (!articleGroupings.contains(userString)) {
					allPresent = false;
					break;
				}
			}
			// Article Has Been Tested To Contain All Groups
			if (allPresent) {
				//System.out.println("articleGrouping: " + articleGroupings); //System.out.printf("Match: %d\n\n", id);
				System.out.print("\n");
				displayArticleByKey(id);
			} else {
				//System.out.println("articleGrouping: " + articleGroupings); //System.out.printf("Not Match: %d\n\n", id);
			}
		}
	}
		// MODIFIED COPY OF DISP BY GROUPINGS
		public void displayArticlesByKeywords(String grouping) throws Exception {
			String sql = "SELECT * FROM cse360articles";
			Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql);
			// User Input Of Groups Into Individual Groups
			String[] parsedGroups = grouping.split("\\s+");
			//for (int i = 0; i < parsedGroups.length; i++) {
		//		System.out.printf("%s ", parsedGroups[i]);
	//		}
			// Go Through Each Article And Check, If It Matches All Groups, Display
			while( rs.next() ) {
				int id  = rs.getInt("id");
				char[] decryptedKeywords = EncryptionUtils.toCharArray(
						encryptionHelper.decrypt( Base64.getDecoder().decode( rs.getString("keywords") ),
								EncryptionUtils.getInitializationVector("keywords".toCharArray()) ) );
				String articleGroupings = new String(decryptedKeywords);
				boolean allPresent = true;

				// For Each Article Entry, Check If Each Parsed Group Is Found
				for (String userString : parsedGroups) {
					//System.out.printf("if articleGrouping contains %s \n", userString);
					if (!articleGroupings.contains(userString)) {
						allPresent = false;
						break;
					}
				}
				// Article Has Been Tested To Contain All Groups
				if (allPresent) {
					//==System.out.println("articleGrouping: " + articleGroupings);
					//System.out.printf("Match: %d\n\n", id);
					System.out.print("\n");
					displayArticleByKey(id); //System.out.print("\n");
				} else {
					//System.out.println("articleGrouping: " + articleGroupings);
					//System.out.printf("Not Match: %d\n\n", id);
				}
			}

		}
	//
	// disparticlebykey
	public int checkArticleByKey(int key) throws Exception{
		String sql = "SELECT * FROM cse360articles";
		Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql);
		int articleFound = 0;
		while( rs.next() ) {
			int id  = rs.getInt("id");
			if (id == key) {
				articleFound = 1;
			}
		}
		return articleFound;
	}
	public int checkSensByKey(int key) throws Exception{
		String sql = "SELECT * FROM cse360articles";
		Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql);
		int articleFound = 0;
		while( rs.next() ) {
			int id  = rs.getInt("id");
			if (id == key) {
				String emptyKey = "0";
				String result = rs.getString("nonSensKey");
				if (!emptyKey.equals( rs.getString("nonSensKey") ) ) { // if key is not equal to emptyKey
					return 1;
				} else return 0;
			}
		}
		return 0;
	}



		//
	//
	// deleteArticleByKey() takes article's sequence number to locate article and perform deletion.
	public void deleteArticleByKey(int key) throws Exception{
		String sql = "SELECT * FROM cse360articles"; String sqlDel = "DELETE FROM cse360articles WHERE id = ?";
		Statement stmt = connection.createStatement(); PreparedStatement stmtDel = connection.prepareStatement(sqlDel);
		ResultSet rs = stmt.executeQuery(sql);
		stmtDel.setInt(1, key); stmtDel.executeUpdate(); System.out.println("Article deleted.");
	}

	// editArticleByKey() EDIT ARTICLE BY KEY (admin + instructor function)
	public void editArticleByKey(int key, String newTitle, String newBody, String newAuthor, String newAbstrac, String newKeywords, String newReferences, String newDifficulty, String newGrouping) throws Exception {
		String selectSql = "SELECT * FROM cse360articles WHERE id = ?";
		boolean articleFound = false;
		try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
			selectStmt.setInt(1, key);
			ResultSet rs = selectStmt.executeQuery();
			if (rs.next()) {
				articleFound = true;
				String updateSql = "UPDATE cse360articles SET title = ?, body = ?, author = ?, abstrac = ?, keywords = ?, references = ?, difficulty = ?, grouping = ?, nonSensKey = ?, nonSensTitle = ?, nonSensAbstrac = ? WHERE id = ?";

				String encryptedBody = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(newBody.getBytes(), EncryptionUtils.getInitializationVector("body".toCharArray())) );
				String encryptedTitle = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(newTitle.getBytes(), EncryptionUtils.getInitializationVector("title".toCharArray())) );
				String encryptedAuthor = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(newAuthor.getBytes(), EncryptionUtils.getInitializationVector("author".toCharArray())) );
				String encryptedAbstrac = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(newAbstrac.getBytes(), EncryptionUtils.getInitializationVector("abstrac".toCharArray())) );
				String encryptedKeywords = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(newKeywords.getBytes(), EncryptionUtils.getInitializationVector("keywords".toCharArray())) );
				String encryptedReferences = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(newReferences.getBytes(), EncryptionUtils.getInitializationVector("references".toCharArray())) );

				try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
					pstmt.setString(1, encryptedTitle);
					pstmt.setString(2, encryptedBody);
					pstmt.setString(3, encryptedAuthor);
					pstmt.setString(4, encryptedAbstrac);
					pstmt.setString(5, encryptedKeywords);
					pstmt.setString(6, encryptedReferences);
					pstmt.setString(7, newDifficulty);
					pstmt.setString(8, newGrouping);
					pstmt.setString(9, "0"); // String newNonSensKey = "0";    SET TO DEFAULTS
					pstmt.setString(10, "0"); // String newNonSensAbstrac = "0";
					pstmt.setString(11, "0"); // String newNonSensAbstrac = "0";
					pstmt.setInt(12, key); // Article ID
					int rowsAffected = pstmt.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Article with ID " + key + " has been updated successfully.");
					} else {
						System.out.println("No article was updated.");
					}
				}
			} else {
				System.out.println("No article found with ID " + key + ".");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("An error occurred while updating the article.");
		}
	}
		// editArticleByKey() EDIT ARTICLE BY KEY (admin + instructor function)
	public void editArticleByKeySens(int key, String newTitle, String newBody, String newAuthor, String newAbstrac, String newKeywords, String newReferences,  String newDifficulty, String newGrouping, String newSensKey, String newSensTitle, String newSensAbstrac) throws Exception {
		String selectSql = "SELECT * FROM cse360articles WHERE id = ?";
		boolean articleFound = false;
		try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
			selectStmt.setInt(1, key);
			ResultSet rs = selectStmt.executeQuery();
			if (rs.next()) {
				articleFound = true;
				String updateSql = "UPDATE cse360articles SET title = ?, body = ?, author = ?, abstrac = ?, keywords = ?, references = ?, difficulty = ?, grouping = ?, nonSensKey = ?, nonSensTitle = ?, nonSensAbstrac = ? WHERE id = ?";

				String encryptedBody = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(newBody.getBytes(), EncryptionUtils.getInitializationVector("body".toCharArray())) );
				String encryptedTitle = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(newTitle.getBytes(), EncryptionUtils.getInitializationVector("title".toCharArray())) );
				String encryptedAuthor = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(newAuthor.getBytes(), EncryptionUtils.getInitializationVector("author".toCharArray())) );
				String encryptedAbstrac = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(newAbstrac.getBytes(), EncryptionUtils.getInitializationVector("abstrac".toCharArray())) );
				String encryptedKeywords = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(newKeywords.getBytes(), EncryptionUtils.getInitializationVector("keywords".toCharArray())) );
				String encryptedReferences = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(newReferences.getBytes(), EncryptionUtils.getInitializationVector("references".toCharArray())) );

				try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
					pstmt.setString(1, encryptedTitle);
					pstmt.setString(2, encryptedBody);
					pstmt.setString(3, encryptedAuthor);
					pstmt.setString(4, encryptedAbstrac);
					pstmt.setString(5, encryptedKeywords);
					pstmt.setString(6, encryptedReferences);
					pstmt.setString(7, newDifficulty);
					pstmt.setString(8, newGrouping);
					pstmt.setString(9, newSensKey);
					pstmt.setString(10, newSensTitle);
					pstmt.setString(11, newSensAbstrac);
					pstmt.setInt(12, key);
					int rowsAffected = pstmt.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Article with ID " + key + " has been updated successfully.");
					} else {
						System.out.println("No article was updated.");
					}
				}
			} else {
				System.out.println("No article found with ID " + key + ".");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("An error occurred while updating the article.");
		}
	}








/*
	public void updateArticleByKey(int key, String newTitle, String newBody, String newAuthor, String newAbstract, String newKeywords, String newGrouping) throws Exception {
		// First, check if the article exists
		String selectSql = "SELECT * FROM cse360articles WHERE id = ?";
		boolean articleFound = false;

		try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
			selectStmt.setInt(1, key);
			ResultSet rs = selectStmt.executeQuery();

			if (rs.next()) {
				articleFound = true;

				// Prepare the SQL update statement
				String updateSql = "UPDATE cse360articles SET title = ?, body = ?, author = ?, abstrac = ?, keywords = ?, grouping = ? WHERE id = ?";

				try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
					// Set the parameters for the prepared statement
					pstmt.setString(1, EncryptionUtils.encrypt(newTitle)); // Encrypt the new title
					pstmt.setString(2, EncryptionUtils.encrypt(newBody));  // Encrypt the new body
					pstmt.setString(3, EncryptionUtils.encrypt(newAuthor)); // Encrypt the new author
					pstmt.setString(4, EncryptionUtils.encrypt(newAbstract)); // Encrypt the new abstract
					pstmt.setString(5, EncryptionUtils.encrypt(newKeywords)); // Encrypt the new keywords
					pstmt.setString(6, newGrouping); // Grouping does not need encryption
					pstmt.setInt(7, key); // Article ID

					// Execute the update
					int rowsAffected = pstmt.executeUpdate();

					// Check if the update was successful
					if (rowsAffected > 0) {
						System.out.println("Article with ID " + key + " has been updated successfully.");
					} else {
						System.out.println("No article was updated.");
					}
				}
			} else {
				System.out.println("No article found with ID " + key + ".");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("An error occurred while updating the article.");
		}
	}
*/
		// closeConnection() : handles disconnection of database.
	public void closeConnection() {
		try { if (statement!=null) statement.close(); } catch (SQLException se2) { se2.printStackTrace(); }
		try { if (connection!=null) connection.close(); } catch (SQLException se) { se.printStackTrace(); }
	}
}