package com.educationCenter.Articler_Database_Handler;

import com.educationCenter.App;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ArticleDatabase {
	public static ArticleDatabaseHelper articleDatabaseHelper; 					// Instantiate ArticleDatabaseHelper.
	private static final Scanner scanner = new Scanner(System.in);  // Instantiate Scanner.
	public static void main( String[] args ) throws Exception 		// Main Method: tries connecting to database.
	{
		connect_dataBase();
		defaultFlow();
	}

	public static void connect_dataBase() {
		articleDatabaseHelper = new ArticleDatabaseHelper();
		try {
			articleDatabaseHelper.articleConnectToDatabase();
		} catch (SQLException e) {
			System.err.println("Database error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// New searchArticles method to filter articles based on query, level, and group
	public static String[][] searchArticles(String query, String level, String group) {
		List<String[]> results = new ArrayList<>();

		try {
			// Fetch all articles from the database
			String[][] allArticles = articleDatabaseHelper.returnListArticles();

			for (String[] article : allArticles) {
				String articleTitle = article[1];
				String articleAuthor = article[2];
				String articleAbstract = article[3];
				String articleLevel = article[4];
				String articleGroup = article[5]; // Assuming article[5] represents the group

				// Check if the article matches the search criteria
				boolean matchesQuery = query.isEmpty() ||
						articleTitle.contains(query) ||
						articleAuthor.contains(query) ||
						articleAbstract.contains(query);

				boolean matchesLevel = level.equals("All") || articleLevel.equalsIgnoreCase(level);
				boolean matchesGroup = group.equals("All") || articleGroup.equalsIgnoreCase(group);

				// If the article matches all criteria, add it to results
				if (matchesQuery && matchesLevel && matchesGroup) {
                    results.add(article);
                }
			}
		} catch (Exception e) {
			System.out.println("Error searching articles: " + e.getMessage());
		}

		// Convert results list to a 2D array and return it
		return results.toArray(new String[0][]);
	}

	public static boolean addAccessForGroup(String userid,String groupName){
		// instructor to student
		// admin to instructor
		try{
			articleDatabaseHelper.addSpecialAccess(userid,groupName,"Instructor");


			return true;
		}catch (Exception e){
			System.out.println("Database error: " + e);
		}
		return false;
	}

	public static boolean deleteAccessForGroup(String userid,String groupName){
		try{

			articleDatabaseHelper.deleteSpecialAccess(userid,groupName);

			return true;
		} catch (Exception e) {
			System.out.println("Database error: " + e.getMessage());
		}
		return false;
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
				System.out.println("\nDisplaying all articles:"); articleDatabaseHelper.displayArticles(); break;
			case "2": // "Display Article By Sequence Number"
				System.out.print("\nDisplay article.\nEnter article sequence number to display: "); String input = scanner.nextLine(); System.out.print("\n");
				int key = Integer.parseInt(input); articleDatabaseHelper.displayArticleByKey(key); break;
			case "3": // "Delete Article By Sequence Number"
				System.out.print("\nDelete article.\nEnter sequence number to delete: "); String delInput = scanner.nextLine();
				int delKey = Integer.parseInt(delInput); articleDatabaseHelper.deleteArticleByKey(delKey); break;
			case "4": // "Create Article"
				System.out.print("\nCreate article.\nEnter the new article's information:\nTitle: "); String newTitle = scanner.nextLine();
				System.out.print("Author: "); String newAuthor = scanner.nextLine();
				System.out.print("Keywords: "); String newKeywords = scanner.nextLine();
				System.out.print("Abstract: "); String newAbstrac = scanner.nextLine();
				System.out.print("Body Of Article: "); String newBody = scanner.nextLine();
				System.out.print("References: "); String newReferences = scanner.nextLine();
				//articleDatabaseHelper.createArticle(newTitle, newBody, newAuthor, newAbstrac, newKeywords, newReferences);
				System.out.println("New article added."); break;
			case "5": // "Backup Articles To File"
				System.out.print("\nBackup database to file.\nEnter the backup filename (without file extension): ");
				String backupFilename = scanner.nextLine(); articleDatabaseHelper.backupToFile(backupFilename); break;
			case "6": // "Load Article Set From File"
				System.out.print("\nLoad database from file.\nEnter the .zip database path and filename (with extension): ");
				String loadDb = scanner.nextLine(); System.out.print("\n"); articleDatabaseHelper.loadFromFile(loadDb); break;
			case "0": // "Quit"
				System.out.println("\nQuit program.\nSaving database..."); articleDatabaseHelper.saveDatabase(); return;
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
				System.out.println("\nDisplay All Articles:"); articleDatabaseHelper.displayArticles(); break;
			case "2": // "Display Article By Sequence Number" prompts user for an article sequence number, to attempt to fully display.
				System.out.print("\nDisplay article.\nEnter article sequence number to display: "); String input = scanner.nextLine();
				System.out.print("\n"); int key = Integer.parseInt(input); articleDatabaseHelper.displayArticleByKey(key); break;
			case "3": // "Delete Article By Sequence Number" prompts user for article's sequence number to be deleted.
				System.out.print("\nDelete article.\nEnter sequence number to delete: "); String delInput = scanner.nextLine();
				int delKey = Integer.parseInt(delInput); articleDatabaseHelper.deleteArticleByKey(delKey); break;
			case "4": // "Create Article" prompts user for a new article's full data set, to add to database as new article.
				System.out.print("\nCreate article.\nEnter the new article's information:\nTitle: "); String newTitle = scanner.nextLine();
				System.out.print("Author: "); String newAuthor = scanner.nextLine();
				System.out.print("Keywords: "); String newKeywords = scanner.nextLine();
				System.out.print("Abstract: "); String newAbstrac = scanner.nextLine();
				System.out.print("Body Of Article: "); String newBody = scanner.nextLine();
				System.out.print("References: "); String newReferences = scanner.nextLine();
				System.out.print("Difficulty (1-4): "); String difficulty = scanner.nextLine();
				System.out.print("Grouping: "); String grouping = scanner.nextLine();
				//articleDatabaseHelper.createArticle(newTitle, newBody, newAuthor, newAbstrac, newKeywords, newReferences, difficulty, grouping);

				// Check if using nonSens fields
				System.out.print("Use A Non-Sensitive Title And Abstract? (Y/N): "); String sensFields = scanner.nextLine();
				if ( sensFields.equals("Y") || sensFields.equals("y")  ) {
					System.out.print("Non-Sensitive Title: "); String nonSensTitle = scanner.nextLine();
					System.out.print("Non-Sensitive Abstract: "); String nonSensAbstrac = scanner.nextLine();
					System.out.print("Key For Sensitive: "); String sensKey = scanner.nextLine();
						// sens true: call
					articleDatabaseHelper.createArticle(newTitle, newBody, newAuthor, newAbstrac, newKeywords, newReferences, difficulty, grouping, nonSensTitle, nonSensAbstrac, sensKey);
				} else { // non sens call
					articleDatabaseHelper.createArticle(newTitle, newBody, newAuthor, newAbstrac, newKeywords, newReferences, difficulty, grouping);
					System.out.println("New article added."); }
				break;	
			case "6": // "Backup Articles To File" prompts user for filename for backup creation of current database.
				System.out.print("\nBackup database to file.\nEnter the backup filename (without file extension): ");
				String backupFilename = scanner.nextLine(); articleDatabaseHelper.backupToFile(backupFilename); break;
			case "7": // "Load Article Set From File"
				System.out.print("\nLoad database from file.\nEnter the .zip database path and filename (with extension): ");
				String loadDb = scanner.nextLine(); System.out.print("\n"); articleDatabaseHelper.loadFromFile(loadDb); break;
			case "8": return; // "Return" without explicitly closing connection.
			case "5": // UPDATE/EDIT ARTICLE
				System.out.print("\nEdit article.\nEnter sequence number to edit: ");
				String editInput = scanner.nextLine();
				int editKey = Integer.parseInt(editInput);
				if (articleDatabaseHelper.checkArticleByKey(editKey) != 1) {
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
					int isSens = articleDatabaseHelper.checkSensByKey(editKey);
					if (isSens == 0) { // article not sens
						System.out.print("Article is not sensitive. Will it remain non-sensitive? (Y/N): ");
						String newSens = scanner.nextLine();
						if ( newSens.equals("Y") || newSens.equals("y")  ) {
							// call non-sens edit method
							//articleDatabaseHelper.editArticleByKey(editKey, editTitle, editBody, editAuthor, editAbstrac, editKeywords, editReferences, editDifficulty, editGrouping);
						} else {
							// call sens edit method - get sens values
							System.out.print("Non-Sensitive Access-Key: "); String editNonSensKey = scanner.nextLine();
							System.out.print("Non-Sensitive Title: "); String editNonSensTitle = scanner.nextLine();
							System.out.print("Non-Sensitive Abstract: "); String editNonSensAbstrac = scanner.nextLine();
							articleDatabaseHelper.editArticleByKeySens(editKey, editTitle, editBody, editAuthor, editAbstrac, editKeywords, editReferences, editDifficulty, editGrouping, editNonSensKey, editNonSensTitle, editNonSensAbstrac);
						}
					} else {
						System.out.print("Article is sensitive. Will it remain sensitive? (Y/N): ");
						String newSens = scanner.nextLine();
						if ( newSens.equals("Y") || newSens.equals("y")  ) {
							// call sens edit method - get sens values
							System.out.print("Non-Sensitive Access-Key: "); String editNonSensKey = scanner.nextLine();
							System.out.print("Non-Sensitive Title: "); String editNonSensTitle = scanner.nextLine();
							System.out.print("Non-Sensitive Abstract: "); String editNonSensAbstrac = scanner.nextLine();
							articleDatabaseHelper.editArticleByKeySens(editKey, editTitle, editBody, editAuthor, editAbstrac, editKeywords, editReferences, editDifficulty, editGrouping, editNonSensKey, editNonSensTitle, editNonSensAbstrac);
						} else {
							// call non-sens edit method - cleared any sens fields
							//articleDatabaseHelper.editArticleByKey(editKey, editTitle, editBody, editAuthor, editAbstrac, editKeywords, editReferences, editDifficulty, editGrouping);
						}
					}
				}
				break;
				case "9": // display article by groupings (String groupings space seprated)
					System.out.print("\nDisplay articles by grouping.\nEnter group IDs (space-separated): ");
					String searchGroupings = scanner.nextLine();
					articleDatabaseHelper.displayArticlesByGrouping(searchGroupings);
					break;
				case "10": // "Merge Article Set From File"
					System.out.print("\nMerge database from file.\nEnter the .zip database path and filename (with extension): ");
					String mergeDb = scanner.nextLine();
					System.out.print("\n"); articleDatabaseHelper.loadFromFileMerge(mergeDb);
					break;
				case "11": // "Backup By Groupings" prompts user for filename for backup creation of current database.
					System.out.print("\nBackup database to file.\nEnter the backup filename (without file extension): ");
					String backupGroupingsFilename = scanner.nextLine();
					System.out.print("\nEnter Groupings String To Backup By: ");
					String backupGroupingsGroupings = scanner.nextLine();
					articleDatabaseHelper.backupByGrouping(backupGroupingsFilename, backupGroupingsGroupings);
					break;
				case "0": // "Quit" ensures current database is saved before closing connection.
					System.out.println("\nQuit program.\nSaving database..."); articleDatabaseHelper.saveDatabase(); return;
				default: System.out.println("\nInvalid menu choice.");
			}
		}
	}

	public static String[] returnGroupsFromUser(String username){
		try{
			int userId = Integer.parseInt(App.returnUserId(username));
			return articleDatabaseHelper.returnGroupFromUser(userId);
		} catch (Exception e) {
            System.out.println("Error in returnGroupsFromUser");
        }
		return null;
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

	// to be able to give ArticleDatabase's caller direct access to ArticleDatabaseHelper
	// THIS is how Help System, once logged in, can interface with article DB

		// Display All Articles w/o sensitive info
		// HELPER FUNCTIONS FOR CALLER - bypass menu/defaultFlow
		public static void callDisplayArticles() throws Exception {
			articleDatabaseHelper.displayArticles();
		}
		public static String[][] returnListArticles() {

			try {

                return articleDatabaseHelper.returnListArticles();
            }catch(Exception e) {
			System.out.println(e.getMessage());
			}
			return null;
		}
		public static void callDisplayArticleByKey(String input) throws Exception {
			int key = Integer.parseInt(input); articleDatabaseHelper.displayArticleByKey(key);
			// DOESNT DO USER KEY match ARTICLE KEY checks ; just displays all data
		}
		public static String[] returnArticle(String id ){
		try{
			int key = Integer.parseInt(id);
			return articleDatabaseHelper.returnArticle(key);
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
		return null;
		}
		public static void callDisplayArticleByGroups(String searchGroupings) throws Exception {
			articleDatabaseHelper.displayArticlesByGrouping(searchGroupings);
		}
		public static void callDisplayArticleByKeywords(String searchKeywords) throws Exception {
			articleDatabaseHelper.displayArticlesByKeywords(searchKeywords);
		}
		public static void callDeleteArticleByKey(String input){
		try {
			int key = Integer.parseInt(input);
			articleDatabaseHelper.deleteArticleByKey(key);
		}catch (Exception e) {System.out.println(e);}
		}
		public static void callCreateArticle(String newTitle, String newBody, String newAuthor, String newAbstrac, String newKeywords, String newReferences, String newDifficulty, String newGrouping) throws Exception {
			articleDatabaseHelper.createArticle(newTitle, newBody, newAuthor, newAbstrac, newKeywords, newReferences, newDifficulty, newGrouping);
		}	// Create Non-Sensitive Article
		public static void callCreateArticle(String newTitle, String newBody, String newAuthor, String newAbstrac, String newKeywords, String newReferences, String newDifficulty, String newGrouping, String nonSensTitle, String nonSensAbstrac, String sensKey) throws Exception {
			articleDatabaseHelper.createArticle(newTitle, newBody, newAuthor, newAbstrac, newKeywords, newReferences, newDifficulty, newGrouping, nonSensTitle, nonSensAbstrac, sensKey);
		}  // Create Sensitive Article
		public static void callEditArticle(String key, String editTitle, String editBody){
			int editKey = Integer.parseInt(key);
			try {
				articleDatabaseHelper.editArticleByKey(editKey, editTitle, editBody);
			}catch (Exception e) {System.out.println(e);}
		}
//		public static void callEditArticle(String key, String editTitle, String editBody, String editAuthor, String editAbstrac, String editKeywords, String editReferences, String editDifficulty, String editGrouping) throws Exception {
//			int editKey = Integer.parseInt(key);
//			articleDatabaseHelper.editArticleByKey(editKey, editTitle, editBody, editAuthor, editAbstrac, editKeywords, editReferences, editDifficulty, editGrouping);
//		} // Edit Article As New Non-Sensitive Article
//		public static void callEditArticle(String key, String editTitle, String editBody, String editAuthor, String editAbstrac, String editKeywords, String editReferences, String editDifficulty, String editGrouping, String editNonSensKey, String editNonSensTitle, String editNonSensAbstrac) throws Exception {
//			int editKey = Integer.parseInt(key);
//			articleDatabaseHelper.editArticleByKeySens(editKey, editTitle, editBody, editAuthor, editAbstrac, editKeywords, editReferences, editDifficulty, editGrouping, editNonSensKey, editNonSensTitle, editNonSensAbstrac);
//		} // Edit Article As New Sensitive Article

//		public static void callBackupFile(String backupFilename) throws Exception {
//			//  backup filename without file extension e.g. "backup1"
//			articleDatabaseHelper.backupToFile(backupFilename);
//		}
		public static void callBackupByGrouping(String backupFilename, String groupingIDs){
            try {
                articleDatabaseHelper.backupByGrouping(backupFilename, groupingIDs);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
		public static void callLoadFile(String loadFilename){
			// Enter the .zip database path and filename with extension
			// ex: C:\Users\KT_Laptop\eclipse-workspace\HW6\backups\1146pm.zip
            try {
                articleDatabaseHelper.loadFromFile(loadFilename);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
	public static void backupToFile(String filename)   {
		try {
			articleDatabaseHelper.backupToFile(filename); // Use the existing method to handle backup
		}catch (Exception e) {System.out.println(e);}
	}
	// Method to load articles from a file


	public static void callLoadFileMerge(String loadFilename) throws Exception {
		// Enter the .zip database path and filename with extension
		// ex: C:\Users\KT_Laptop\eclipse-workspace\HW6\backups\1146pm.zip
		articleDatabaseHelper.loadFromFileMerge(loadFilename);
	}



}