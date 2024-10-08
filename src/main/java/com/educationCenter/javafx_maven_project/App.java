package com.educationCenter.javafx_maven_project;
import java.security.PublicKey;
import java.sql.SQLException;
import java.util.*;
import edu.asu.DatabasePart1.DatabaseHelper;


/**
 * Hello world!
 */
public class App {
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	private static final Scanner scanner = new Scanner(System.in);
	private static String username;
	private static String password;
	private static String[] datas; 

	public static void main(String[] args) {
    	
    	try {
    		
    		databaseHelper.connectToDatabase(); // connecting to database
    		
    		if (databaseHelper.isDatabaseEmpty()) {
				System.out.println( "In-Memory Database  is empty" );
				//set up administrator access
				if (setupAdministrator()) {
					adminLogin();
				}else { 
					System.out.println("Try Again !");
				}
			}
    		else {
				System.out.println( "1.Admin 2.Instrutor 3.Student 4.Parents" );
				String role = scanner.nextLine();

				switch (role) {
				case "Admin":
					System.out.println(adminLogin());
					if (datas[4] == null) {
						adminSetupLogin();
					}
					break;
				case "Instrutor":
					userFlow();
					break;
				case "Student":
					userFlow();
					break;
				case "Parents":
					userFlow();
					break;
				
				default:
					System.out.println("Invalid choice. Please select 'a', 'u'");
					databaseHelper.closeConnection();
				}
			}
    		
    	}catch (SQLException e) {
    		System.out.println(e.getMessage());
    	}
    	databaseHelper.closeConnection();
    	
    }
    
    
	 public static boolean setupAdministrator() throws SQLException {
	    int trials = 3;
	    String userName = "";
	    String passWord = "";
	    while (trials > 0 ) {
			System.out.println("Setting up the Administrator access.");
			System.out.print("Enter Admin Username: ");
			userName = scanner.nextLine();
			System.out.print("Enter Admin Password: ");
			passWord = scanner.nextLine();
			System.out.print("Enter Admin Re-EnterPassword: ");
			String rePassword = scanner.nextLine();
			
			if (!passWord.equals(rePassword)) {
			trials = trials - 1;  
			System.out.printf("Password doesn't match, %d trails left!", trials );
			continue;
			}		
				
			break;
	    }
	    if (trials == 0 ) {
			return false;
	    }
	     
		databaseHelper.register(userName, passWord, "admin");
		username=  userName;
		password = passWord;
		
		System.out.println("Administrator setup completed.");
		
		return true;

	}
	 

		private static void userFlow() throws SQLException {
			String email = null;
			String password = null;
			System.out.println("user flow");
			System.out.print("What would you like to do 1.Register 2.Login  ");
			String choice = scanner.nextLine();
			switch(choice) {
			case "1": 
				System.out.print("Enter User Email: ");
				email = scanner.nextLine();
				System.out.print("Enter User Password: ");
				password = scanner.nextLine(); 
				// Check if user already exists in the database
			    if (!databaseHelper.doesUserExist(email)) {
			        databaseHelper.register(email, password, "user");
			        System.out.println("User setup completed.");
			    } else {
			        System.out.println("User already exists.");
			    }
				break;
			case "2":
				System.out.print("Enter User Email: ");
				email = scanner.nextLine();
				datas = databaseHelper.login(username, password, "user");
				if (datas != null) {
					System.out.println("User login successful.");
//					databaseHelper.displayUsers();

				} else {
					System.out.println("Invalid user credentials. Try again!!");
				}
				break;
			}
		}
		private static boolean adminLogin() throws SQLException {
			System.out.print("Enter Login Username: ");
			String loginName = scanner.nextLine();
			System.out.print("Enter Login Password: ");
			String credentials = scanner.nextLine();
			datas = databaseHelper.login(loginName, credentials, "admin");
			if (datas != null) {
				username = loginName;
				password = credentials;
				return true;
			}
			return false;
		}
		
		private static void adminSetupLogin() throws SQLException {
			System.out.println("admin flow");
			
			// name input
			System.out.print("Enter Admin Frist Name: ");
			String fristName = scanner.nextLine();
			System.out.print("Enter Admin Middle Name: ");
			String middleName = scanner.nextLine();
			System.out.print("Enter Admin Last Name: ");
			String lastNameString = scanner.nextLine();
			
			// email input
			System.out.print("Enter Admin Email: ");
			String email = scanner.nextLine();
			databaseHelper.displayUsersByAdmin();
			datas = databaseHelper.login(username, password, "admin");
			if (verifyEmail(email) && datas != null) {
				System.out.println("Admin login successful.");
				databaseHelper.displayUsersByAdmin();
				
			} else {
				System.out.println("Invalid admin credentials. Try again!!");
			}
		}
		
		private static char[] generateRandomOneTimeCode() {
			int len = 8;
	      
	  
	        String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
	        String Small_chars = "abcdefghijklmnopqrstuvwxyz"; 
	        String numbers = "0123456789";
	  
	  
	        String values = Capital_chars + Small_chars + 
	                        numbers; 
	  
	        // Using random method 
	        Random rndm_method = new Random(); 
	  
	        char[] password = new char[len]; 
	  
	        for (int i = 0; i < len; i++) 
	        { 
	            // Use of charAt() method : to get character value 
	            // Use of nextInt() as it is scanning the value as int 
	            password[i] = 
	              values.charAt(rndm_method.nextInt(values.length())); 
	  
	        } 
	        return password; 
			
		}
		
		private static boolean verifyEmail(String email) throws SQLException{
			char[] onetimeCode = generateRandomOneTimeCode();
			System.out.println(onetimeCode);
			int trials = 3;
			while (trials > 0) {
			System.out.print("Enter One time code: ");	
			char[] inputCode = scanner.nextLine().toCharArray();
			System.out.println("");
			System.out.println(inputCode);
			System.out.println(onetimeCode);
			System.out.println(Arrays.equals(onetimeCode, inputCode));
			if (Arrays.equals(onetimeCode, inputCode)) {
				return true;
			}
			trials = trials -1; 
			System.out.printf("Password doesn't match, %d trails left!", trials );
			}
			return false;
		}
}
