package com.article_db.simpleDatabase; import java.sql.SQLException; import java.util.Scanner;
/**
 * <p> ArticleDatabase Class </p>
 * <p> Description: Interface to facilitate user access/requests to article database.</p>
 * @author Lynn Robert Carter, Keenan Tait
 * @version 1.00		2024-10-14 Original version provided as CSE360 class materials.
 * @version 2.00		2024-10-20 Updated function as a database as articles, instead of a user login database.
 */
	/**********
	 * ArticleDatabase allows client to choose how to interact with database.
	 * From here, DatabaseHelper calls are made to carry out database functionality.
	 *
	 * Main() 							: tries to establish db connection.
	 * callMenu(String menuChoice) 		: non-CLI interface for db functions.
	 * defaultFlow()					: CLI menu interface for db functions.
	 *
	 * Direct Access DB Calls - Helper Functions:
	 * callDisplayArticles() 				: calls displayArticles()
	 * callDisplayArticleByKey(String key) 	: calls displayArticleByKey(String key)
	 */
public class ArticleDatabase {
	public static DatabaseHelper databaseHelper; 					// Instantiate DatabaseHelper.
	private static final Scanner scanner = new Scanner(System.in);  // Instantiate Scanner.
	public static void main( String[] args ) throws Exception 		// Main Method: tries connecting to database.
	{
		databaseHelper = new DatabaseHelper(); //System.out.println("Start ArticleDatabse.\n");
		try { databaseHelper.connectToDatabase();
			// defaultFlow(); // After database is connected, default user flow will begin.
			//    ** Instead of starting CLI menu / defaultFlow(), wait for call from Help System.
		} catch (SQLException e) { System.err.println("Database error: " + e.getMessage()); e.printStackTrace(); }
		finally {
			System.out.println("Pass back to Help System..."); //System.out.println("Database closed, goodbye."); databaseHelper.closeConnection();
		}
	}

	// THIS function might not be necessary / redundant
 	// Helper Class For Menu Calls: Silent/No Output, passed menu choice arg from caller.
	public static void callMenu(String menuOption) throws Exception {
		System.out.println("Article Database Menu called from Help System."); // String menuOption = "1";
		if (Integer.parseInt(menuOption) < 0 || Integer.parseInt(menuOption) > 6 ) {
			System.out.println("MenuOption out of bounds.\n"); return;
		} else { System.out.printf("Menu Called With: %s.\n", menuOption); }
		switch (menuOption) {
			case "1": // "List All Articles"
				System.out.println("\nDisplaying all articles:"); databaseHelper.displayArticles(); break;
			case "2": // "Display Article By Sequence Number"
				System.out.print("\nDisplay article.\nEnter article sequence number to display: "); String input = scanner.nextLine(); System.out.print("\n");
				int key = Integer.parseInt(input); databaseHelper.displayArticleByKey(key); break;
			case "3": // "Delete Article By Sequence Number"
				System.out.print("\nDelete article.\nEnter sequence number to delete: "); String delInput = scanner.nextLine();
				int delKey = Integer.parseInt(delInput); databaseHelper.deleteArticleByKey(delKey); break;
			case "4": // "Create Article"
				System.out.print("\nCreate article.\nEnter the new article's information:\nTitle: "); String newTitle = scanner.nextLine();
				System.out.print("Author: "); String newAuthor = scanner.nextLine();
				System.out.print("Keywords: "); String newKeywords = scanner.nextLine();
				System.out.print("Abstract: "); String newAbstrac = scanner.nextLine();
				System.out.print("Body Of Article: "); String newBody = scanner.nextLine();
				System.out.print("References: "); String newReferences = scanner.nextLine();
				//databaseHelper.createArticle(newTitle, newBody, newAuthor, newAbstrac, newKeywords, newReferences);
				System.out.println("New article added."); break;
			case "5": // "Backup Articles To File"
				System.out.print("\nBackup database to file.\nEnter the backup filename (without file extension): ");
				String backupFilename = scanner.nextLine(); databaseHelper.backupToFile(backupFilename); break;
			case "6": // "Load Article Set From File"
				System.out.print("\nLoad database from file.\nEnter the .zip database path and filename (with extension): ");
				String loadDb = scanner.nextLine(); System.out.print("\n"); databaseHelper.loadFromFile(loadDb); break;
			case "0": // "Quit"
				System.out.println("\nQuit program.\nSaving database..."); databaseHelper.saveDatabase(); return;
			default: System.out.println("\nInvalid menu choice.");
			}
	}

