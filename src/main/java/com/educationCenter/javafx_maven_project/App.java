package com.educationCenter.javafx_maven_project;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import edu.asu.DatabasePart1.DatabaseHelper;

/**
 * Hello world!
 */
public class App {
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	private static final Scanner scanner = new Scanner(System.in);
	private static String username;
	private static String password;
	private static String activeRole;
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
					System.out.println( "1.Login 2.ForgotPassword" );
					String choice = scanner.nextLine();
					
					switch (choice) {
						case "1":
							if(!appLogin()){
								System.out.println("Invalid Login!");
								continue;
							};
							System.out.println(activeRole);
							switch (activeRole) {
								case "admin":
									adminHome();
									continue;
								case "student":
									studentHome();
									continue;
								case "instructor":
									instructorHome();
									continue;
								case "parents":
									parentsHome();
									continue;
								default:
									break;
							}
							
							continue;
						case "2":
							System.out.println("Enter Fordot Username: ");
							String forgetUsername = scanner.nextLine();
							resetPasword(forgetUsername);
							continue;
						default:
							continue;
					}
				}
			}
	    		
    	}catch (SQLException e) {
    		System.out.println(e.getMessage());
    	}
    	databaseHelper.closeConnection();
    	
    }
    
	public static void adminHome() throws SQLException {
		System.out.println("1.logout 2.generateUser");
		String command = scanner.nextLine();
		switch (command) {
		case "1": {
			logout();
			break;
		}
		case "2":{
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
		String[] userRoles = {testRole};

		databaseHelper.generateUserByAdmin(testUser,userRoles,codeDetails);
		}
 
	public static boolean resetPasword(String forgotpasswordUsername) throws SQLException{
		String[] collectedData = databaseHelper.doesUserExist(forgotpasswordUsername);

		if (collectedData == null){
			System.out.println("No user with given USERNAME");
			return false;
		}
		if (collectedData[4] == null){
			if (setupUserInformation()){
			collectedData = databaseHelper.doesUserExist(forgotpasswordUsername);
			}else{
				System.out.println("Invalid Try Again!");
				return false;
			}
		}//udcNKKhA

		if(verifyEmail(collectedData[4]) == false){
			return false;
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
		if (!databaseHelper.updatePassword(forgotpasswordUsername,passWord)){
			return false;
		};
		password = passWord;
		System.out.println("Update Succefull");
		return true;
	}
	
	}
	
	public static void logout(){
		username=null;
		password=null;
		datas=null;
		activeRole=null;
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
	    String[] roles = {"admin"};
		databaseHelper.register(userName, passWord, roles);
		username=  userName;
		password = passWord;
		
		System.out.println("Administrator setup completed.");
		
		return true;

	}
	 
		private static boolean appLogin() throws SQLException {
			System.out.print("Enter Login Username: ");
			String loginName = scanner.nextLine();

			String oneTimeCodeInvited = databaseHelper.checkInvitedUser(loginName);

			if (oneTimeCodeInvited != null){
				System.out.println("Entry One Time Provieded code : ");
				String inputCode = scanner.nextLine();
				if (!loginInvitedUser(loginName,oneTimeCodeInvited,inputCode)){
					return false;
				};
			}
			
			
			System.out.println("Enter Login Password: ");
			String credentials = scanner.nextLine();
			datas = databaseHelper.login(loginName, credentials);
			if (datas != null) {
				username = loginName;
				password = credentials;
				checkrole();
				if(datas[4] == null){
					setupUserInformation();
				}
				return true;
			}
			return false;
		}
		
		private static boolean loginInvitedUser(String user,String onetimecode,String inputCode) throws SQLException{
			//date
			String[] onetimearry = onetimecode.split(" ");
			System.out.println(onetimearry[0] + " " + onetimearry[1]);
			
			if (!LocalDateTime.now().isBefore(LocalDateTime.parse(onetimearry[0], DateTimeFormatter.ISO_DATE_TIME))){
				System.out.println("Credentials Expired!");
				return false;
			};

			//passwordmatch
			if (!inputCode.equals(onetimearry[1])){
				System.out.println("Wrong Password! Try Again");
				return false;
			}

			//createNewPassword
			if (!resetPasword(user)){
				return false;
			};

			return true;
		
		}

		private static void checkrole() throws SQLException{
			String[] roles = databaseHelper.getRoleArray(username,password);
			System.out.println(roles);
			if (roles.length > 1){
				System.out.println("Choose Roles: ");
				for (int i = 1; i > roles.length;i++ ){
					System.out.printf("%d %s" , i , roles[i-1]);
				}
				int roleChoice = scanner.nextInt();
				activeRole = roles[roleChoice-1];
			}else{
				System.out.println(roles[0]);
				activeRole = roles[0];
			}

		}
		private static boolean setupUserInformation() throws SQLException {
			
			// name input
			System.out.print("Enter Frist Name: ");
			String fristName = scanner.nextLine();
			System.out.print("Enter Middle Name: ");
			String middleName = scanner.nextLine();
			System.out.print("Enter Last Name: ");
			String lastName = scanner.nextLine();
			
			// email input
			System.out.print("Enter Email: ");
			String email = scanner.nextLine();
			databaseHelper.displayUsersByAdmin();
			datas = databaseHelper.login(username, password);
			if (verifyEmail(email) && datas != null) {
				System.out.println("Admin login successful.");
				databaseHelper.displayUsersByAdmin();
				String[] userData = {email,fristName,middleName,lastName};
				if (databaseHelper.updateUserInformation(username,password,userData)){
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
	  
	        char[] onePassword = new char[len]; 
	  
	        for (int i = 0; i < len; i++) 
	        { 
	            // Use of charAt() method : to get character value 
	            // Use of nextInt() as it is scanning the value as int 
	            onePassword[i] = 
	              values.charAt(rndm_method.nextInt(values.length())); 
	  
	        } 
	        return onePassword; 
			
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

		public static void studentHome(){
			while (true) { 
				
				System.out.println("1. Logout");
				String choice = scanner.nextLine();
				
				switch (choice) {
					case "1":
						logout();
						break;
					default:
						continue;
				}
			}

		}

		public static void instructorHome(){
			while (true) { 
				
				System.out.println("1. Logout");
				String choice = scanner.nextLine();
				
				switch (choice) {
					case "1":
						logout();
						break;
					default:
						continue;
				}
			}

		}

		public static void parentsHome(){
			while (true) { 
				
				System.out.println("1. Logout");
				String choice = scanner.nextLine();
				
				switch (choice) {
					case "1":
						logout();
						break;
					default:
						continue;
				}
			}

		}

}
