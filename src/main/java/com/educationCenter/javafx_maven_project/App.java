package com.educationCenter.javafx_maven_project;
import java.security.PublicKey;
import java.sql.SQLException;
import java.util.*;

import org.h2.expression.function.DateTimeFormatFunction;
import org.h2.mvstore.type.StringDataType;

import edu.asu.DatabasePart1.DatabaseHelper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    		
    		while (true){
    		
	    		if (databaseHelper.isDatabaseEmpty()) {
					System.out.println( "In-Memory Database  is empty" );
					//set up administrator access
					if (setupAdministrator()) {
						// add if needed
					}else { 
						System.out.println("Try Again !");
					}
				}
	    		else {
					System.out.println( "1.Admin 2.Instrutor 3.Student 4.Parents 5.Forget Password" );
					String role = scanner.nextLine();
	
					switch (role) {
					case "Admin":
						
						if (!adminLogin()){
							System.out.println("Invalid Login!");
							continue;
						}
						
						if (datas[4] == null) {
							adminSetupLogin();
						}

						adminHome();
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
					case "Forgot Password":
						resetPasword(username);
						break;
					default:
						System.out.println("Invalid choice. Please select 'a', 'u'");
						databaseHelper.closeConnection();
					}
				}}
	    		
    	}catch (SQLException e) {
    		System.out.println(e.getMessage());
    	}
    	databaseHelper.closeConnection();
    	
    }
    
	public static void adminHome() throws SQLException {
		System.out.println("1.logout 2.generateUser");
		String command = scanner.nextLine();
		switch (command) {
		case "logout": {
			logout();
			break;
		}
		case "generateUser":{
			generateUser();
		}
		default:
			adminHome();
		}
		
	}

	public static void generateUser() throws SQLException  {
		String testUser ;
		String[] user ;
		String testRole;
		while (true){
		System.out.println("Enter Preferred Username : ");
		testUser = scanner.nextLine();
		user = databaseHelper.doesUserExist(testUser);
		if (user != null){
			System.out.println("Already Exists Username, Try Again!");
			continue;
		}
		System.out.println("Enter Preferred User Role : ");
		testRole = scanner.nextLine();
		break;
		}
		
		char[] onetimeCode = generateRandomOneTimeCode();
		String onetimeCodeString = new String(onetimeCode);
		String expiringDateTime = LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ISO_DATE_TIME);
		String codeDetails = expiringDateTime+" "+onetimeCodeString;

		databaseHelper.generateUserByAdmin(testUser,testRole,codeDetails);
		}
 
	public static void resetPasword(String forgotpasswordUsername) throws SQLException{
		String[] collectedData = databaseHelper.doesUserExist(username);

		if (collectedData[4] == null){
			if (adminSetupLogin()){
			collectedData = databaseHelper.doesUserExist(username);
			}else{
				System.out.println("Invalid Try Again!");
			}
		}

		if(!verifyEmail(collectedData[4])){
			
		}else {
	    int trials = 3;
	    String passWord = "";
	    while (trials > 0 ) {
			System.out.print("Enter New Admin Password: ");
			passWord = scanner.nextLine();
			System.out.print("Enter New Admin Re-EnterPassword: ");
			String rePassword = scanner.nextLine();
			
			if (!passWord.equals(rePassword)) {
			trials = trials - 1;  
			System.out.printf("Password doesn't match, %d trails left!", trials );
			continue;
			}		
				
			break;
		}
		databaseHelper.updatePassword(username,password,"admin");
		password = passWord;

	}
	}
	
	public static void logout(){
		username=null;
		password=null;
		datas=null;
		
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
			    if (databaseHelper.doesUserExist(email) == null) {  
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
		
		private static boolean adminSetupLogin() throws SQLException {
			
			// name input
			System.out.print("Enter Admin Frist Name: ");
			String fristName = scanner.nextLine();
			System.out.print("Enter Admin Middle Name: ");
			String middleName = scanner.nextLine();
			System.out.print("Enter Admin Last Name: ");
			String lastName = scanner.nextLine();
			
			// email input
			System.out.print("Enter Admin Email: ");
			String email = scanner.nextLine();
			databaseHelper.displayUsersByAdmin();
			datas = databaseHelper.login(username, password, "admin");
			if (verifyEmail(email) && datas != null) {
				System.out.println("Admin login successful.");
				databaseHelper.displayUsersByAdmin();
				String[] adminData = {email,fristName,middleName,lastName};
				if (databaseHelper.updateAdminUser(username,password,adminData)){
					return true;
				};
			} else {
				System.out.println("Invalid admin credentials. Try again!!");
				return false;
			}
			return false;
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