	// defaultFlow() CLI menu loop: not used when called from Help System.
	public static void defaultFlow() throws Exception {
		//System.out.println("Welcome To Article Database.");
		String menuOption = "1";
		while (menuOption != "0") { // User menu displayed, waits for user's input.
			System.out.println("\nArticle Database - Menu:\n1. List All Articles");
			System.out.println("2. Display Article By Sequence Number\n3. Delete Article By Sequence Number");
			System.out.println("4. Create Article\n5. Edit Article By Sequence Number\n6. Backup Articles To File");
			System.out.println("7. Load Article Set From File\n8. Return\n9. Display Articles By Groupings.\n0. Quit\nEnter Menu Choice: "); menuOption = scanner.nextLine();
			switch (menuOption) {
			case "1": // "List All Articles" will list all articles in the database, to whichever degree is possible.
				System.out.println("\nDisplay All Articles:"); databaseHelper.displayArticles(); break;
			case "2": // "Display Article By Sequence Number" prompts user for an article sequence number, to attempt to fully display.
				System.out.print("\nDisplay article.\nEnter article sequence number to display: "); String input = scanner.nextLine();
				System.out.print("\n"); int key = Integer.parseInt(input); databaseHelper.displayArticleByKey(key); break;
			case "3": // "Delete Article By Sequence Number" prompts user for article's sequence number to be deleted.
				System.out.print("\nDelete article.\nEnter sequence number to delete: "); String delInput = scanner.nextLine();
				int delKey = Integer.parseInt(delInput); databaseHelper.deleteArticleByKey(delKey); break;
			case "4": // "Create Article" prompts user for a new article's full data set, to add to database as new article.
				System.out.print("\nCreate article.\nEnter the new article's information:\nTitle: "); String newTitle = scanner.nextLine();
				System.out.print("Author: "); String newAuthor = scanner.nextLine();
				System.out.print("Keywords: "); String newKeywords = scanner.nextLine();
				System.out.print("Abstract: "); String newAbstrac = scanner.nextLine();
				System.out.print("Body Of Article: "); String newBody = scanner.nextLine();
				System.out.print("References: "); String newReferences = scanner.nextLine();
				System.out.print("Difficulty (1-4): "); String difficulty = scanner.nextLine();
				System.out.print("Grouping: "); String grouping = scanner.nextLine();
				//databaseHelper.createArticle(newTitle, newBody, newAuthor, newAbstrac, newKeywords, newReferences, difficulty, grouping);

				// Check if using nonSens fields
				System.out.print("Use A Non-Sensitive Title And Abstract? (Y/N): "); String sensFields = scanner.nextLine();
				if ( sensFields.equals("Y") || sensFields.equals("y")  ) {
					System.out.print("Non-Sensitive Title: "); String nonSensTitle = scanner.nextLine();
					System.out.print("Non-Sensitive Abstract: "); String nonSensAbstrac = scanner.nextLine();
					System.out.print("Key For Sensitive: "); String sensKey = scanner.nextLine();
						// sens true: call
					databaseHelper.createArticle(newTitle, newBody, newAuthor, newAbstrac, newKeywords, newReferences, difficulty, grouping, nonSensTitle, nonSensAbstrac, sensKey);
				} else { // non sens call
					databaseHelper.createArticle(newTitle, newBody, newAuthor, newAbstrac, newKeywords, newReferences, difficulty, grouping);
					System.out.println("New article added."); }
				break;	
			case "6": // "Backup Articles To File" prompts user for filename for backup creation of current database.
				System.out.print("\nBackup database to file.\nEnter the backup filename (without file extension): ");
				String backupFilename = scanner.nextLine(); databaseHelper.backupToFile(backupFilename); break;
			case "7": // "Load Article Set From File"
				System.out.print("\nLoad database from file.\nEnter the .zip database path and filename (with extension): ");
				String loadDb = scanner.nextLine(); System.out.print("\n"); databaseHelper.loadFromFile(loadDb); break;
			case "8": return; // "Return" without explicitly closing connection.
			case "5": // UPDATE/EDIT ARTICLE
				System.out.print("\nEdit article.\nEnter sequence number to edit: ");
				String editInput = scanner.nextLine();
				int editKey = Integer.parseInt(editInput);
				if (databaseHelper.checkArticleByKey(editKey) != 1) {
					System.out.print("Article not found.");
				} else {
					System.out.print("Title: "); String editTitle = scanner.nextLine();
					System.out.print("Author: "); String editAuthor = scanner.nextLine();
					System.out.print("Keywords: "); String editKeywords = scanner.nextLine();
					System.out.print("Abstract: "); String editAbstrac = scanner.nextLine();
					System.out.print("Body Of Article: "); String editBody = scanner.nextLine();
					System.out.print("References: "); String editReferences = scanner.nextLine();
					System.out.print("Difficulty (1-4 : beg-expert): "); String editDifficulty = scanner.nextLine();
					//Integer editDifficulty = Integer.parseInt(editDifficultyInput);
					System.out.print("Grouping: "); String editGrouping = scanner.nextLine();

					System.out.print("Article found.");
					int isSens = databaseHelper.checkSensByKey(editKey);
					if (isSens == 0) { // article not sens
						System.out.print("Article is not sensitive. Will it remain non-sensitive? (Y/N): ");
						String newSens = scanner.nextLine();
						if ( newSens.equals("Y") || newSens.equals("y")  ) {
							// call non-sens edit method
							databaseHelper.editArticleByKey(editKey, editTitle, editBody, editAuthor, editAbstrac, editKeywords, editReferences, editDifficulty, editGrouping);
						} else {
							// call sens edit method - get sens values
							System.out.print("Non-Sensitive Access-Key: "); String editNonSensKey = scanner.nextLine();
							System.out.print("Non-Sensitive Title: "); String editNonSensTitle = scanner.nextLine();
							System.out.print("Non-Sensitive Abstract: "); String editNonSensAbstrac = scanner.nextLine();
							databaseHelper.editArticleByKeySens(editKey, editTitle, editBody, editAuthor, editAbstrac, editKeywords, editReferences, editDifficulty, editGrouping, editNonSensKey, editNonSensTitle, editNonSensAbstrac);
						}
					} else {
						System.out.print("Article is sensitive. Will it remain sensitive? (Y/N): ");
						String newSens = scanner.nextLine();
						if ( newSens.equals("Y") || newSens.equals("y")  ) {
							// call sens edit method - get sens values
							System.out.print("Non-Sensitive Access-Key: "); String editNonSensKey = scanner.nextLine();
							System.out.print("Non-Sensitive Title: "); String editNonSensTitle = scanner.nextLine();
							System.out.print("Non-Sensitive Abstract: "); String editNonSensAbstrac = scanner.nextLine();
							databaseHelper.editArticleByKeySens(editKey, editTitle, editBody, editAuthor, editAbstrac, editKeywords, editReferences, editDifficulty, editGrouping, editNonSensKey, editNonSensTitle, editNonSensAbstrac);
						} else {
							// call non-sens edit method - cleared any sens fields
							databaseHelper.editArticleByKey(editKey, editTitle, editBody, editAuthor, editAbstrac, editKeywords, editReferences, editDifficulty, editGrouping);
						}
					}
				}
				break;
			case "9": // display article by groupings (String groupings space seprated)
				System.out.print("\nDisplay articles by grouping.\nEnter group IDs (space-separated): ");
				String searchGroupings = scanner.nextLine();

				databaseHelper.displayArticlesByGrouping(searchGroupings);

				break;
			case "0": // "Quit" ensures current database is saved before closing connection.
				System.out.println("\nQuit program.\nSaving database..."); databaseHelper.saveDatabase(); return;
			default: System.out.println("\nInvalid menu choice.");
			}
		}
	}

