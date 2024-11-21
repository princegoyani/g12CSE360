package com.educationCenter.Articler_Database_Handler;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*; import java.sql.Connection; import java.sql.DriverManager;
import java.sql.SQLException; import java.sql.Statement;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


import org.h2.tools.Backup;
import org.h2.tools.Restore;
//import Encryption.EncryptionHelper;
//import Encryption.EncryptionUtils;
import com.educationCenter.Encryption.EncryptionHelper; import com.educationCenter.Encryption.EncryptionUtils;
/**
 * <p> ArticleDatabaseHelper Class </p>
 * <p> Description: This class is the helper for the ArticleDatabase class,
 *        here database functions are implemented, which can be called from ArticleDatabase.</p>
 * @author ,
 * @version 1.00		2024-10-14 Original version provided as CSE360 class materials.
 * @version 2.00		2024-10-20 Updated function as a database as articles, instead of a user login database.
 */
	/**********
	 * ArticleDatabaseHelper includes helper functions which allow direct interaction with the database.
	 *
	 * ArticleDatabaseHelper() 			: initializes db connection variables.
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
class ArticleDatabaseHelper {
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_NAME = "articleDatabase";
	//static final String DB_URL = "jdbc:h2:~/articleDB/" + DB_NAME;
	//static final String DB_URL = "jdbc:h2:~/" + DB_NAME;
	static final String DB_URL = "jdbc:h2:file:./database/" + DB_NAME;
	private static final String DB_ROOT;
		//static final String DB_URL = "jdbc:h2:file:database/Test1005";
	static {
		// Get the current working directory and navigate to "database" within "g12CSE360"
		Path currentPath = Paths.get("").toAbsolutePath();  // Gets the current directory path
		DB_ROOT = currentPath.resolve("database").toString() + "/";
	}

	static final String DB_BACKUP_ROOT = "jdbc:h2:file:/backups/";
	static final String USER = "sa"; static final String PASS = "";
	private Connection connection = null;
	private Statement statement = null;
	private EncryptionHelper encryptionHelper;
	public ArticleDatabaseHelper() { try {encryptionHelper = new EncryptionHelper();}catch (Exception e) {System.out.println(e);}}
	
	// connectToDatabase() allows connection with default article database file.
	public void articleConnectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); System.out.println("Connecting to Article Database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
			articleCreateTables();
			specialAccessTable();
			specialAccessGroupsTable();
			System.gc();
		} catch (ClassNotFoundException e) { System.err.println("JDBC Driver not found: " + e.getMessage()); }
	}
	// connectToDatabase(string) allows connection with a database other than default database.
	public void articleConnectToDatabase(String loadFileURL) throws SQLException {
		Connection backupConnection = null; // New connection for backup
		try {
			Class.forName(JDBC_DRIVER);
			System.out.println("Connecting to backup database...");
			backupConnection = DriverManager.getConnection(loadFileURL, USER, PASS);
			Statement backupStatement = backupConnection.createStatement();
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		} finally {
			// Close backup connection
			if (backupConnection != null && !backupConnection.isClosed()) {
				backupConnection.close();
			}
		}
	}
	private void deleteTable() throws SQLException {
		String sql = "DROP TABLE IF EXISTS cse360access";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.execute();
	}
	// createTables() creates the database correct form for article data.
	private void articleCreateTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS articleDatabase ("
				+ "id LONG AUTO_INCREMENT UNIQUE PRIMARY KEY, " + "creationID LONG, " // unique article ID thru backups
				+ "title VARCHAR(255), " + "body VARCHAR(255), "
				+ "author VARCHAR(255), " + "abstrac VARCHAR(255), " + "keywords VARCHAR(255) DEFAULT '0', "
				+ "difficulty INT DEFAULT '0', "				// 0=unassigned, 1=beginner, 2=intermediate, 3=advanced, 4=expert
				+ "grouping VARCHAR(255) DEFAULT '0', "						// " where @stringVar like '%thisstring%' "
				+ "nonSensKey VARCHAR(255) DEFAULT '0', " 		// user must have this key to view.. find this key in user: Keys
				+ "nonSensTitle VARCHAR(255) DEFAULT '0', " 	// optional, based on key
				+ "nonSensAbstrac VARCHAR(255) DEFAULT '0', " 	// optional, based on key
				+ "references VARCHAR(255))";

		statement.execute(userTable);
	}

	public void specialAccessGroupsTable() throws SQLException {
		String groupTable = "CREATE TABLE IF NOT EXISTS cse360groups ("
				+ "id LONG AUTO_INCREMENT UNIQUE PRIMARY KEY, "
				+ "groupName VARCHAR(255) ,"
				+ "groupAdmin INT)";
		statement.execute(groupTable);
	}

	public void addNewGroup(String groupName,int groupAdmin) throws SQLException {
		String sql = "INSERT INTO cse360groups (groupName, groupAdmin) VALUES (?, ?)";
		try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			preparedStatement.setString(1, groupName);
			preparedStatement.setInt(2, groupAdmin);
			preparedStatement.executeUpdate();
		};
	}

	public void DeleteGroup(String groupName) throws SQLException {
		String sql = "DELETE FROM cse360groups WHERE groupName = ?";

		try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			preparedStatement.setString(1, groupName);
			preparedStatement.executeUpdate();
		}
	}

		public void DeleteUserAcessGroup(String groupName,int userid ) throws SQLException {
		String sql;
		if (userid == 0){
			sql = "DELETE FROM cse360users WHERE groupName = ?";
		}else {
			sql = "DELETE FROM cse360groups WHERE groupName = ? AND groupAdmin = ?";
		}
			try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
				preparedStatement.setString(1, groupName);
				preparedStatement.setInt(2, userid);
				preparedStatement.executeUpdate();
			}
		}

	public int[] getGroupAdmin(String groupName) throws SQLException {
		String sql = "SELECT * FROM cse360groups WHERE groupName = ?";
		List<Integer> adminList = new ArrayList<>();
		try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			preparedStatement.setString(1, groupName);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				adminList.add(resultSet.getInt("groupAdmin"));
			}

		}

        return adminList.stream().mapToInt(i->i).toArray();
	}


	private void specialAccessTable() throws SQLException {
		String specialAccessTable = "CREATE TABLE IF NOT EXISTS cse360access ("
				+ "id LONG AUTO_INCREMENT UNIQUE PRIMARY KEY, "
				+"userId INT, "
				+ "groupName VARCHAR(255) DEFAULT '0', "
				+ "accessType VARCHAR(255) DEFAULT '0')";

		statement.execute(specialAccessTable);

	}

		public void addSpecialAccess(int userId,String groupName, String accessType) throws SQLException{
			String sql = "INSERT INTO cse360access (userId,groupName,accessType) VALUES(?,?,?)";
			try(PreparedStatement pstmt = connection.prepareStatement(sql)) {
				pstmt.setInt(1, userId);
				pstmt.setString(2, groupName);
				pstmt.setString(3, accessType);

				pstmt.executeUpdate();

			}
		}

	// backupToFile() backup current database with user-specified filename.
	public void backupToFile(String backupFilename) throws Exception {
		String backupPath = "./backups/" + backupFilename + ".zip";
		System.out.println("Attempting backup...");

		// Close connection before backup to avoid locking issues
		this.articleCloseConnection();

		try {
			Thread.sleep(5000);
			Backup.execute(backupPath, "./database/", DB_NAME, false);
			Thread.sleep(3000);
			System.out.println("Backup complete.");
		} catch (Exception e) {
			System.out.println("Backup failed: " + e);
		} finally {
			// Reconnect after backup completes
			this.articleConnectToDatabase();
		}
	}


		public void backupByGrouping(String backupFilename, String groupingIDs) throws Exception {
		backupToFile("full_backup"); // Backup to temp

		//  Delete articles
		String[] parsedGroups = groupingIDs.split("\\s+");
		if (parsedGroups.length == 0) {
			System.out.println("No groupings provided.");
			return;
		}
		String sql = "SELECT * FROM articleDatabase";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			int id = rs.getInt("id");
			String articleGroupings = rs.getString("grouping");
			boolean allPresent = true;

			// Check if article matches all groupings
			for (String userString : parsedGroups) {
				if (!articleGroupings.contains(userString)) {
					allPresent = false;
					break;
				}
			}

			if (!allPresent) {
				deleteArticleByKey(id);
			}
		}
		backupToFile(backupFilename);
		loadFromFile("./backups/full_backup.zip");
	}
	//
		//
	// saveDatabase() save backup of current database.
	public void saveDatabase() throws SQLException {
		String backupPath = "C:\\Users\\KT_Laptop\\eclipse-workspace\\HW6\\backups\\exitSave.zip";
		this.articleCloseConnection(); Backup.execute(backupPath, DB_ROOT, DB_NAME, false);
	}
	// loadFromFile() load database from user-specified file.
	public void loadFromFile(String loadDb) throws Exception {
		System.out.println("Resetting current articles to empty before loading backup...");

		// Empty current database
		String sqlDeleteAll = "DELETE FROM articleDatabase";
		try (Statement stmt = connection.createStatement()) {
			stmt.executeUpdate(sqlDeleteAll);
			System.out.println("Current database cleared.");
		}

		// Close connection before restore to avoid locking issues
		this.articleCloseConnection();

		try {
			Thread.sleep(5000);
			Restore.execute(loadDb, DB_ROOT, DB_NAME);
			Thread.sleep(3000);
			System.out.println("Restore complete.");
		} catch (Exception e) {
			System.err.println("Error while loading from file: " + e.getMessage());
		} finally {
			// Reconnect after restore completes
			this.articleConnectToDatabase();
		}

		displayArticles();
	}

		//
	//
	private String extractBackup(String zipFilePath) throws IOException {
		String tempDir = Paths.get("").toAbsolutePath() + "/backups/extracted";
		File dir = new File(tempDir);
		if (!dir.exists()) {
			dir.mkdirs(); // Create directory if it does not exist
		}
		deleteAllFiles(dir.getAbsolutePath());
		//deleteAllFiles(tempDir);

		try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)))) {
			ZipEntry zipEntry;
			while ((zipEntry = zis.getNextEntry()) != null) {
				File newFile = new File(tempDir, zipEntry.getName());
				if (zipEntry.isDirectory()) {
					newFile.mkdirs();
				} else {
					// parent directories created
					new File(newFile.getParent()).mkdirs();
					Files.copy(zis, newFile.toPath());
				}
				zis.closeEntry();
			}
		}
		return tempDir + "/" + DB_NAME ;
	}

	public static void deleteAllFiles(String folderPath) throws IOException {
		Path directory = Paths.get(folderPath);

		// Single command to delete all files and directories in the folder
		Files.walk(directory)
				.sorted(Comparator.reverseOrder()) // Sort in reverse order to delete files before directories
				.forEach(path -> {
					try {
						Files.deleteIfExists(path);
						System.out.println("Deleted: " + path);
					} catch (IOException e) {
						System.err.println("Failed to delete: " + path + " due to " + e.getMessage());
					}
				});
	}
	//
	//
	// loadFromFileMerge() merges database from user-specified file.
	public void loadFromFileMerge(String loadDb) throws Exception {
		// Extract the backup zip file
		String extractedDbPath = extractBackup(loadDb); // Extract to a temporary path

		// Connect to the backup database
		String tempDbPath = "jdbc:h2:file:" + extractedDbPath;
		Connection backupConnection = null;
		try {
			Class.forName("org.h2.Driver");
			backupConnection = DriverManager.getConnection(tempDbPath, USER, PASS);
			String sql = "SELECT * FROM articleDatabase";

			try (Statement stmt = backupConnection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
				// Iterate through backup articles
				while (rs.next()) {
					long creationID = rs.getLong("creationID");
					String title = rs.getString("title");
					String body = rs.getString("body");
					String author = rs.getString("author");
					String abstrac = rs.getString("abstrac");
					String keywords = rs.getString("keywords");
					String references = rs.getString("references");
					int difficulty = rs.getInt("difficulty");
					String grouping = rs.getString("grouping");
					String nonSensTitle = rs.getString("nonSensTitle");
					String nonSensAbstrac = rs.getString("nonSensAbstrac");
					String nonSensKey = rs.getString("nonSensKey");

					// Check if the article already exists in the current database
					String checkSql = "SELECT COUNT(*) FROM articleDatabase WHERE creationID = ?";
					try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
						checkStmt.setLong(1, creationID);
						ResultSet countResult = checkStmt.executeQuery();
						countResult.next();
						int count = countResult.getInt(1);

						// If article does not exist, insert it
						if (count == 0) {
							createMergedArticle(title, body, author, abstrac, keywords, references, String.valueOf(difficulty), grouping, nonSensTitle, nonSensAbstrac, nonSensKey);
							System.out.printf("Article %d merged.\n", creationID);
						} else {
							System.out.printf("Article %d already exists, skip merge.\n", creationID);
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Ensure backup connection is closed
			if (backupConnection != null && !backupConnection.isClosed()) {
				backupConnection.close();
			}
		}
	}
	// isDatabaseEmpty() returns a boolean signifying whether the database is empty or not.
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360articles";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// createArticle - not sensitive - sensitive fields blank
	public void createArticle(String title, String body, String author, String abstrac, String keywords, String references, String difficulty, String grouping) throws Exception {
		// User input for each field is encrypted using EncryptionHelper.
		int difficultyLevel;
		if ("Beginner".equals(difficulty)) {
			difficultyLevel = 1;
		} else if ("Intermediate".equals(difficulty)) {
			difficultyLevel = 2;
		} else if ("Advanced".equals(difficulty)) {
			difficultyLevel = 3;
		} else if ("Expert".equals(difficulty)) {
			difficultyLevel = 4;
		} else {
			difficultyLevel = 0; // Undefined or invalid difficulty
		}

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
		String insertUser = "INSERT INTO articleDatabase (title, body, author, abstrac, keywords, references,difficulty, grouping) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, encryptedTitle); pstmt.setString(2, encryptedBody);
			pstmt.setString(3, encryptedAuthor); pstmt.setString(4, encryptedAbstrac);
			pstmt.setString(5, encryptedKeywords); pstmt.setString(6, encryptedReferences);
			pstmt.setInt(7, difficultyLevel ); // DIFFICULTY LEVEL INT: 1-4
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
					String updateCreationID = "UPDATE articleDatabase SET creationID = ? WHERE id = ?";
					try (PreparedStatement updateStmt = connection.prepareStatement(updateCreationID)) {
						updateStmt.setLong(1, uniqueId); updateStmt.setLong(2, autoIncrementedId);
						updateStmt.executeUpdate();
					}
				} else { throw new SQLException("No ID obtained.");	}
			}
		} catch (SQLException e) { e.printStackTrace(); }
	}
		public void createMergedArticle(String title, String body, String author, String abstrac, String keywords, String references, String difficulty, String grouping, String nonSensTitle, String nonSensAbstrac, String nonSensKey) throws Exception {
			// This method assumes the data passed is already encrypted where necessary.
			String insertUser = "INSERT INTO articleDatabase (title, body, author, abstrac, keywords, references, difficulty, grouping, nonSensTitle, nonSensAbstrac, nonSensKey) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
				pstmt.setString(1, title); //
				pstmt.setString(2, body);  //
				pstmt.setString(3, author); //
				pstmt.setString(4, abstrac); //
				pstmt.setString(5, keywords); //
				pstmt.setString(6, references); //
				pstmt.setInt(7, Integer.parseInt(difficulty)); //
				pstmt.setString(8, grouping); //
				pstmt.setString(9, nonSensTitle); //
				pstmt.setString(10, nonSensAbstrac); //
				pstmt.setString(11, nonSensKey); //
				// Execute insert
				int affectedRows = pstmt.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Inserting article failed, no rows affected.");
				}
				// Retrieve generated keys
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						long autoIncrementedId = generatedKeys.getLong(1);
						// Create uniqueID
						long hashValue = DB_NAME.hashCode();
						long uniqueId = (hashValue & 0xFFFFFFFFL) + autoIncrementedId;
						// Update creationID field for new article
						String updateCreationID = "UPDATE articleDatabase SET creationID = ? WHERE id = ?";
						try (PreparedStatement updateStmt = connection.prepareStatement(updateCreationID)) {
							updateStmt.setLong(1, uniqueId);
							updateStmt.setLong(2, autoIncrementedId);
							updateStmt.executeUpdate();
						}
					} else {
						throw new SQLException("No ID obtained.");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	// createArticle - sensitive - sensitive fields populated
	public void createArticle(String title, String body, String author, String abstrac, String keywords, String references, String difficulty, String grouping, String nonSensTitle, String nonSensAbstrac, String sensKey) throws Exception {
		int difficultyLevel;
		if ("Beginner".equals(difficulty)) {
			difficultyLevel = 1;
		} else if ("Intermediate".equals(difficulty)) {
			difficultyLevel = 2;
		} else if ("Advanced".equals(difficulty)) {
			difficultyLevel = 3;
		} else if ("Expert".equals(difficulty)) {
			difficultyLevel = 4;
		} else {
			difficultyLevel = 0; // Undefined or invalid difficulty
		}
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
		String insertUser = "INSERT INTO articleDatabase (title, body, author, abstrac, keywords, references, difficulty, grouping, nonSensTitle, nonSensAbstrac, nonSensKey) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, encryptedTitle); pstmt.setString(2, encryptedBody);
			pstmt.setString(3, encryptedAuthor); pstmt.setString(4, encryptedAbstrac);
			pstmt.setString(5, encryptedKeywords); pstmt.setString(6, encryptedReferences);
			pstmt.setInt(7, difficultyLevel ); // set difficulty
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
					String updateCreationID = "UPDATE articleDatabase SET creationID = ? WHERE id = ?";
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
		String sql = "SELECT * FROM articleDatabase";
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


	public String[][] returnListArticles() throws Exception{
			if (isDatabaseEmpty() == true) { System.out.println("There are no articles in the database."); return null; }
			String sql = "SELECT * FROM articleDatabase";
			Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql);
			List<String[]> ArticleList = new ArrayList<>();

			while(rs.next()) {
				String group = rs.getString("grouping");

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
				String title;
				String author;
				String abstrac;
				String accessKey;
				//
				if ( !rs.getString("nonSensKey").equals("0") ) { // if sensitive
					System.out.print(". Sensitive?: Yes");
					title= rs.getString("nonSensTitle");
					System.out.printf(". Non-Sensitive Title: %s", title );
					author = new String(decryptedAuthor);
					System.out.print(". Author: "); EncryptionUtils.printCharArray(decryptedAuthor);
					abstrac = rs.getString("nonSensAbstrac");
					System.out.printf(". Non-Sens Abstract: %s",  abstrac);
					accessKey =rs.getString("nonSensKey");
					System.out.printf(". Non-Sensitive Access-Key: %s", accessKey );
				} else {
					System.out.print(". Sensitive?:  No");
					title = new String(decryptedTitle);
					System.out.print(". Title: " + title);
					author = new String(decryptedAuthor);
					System.out.print(". Author: " + author);
					char[] decryptedAbstrac = EncryptionUtils.toCharArray(
							encryptionHelper.decrypt( Base64.getDecoder().decode( rs.getString("abstrac") ),
									EncryptionUtils.getInitializationVector("abstrac".toCharArray()) ) );
					abstrac = new String(decryptedAuthor);
					System.out.print(". Abstract: "); EncryptionUtils.printCharArray(decryptedAbstrac);
					accessKey="";
				}
				char[] decryptedKeywords = EncryptionUtils.toCharArray(
						encryptionHelper.decrypt( Base64.getDecoder().decode( rs.getString("keywords") ),
								EncryptionUtils.getInitializationVector("keywords".toCharArray()) ) );
				char[] decryptedReferences = EncryptionUtils.toCharArray(
						encryptionHelper.decrypt( Base64.getDecoder().decode( rs.getString("references") ),
									EncryptionUtils.getInitializationVector("references".toCharArray()) ) );

				String keywords = new String(decryptedKeywords);
				String refs = new String(decryptedReferences);
				String grouping = rs.getString("grouping");
				System.out.print(". Keywords: "); EncryptionUtils.printCharArray(decryptedKeywords);
				System.out.print(". References: "); EncryptionUtils.printCharArray(decryptedReferences);
				System.out.printf(". Groupings: %s.", rs.getString("grouping")); //EncryptionUtils.printCharArray(decryptedReferences);
				System.out.print("\n");
				String[] data = {Integer.toString(id),title,author,abstrac,difficulty,accessKey,keywords,refs,grouping};
				ArticleList.add(data);

//			Arrays.fill(decryptedTitle, '0'); Arrays.fill(decryptedAuthor, '0');
//			Arrays.fill(decryptedKeywords, '0'); Arrays.fill(decryptedReferences, '0');
			}
			String[][] articles = new String[ArticleList.size()][];
			articles = ArticleList.toArray(articles);

			return articles;
		}

		public String[][] returnListArticles(String group) throws Exception{
			if (isDatabaseEmpty() == true) { System.out.println("There are no articles in the database."); return null; }
			String sql = "SELECT * FROM articleDatabase";
			Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql);
			List<String[]> ArticleList = new ArrayList<>();

			while(rs.next() && (rs.getString("grouping").equals(group)) ) {

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
				String title;
				String author;
				String abstrac;
				String accessKey;
				//
				if ( !rs.getString("nonSensKey").equals("0") ) { // if sensitive
					System.out.print(". Sensitive?: Yes");
					title= rs.getString("nonSensTitle");
					System.out.printf(". Non-Sensitive Title: %s", title );
					author = new String(decryptedAuthor);
					System.out.print(". Author: "); EncryptionUtils.printCharArray(decryptedAuthor);
					abstrac = rs.getString("nonSensAbstrac");
					System.out.printf(". Non-Sens Abstract: %s",  abstrac);
					accessKey =rs.getString("nonSensKey");
					System.out.printf(". Non-Sensitive Access-Key: %s", accessKey );
				} else {
					System.out.print(". Sensitive?:  No");
					title = new String(decryptedTitle);
					System.out.print(". Title: " + title);
					author = new String(decryptedAuthor);
					System.out.print(". Author: " + author);
					char[] decryptedAbstrac = EncryptionUtils.toCharArray(
							encryptionHelper.decrypt( Base64.getDecoder().decode( rs.getString("abstrac") ),
									EncryptionUtils.getInitializationVector("abstrac".toCharArray()) ) );
					abstrac = new String(decryptedAuthor);
					System.out.print(". Abstract: "); EncryptionUtils.printCharArray(decryptedAbstrac);
					accessKey="";
				}
				char[] decryptedKeywords = EncryptionUtils.toCharArray(
						encryptionHelper.decrypt( Base64.getDecoder().decode( rs.getString("keywords") ),
								EncryptionUtils.getInitializationVector("keywords".toCharArray()) ) );
				char[] decryptedReferences = EncryptionUtils.toCharArray(
						encryptionHelper.decrypt( Base64.getDecoder().decode( rs.getString("references") ),
								EncryptionUtils.getInitializationVector("references".toCharArray()) ) );

				String keywords = new String(decryptedKeywords);
				String refs = new String(decryptedReferences);
				String grouping = rs.getString("grouping");
				System.out.print(". Keywords: "); EncryptionUtils.printCharArray(decryptedKeywords);
				System.out.print(". References: "); EncryptionUtils.printCharArray(decryptedReferences);
				System.out.printf(". Groupings: %s.", rs.getString("grouping")); //EncryptionUtils.printCharArray(decryptedReferences);
				System.out.print("\n");
				String[] data = {Integer.toString(id),title,author,abstrac,difficulty,accessKey,keywords,refs,grouping};
				ArticleList.add(data);

//			Arrays.fill(decryptedTitle, '0'); Arrays.fill(decryptedAuthor, '0');
//			Arrays.fill(decryptedKeywords, '0'); Arrays.fill(decryptedReferences, '0');
			}
			String[][] articles = new String[ArticleList.size()][];
			articles = ArticleList.toArray(articles);

			return articles;
		}
	// displayArticleByKey() takes an article's sequence number to display the full article data.
	public void displayArticleByKey(int key) throws Exception{
		String sql = "SELECT * FROM articleDatabase";
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
	public String[] returnArticle(int userId, int key) throws Exception{
			String sql = "SELECT * FROM articleDatabase";
			Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql);
			Boolean articleFound = false;
			while( rs.next() ) {
				int id  = rs.getInt("id");
				if (id == key) {
					// display "unique header"

					articleFound = true;
					String encryptedTitle = rs.getString("title"); String encryptedAuthor = rs.getString("author");
					String grouping = rs.getString("grouping");
					String body = "";
					if (getGroupAdmin(grouping).length != 0 && !Arrays.asList(returnGroupFromUser(userId)).contains(grouping) ) {
						body = "Body cannot be shown." ;
					}else{
						String encryptedBody = rs.getString("body");
						char[] decryptedBody = EncryptionUtils.toCharArray(
								encryptionHelper.decrypt( Base64.getDecoder().decode( encryptedBody ),
										EncryptionUtils.getInitializationVector("body".toCharArray()) ) );
						body = new String(decryptedBody);
					}

					String encryptedAbstrac = rs.getString("abstrac");

					String encryptedKeywords = rs.getString("keywords");
					String encryptedReferences = rs.getString("references");
					// Decryption occurs before displaying data to user.

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
					System.out.print(".\nBody: ");
					System.out.print(".\nReferences: "); EncryptionUtils.printCharArray(decryptedReferences);
					System.out.printf(".\nEnd Article %d Data.\n", id);
					String idS = Integer.toString(id);
					String creationId = Long.toString(rs.getLong("creationID"));
					String title = new String(decryptedTitle);
					String author = new String(decryptedAuthor);
					String abstrac = new String(decryptedAbstrac);
					String keywords = new String(decryptedKeywords);

					String references = new String(decryptedReferences);

					String[] data =  {idS, creationId, title, body, difficulty, author, abstrac, keywords, references,grouping };
					return data;
				}
			} if (articleFound == false) System.out.println("No article was found with that ID.");
			return null;
		}
	//
	public String[] returnArticlesInGroup(String GroupName){
		String sql = "SELECT id FROM articleDatabase WHERE grouping=?";

		try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			preparedStatement.setString(1, GroupName);
			ResultSet rs = preparedStatement.executeQuery();
			List<String> usersList = new ArrayList<>();
			if (rs.next()){
				usersList.add(rs.getString("id"));
			}
			return usersList.toArray(new String[usersList.size()]);
		} catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		return null;
    }

	public String[] returnGroupFromUser(int userid){
		String sql = "SELECT * FROM cse360access WHERE userId=?";
		List<String> usersList = new ArrayList<>();
		try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			preparedStatement.setInt(1, userid);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()){
				String groupName = rs.getString("groupName");
					if (!(usersList.contains(groupName))) {
						usersList.add(groupName);
				}
			}
		} catch (Exception e) {
            System.out.println(e.getMessage());
        }
			return usersList.toArray(new String[0]);

        }


		public void deleteSpecialAccess(int userId,String groupName) throws SQLException{
			String sql = "DELETE FROM cse360access WHERE userId = ? AND groupName = ?";
			try(PreparedStatement pstmt = connection.prepareStatement(sql)) {
				pstmt.setInt(1, userId);
				pstmt.setString(2, groupName);

				pstmt.executeUpdate();
			}
		}

		public ArrayList<String> displayAllUniqueGroups() throws Exception{
			String sql = "SELECT grouping FROM cse360articles";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<String> results = new ArrayList<String>();

			while(rs.next()){
				results.addAll(List.of(rs.getString("grouping").split("\\s+")));
			}

			// Create a new LinkedHashSet
			Set<String> set = new LinkedHashSet<>();

			// Add the elements to set
			set.addAll(results);

			// Clear the list
			results.clear();

			// add the elements of set
			// with no duplicates to the list
			results.addAll(set);

			// return the list
			System.out.println("Unique Groups: " + (results));
			return results;

		}
        public void displayArticlesByGrouping(String grouping) throws Exception {
		String sql = "SELECT * FROM articleDatabase";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		// User Input Of Groups Into Individual Groups
		String[] parsedGroups = grouping.split("\\s+");
		// Go Through Each Article And Check, If It Matches All Groups, Display
		while( rs.next() ) {
			int id  = rs.getInt("id");
			String articleGroupings = rs.getString("grouping");
			boolean allPresent = true;
			//array = Arrays.stream(array).distinct().toArray(String[]::new);
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
			String sql = "SELECT * FROM articleDatabase";
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
		String sql = "SELECT * FROM articleDatabase";
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
		String sql = "SELECT * FROM articleDatabase";
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
		String sql = "SELECT * FROM articleDatabase"; String sqlDel = "DELETE FROM articleDatabase WHERE id = ?";
		Statement stmt = connection.createStatement(); PreparedStatement stmtDel = connection.prepareStatement(sqlDel);
		ResultSet rs = stmt.executeQuery(sql);
		stmtDel.setInt(1, key); stmtDel.executeUpdate(); System.out.println("Article deleted.");
	}

	// editArticleByKey() EDIT ARTICLE BY KEY (admin + instructor function)
	public void editArticleByKey(int key, String newTitle, String newBody, String level, String author, String keywords, String grouping, String links) throws Exception {
		String selectSql = "SELECT title,body FROM cse360articles WHERE id = ?";
		boolean articleFound = false;
		try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
			selectStmt.setInt(1, key);
			ResultSet rs = selectStmt.executeQuery();
			if (rs.next()) {
				articleFound = true;
				System.out.println("Article found.");
				String updateSql = "UPDATE cse360articles SET title =? , body = ?, author=?, keywords=?, references=?,difficulty=?, grouping=? WHERE id = ?";

				String encryptedBody = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(newBody.getBytes(), EncryptionUtils.getInitializationVector("body".toCharArray())) );
				String encryptedTitle = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(newTitle.getBytes(), EncryptionUtils.getInitializationVector("title".toCharArray())) );

				String encryptedAuthor = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(author.getBytes(), EncryptionUtils.getInitializationVector("author".toCharArray())) );
				String encryptedKeywords = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(keywords.getBytes(), EncryptionUtils.getInitializationVector("keywords".toCharArray())) );
				String encryptedReferences = Base64.getEncoder().encodeToString(
						encryptionHelper.encrypt(links.getBytes(), EncryptionUtils.getInitializationVector("references".toCharArray())) );

				int difficultyLevel;
				if ("Beginner".equals(level)) {
					difficultyLevel = 1;
				} else if ("Intermediate".equals(level)) {
					difficultyLevel = 2;
				} else if ("Advanced".equals(level)) {
					difficultyLevel = 3;
				} else if ("Expert".equals(level)) {
					difficultyLevel = 4;
				} else {
					difficultyLevel = 0; // Undefined or invalid difficulty
				}


				PreparedStatement pstmt = connection.prepareStatement(updateSql);

					pstmt.setString(1, encryptedTitle);
					pstmt.setString(2, encryptedBody);
					pstmt.setString(3, encryptedAuthor);
					pstmt.setString(4, encryptedKeywords);
					pstmt.setString(5, encryptedReferences);
					pstmt.setInt(6, difficultyLevel);
					pstmt.setString(7, grouping);

					pstmt.setInt(8, key); // Article ID
					int rowsAffected = pstmt.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Article with ID " + key + " has been updated successfully.");
					} else {
						System.out.println("No article was updated.");
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
		String selectSql = "SELECT * FROM articleDatabase WHERE id = ?";
		boolean articleFound = false;
		try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
			selectStmt.setInt(1, key);
			ResultSet rs = selectStmt.executeQuery();
			if (rs.next()) {
				articleFound = true;
				String updateSql = "UPDATE articleDatabase SET title = ?, body = ?, author = ?, abstrac = ?, keywords = ?, references = ?, difficulty = ?, grouping = ?, nonSensKey = ?, nonSensTitle = ?, nonSensAbstrac = ? WHERE id = ?";

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
		String selectSql = "SELECT * FROM articleDatabase WHERE id = ?";
		boolean articleFound = false;

		try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
			selectStmt.setInt(1, key);
			ResultSet rs = selectStmt.executeQuery();

			if (rs.next()) {
				articleFound = true;

				// Prepare the SQL update statement
				String updateSql = "UPDATE articleDatabase SET title = ?, body = ?, author = ?, abstrac = ?, keywords = ?, grouping = ? WHERE id = ?";

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
public void articleCloseConnection() {
	try {
		if (statement != null && !statement.isClosed()) {
			this.statement.close();
			System.out.println("Article closed.");
		}
		if (connection != null && !connection.isClosed()) {
			this.connection.close();
			System.out.println("Database Article closed.");
		}
		connection = null;
		statement = null;
		System.gc();
	} catch (Exception se) {
		se.printStackTrace();
	}


}


	}