	// DIRECT ACCESS CALLS FROM HELP SYSTEM:
	// make direct access for all menu options 1-6 (to be given to users by user-account programmer)
	//
	// add article
	// delete article
	// edit article (only ask to edit sens title/desc if sensKey exists)
	// backup - variants
	// load - variants

	// search and display from user request: grouping key
	// update and remove by group key

	// to be able to give ArticleDatabase's caller direct access to DatabaseHelper
	// THIS is how Help System, once logged in, can interface with article DB

		// Display All Articles w/o sensitive info
		// HELPER FUNCTIONS FOR CALLER - bypass menu/defaultFlow
		public static void callDisplayArticles() throws Exception {
			databaseHelper.displayArticles();
		}
		public static void callDisplayArticleByKey(String input) throws Exception {
			int key = Integer.parseInt(input); databaseHelper.displayArticleByKey(key);
			// DOESNT DO USER KEY match ARTICLE KEY checks ; just displays all data
		}
		public static void callDisplayArticleByGroups(String searchGroupings) throws Exception {
			databaseHelper.displayArticlesByGrouping(searchGroupings);
		}
		public static void callDisplayArticleByKeywords(String searchKeywords) throws Exception {
			databaseHelper.displayArticlesByKeywords(searchKeywords);
		}
		public static void callDeleteArticleByKey(String input) throws Exception {
			int key = Integer.parseInt(input); databaseHelper.deleteArticleByKey(key);
		}
		public static void callCreateArticle(String newTitle, String newBody, String newAuthor, String newAbstrac, String newKeywords, String newReferences, String newDifficulty, String newGrouping) throws Exception {
			databaseHelper.createArticle(newTitle, newBody, newAuthor, newAbstrac, newKeywords, newReferences, newDifficulty, newGrouping);
		}	// Create Non-Sensitive Article
		public static void callCreateArticle(String newTitle, String newBody, String newAuthor, String newAbstrac, String newKeywords, String newReferences, String newDifficulty, String newGrouping, String nonSensTitle, String nonSensAbstrac, String sensKey) throws Exception {
			databaseHelper.createArticle(newTitle, newBody, newAuthor, newAbstrac, newKeywords, newReferences, newDifficulty, newGrouping, nonSensTitle, nonSensAbstrac, sensKey);
		}  // Create Sensitive Article
		public static void callEditArticle(String key, String editTitle, String editBody, String editAuthor, String editAbstrac, String editKeywords, String editReferences, String editDifficulty, String editGrouping) throws Exception {
			int editKey = Integer.parseInt(key);
			databaseHelper.editArticleByKey(editKey, editTitle, editBody, editAuthor, editAbstrac, editKeywords, editReferences, editDifficulty, editGrouping);
		} // Edit Article As New Non-Sensitive Article
		public static void callEditArticle(String key, String editTitle, String editBody, String editAuthor, String editAbstrac, String editKeywords, String editReferences, String editDifficulty, String editGrouping, String editNonSensKey, String editNonSensTitle, String editNonSensAbstrac) throws Exception {
			int editKey = Integer.parseInt(key);
			databaseHelper.editArticleByKeySens(editKey, editTitle, editBody, editAuthor, editAbstrac, editKeywords, editReferences, editDifficulty, editGrouping, editNonSensKey, editNonSensTitle, editNonSensAbstrac);
		} // Edit Article As New Sensitive Article

		public static void callBackupFile(String backupFilename) throws Exception {
			//  backup filename without file extension e.g. "backup1"
			databaseHelper.backupToFile(backupFilename);
		}
		public static void callLoadFile(String loadFilename) throws Exception {
			// Enter the .zip database path and filename with extension
			// ex: C:\Users\KT_Laptop\eclipse-workspace\HW6\backups\1146pm.zip
			databaseHelper.loadFromFile(loadFilename);
		}
	}