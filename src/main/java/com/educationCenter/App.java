package com.educationCenter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

import com.educationCenter.User_Database_Handler.MessageDatabaseHelper;
import com.educationCenter.User_Database_Handler.UserDatabaseHelper;

public class App {
	private static final UserDatabaseHelper USER_DATABASE_HELPER = new UserDatabaseHelper();
	private static final MessageDatabaseHelper MESSAGE_DATABASE_HELPER = new MessageDatabaseHelper();
	private static final Scanner scanner = new Scanner(System.in);
	private static String username;
	private static String password;
	private static String activeRole;
	private static ArrayList<String> searchHistory = new ArrayList<>();
	public static String getActiveRole(){
		return activeRole;
	}
	public static String getUsername(){return username;}
	public static void setActiveRole(String role){
		activeRole = role;
	}

	private static String[] datas; 

	public static void main(String[] args) {

    	try {
    		USER_DATABASE_HELPER.userConnectToDatabase(); // connecting to database
			MESSAGE_DATABASE_HELPER.messageConnectToDatabase();
    		while (true){

	    		if (USER_DATABASE_HELPER.isDatabaseEmpty()) {
					System.out.println( "In-Memory Database  is empty" );
					//set up administrator access

					if (setupAdministrator(username,password)) {
						// add if needed
					}else {
						System.out.println("Try Again !");
					}
				}
	    		else {
					if (activeRole == null){
						System.out.println( "1.Login 2.ForgotPassword" );
						String choice = scanner.nextLine();

						switch (choice) {
							case "1":
								if(appLogin(username,password) == "loggedIn"){
									System.out.println("Invalid Login!");
								}
								continue;
							case "2":
									System.out.println("Enter Username: ");
									String forgetUsername = scanner.nextLine();
									//resetPasword(forgetUsernamem);
									continue;
								default:
									continue;
							}

					}else{
							switch (activeRole) {
								case "admin":
									adminHome();
									break;
								case "student":
									studentHome();
									break;
								case "instructor":
									instructorHome();
									break;
								case "parents":
									parentsHome();
										break;

								default:
									break;
							}
				}
			}
			continue;
		}}catch (SQLException e) {
    		System.out.println(e.getMessage());
    	}
    	USER_DATABASE_HELPER.closeConnection();

    }


	public static boolean connect(){
		try {
			MESSAGE_DATABASE_HELPER.messageConnectToDatabase();
			if (USER_DATABASE_HELPER.userConnectToDatabase()) {
				return true;
			}
		}catch (SQLException e){
			System.out.println(e.getMessage());
		}
		return false;
	}

	public static boolean fristUser() {
		try {
			if (USER_DATABASE_HELPER.isDatabaseEmpty()) {
				return true;
			}

		}catch (SQLException e) {
			System.out.println(e.getMessage());
	}
		return false;
	}



	public static boolean createUser(String testEmail,String testRole){

		String[] user ;

		user = USER_DATABASE_HELPER.doesEmailExist(testEmail);
		if (user != null){
			System.out.println("Already Exists Username, Try Again!");
			return false;
		}
		
		char[] onetimeCode = generateRandomOneTimeCode();
		String onetimeCodeString = new String(onetimeCode);
		String expiringDateTime = LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ISO_DATE_TIME);
		String codeDetails = expiringDateTime+" "+onetimeCodeString;
		String[] userRoles = {testRole};
		try {
			USER_DATABASE_HELPER.generateUserByAdmin(testEmail, userRoles, codeDetails);
			return true;

		}catch (SQLException e) {
			System.out.println(e);

		}
		return false;
		}
	public static boolean userResetEmail(String username){
		//date

		String[] user = USER_DATABASE_HELPER.doesUserExist(username);

		if (user == null){
			System.out.println("User not exist!");
			return false;
		}

		char[] onetimeCode = generateRandomOneTimeCode();
		String onetimeCodeString = new String(onetimeCode);
		String expiringDateTime = LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ISO_DATE_TIME);
		String codeDetails = expiringDateTime+" "+onetimeCodeString;

		try {
			USER_DATABASE_HELPER.resetbyemail(username, codeDetails);
			return true;

		}catch (SQLException e) {
			System.out.println(e);

		}
		return false;

	}


	public static boolean resetPasword(String email,String passWord){
		try {
			String[] collectedData = USER_DATABASE_HELPER.doesEmailExist(email);

			if (collectedData == null) {
				System.out.println("No user with given USERNAME");
				return false;
			}

			if (!USER_DATABASE_HELPER.updatePassword(email, passWord)) {
				return false;
			}
			;
			password = passWord;
			System.out.println("Update Succefull");
			return true;
		}catch (SQLException e){
			System.out.println(e);
		}
		return false;
	
	}
	
	public static void logout(){
		username=null;
		password=null;
		datas=null;
		activeRole=null;
		clearSearchHistory();
		}
	public static int getUserId(String user){
		return USER_DATABASE_HELPER.getUserId(user);
	}
    
	 public static boolean setupAdministrator(String userName, String passWord) {
		 try {
			 String[] roles = {"admin"};

			 USER_DATABASE_HELPER.register(userName, passWord, roles);

			 username = userName;
			 password = passWord;
			 activeRole = "admin";
			 System.out.println("Administrator setup completed.");

			 return true;
		 } catch (SQLException e){
			 System.out.println(e);
		 }
		 return false;
	}

	public static boolean setupStudent(String userName, String passWord,String email) {
		try {
			String[] roles = {"student"};
			USER_DATABASE_HELPER.studentRegister(userName, passWord,email);

			System.out.println("Administrator setup completed.");

			return true;
		} catch (SQLException e){
			System.out.println(e);
		}
		return false;
	}
	 
		public static String appLogin(String loginName,String credentials) {
			try {
				if (username!= null && activeRole != null){
					System.out.println(activeRole);
					return activeRole;
				}

				datas = USER_DATABASE_HELPER.login(loginName, credentials);
				System.out.println(datas);
				if (datas != null) {
					System.out.println(datas[3]);
					username = loginName;
					password = credentials;
					String[] roles = USER_DATABASE_HELPER.getRoleArray(username,password);
					if (roles.length > 1){
						return "multi";
					}

					activeRole=roles[0];
					if (activeRole.equals("admin")){
						return "admin";
					}

					if (datas[5] == null) {
						return "fristTimeLogin";
					}

					System.out.println(activeRole);
					return activeRole;
				}

			}catch(SQLException e){
				System.out.println(e);
			}
			return "invalidLogin";
		}


		public static String returnUsername(int userid){
			return USER_DATABASE_HELPER.getUsername(userid);
		}

		public static String loginInvitedUser(String email, String inputCode){
			//date
			try {String onetimecode = USER_DATABASE_HELPER.checkInvitedUser(email);

			if (onetimecode == null){
				return "invalid";
			}

			String[] onetimearry = onetimecode.split(" ");
			System.out.println(onetimearry[0] + " " + onetimearry[1]);
			
			if (!LocalDateTime.now().isBefore(LocalDateTime.parse(onetimearry[0], DateTimeFormatter.ISO_DATE_TIME))){
				System.out.println("Credentials Expired!");
				return "invalid";
			};

			//passwordmatch
			if (!inputCode.equals(onetimearry[1])){
				System.out.println("Wrong Password! Try Again");
				return "invalid";
			}
			String[] data = USER_DATABASE_HELPER.doesEmailExist(email);
			if (data[1] == null){
				return "new";
			}
			return "reset";

			}catch (SQLException e){ System.out.println(e);return "invalid";}
		
		}


		public static boolean setupUserInformation(String fristName, String middleName, String lastName){

			try {
				USER_DATABASE_HELPER.displayUsersByAdmin();
				String[] data = USER_DATABASE_HELPER.doesUserExist(username);
				if (data != null) {
					System.out.println("Admin login successful.");
					USER_DATABASE_HELPER.displayUsersByAdmin();
					String[] userData = {username, fristName, middleName, lastName};
					if (USER_DATABASE_HELPER.updateUserInformation(username, userData)) {
						return true;
					}
					;
				} else {
					System.out.println("Invalid admin credentials. Try again!!");
					return false;
				}
			}catch (SQLException e){
				System.out.print(e);
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

		public static boolean deleteUser(String inputUser){
//			System.out.println("Enter Username : ");
//			String inputUser = scanner.nextLine();
		try {

			String[] collectedUser = USER_DATABASE_HELPER.doesUserExist(inputUser);
			if (collectedUser == null) {
				System.out.println("No such user!");
				return false;
			}

			if (USER_DATABASE_HELPER.deleteUser(inputUser)) {
				System.out.println("Deleted Successfully");
				return true;
			}

		}catch (SQLException e){
			System.out.println(e);
		}
			return false;
			
		};

		public static boolean addOrRemoveRole(String inputUser, String whatAction,String inputRole) {
			try {

				String[] collectedUser = USER_DATABASE_HELPER.doesUserExist(inputUser);
				if (collectedUser == null) {
					System.out.println("No such user!");
					return false;
				}

				System.out.println("User with roles : " + collectedUser[3]);

				String updateRoles = collectedUser[3];
				switch (whatAction) {

					case "add":
						updateRoles = collectedUser[3].substring(0, collectedUser[3].length() - 1) + "," + inputRole + "]";
						break;
					case "remove":
						updateRoles = collectedUser[3].replace(inputRole, "");
						updateRoles = updateRoles.replace(inputRole, " , ");
						break;
					default:
						throw new AssertionError();
				}
				USER_DATABASE_HELPER.updateRole(inputUser, updateRoles);
			}catch (SQLException e) {
				System.out.println(e);
			}
			return false;
		}

		public static String[][] listOfUser(){
			try {
				return USER_DATABASE_HELPER.getAllUsers();
			}catch (SQLException e){
				System.out.println(e);
			}
			return null;
		}
	public static String[][] listOfMessages(){
		try {
			System.out.println("Returning list of messages...");
			return MESSAGE_DATABASE_HELPER.getAllMessages();
		}catch (SQLException e){
			System.out.println(e);
		}
		return null;
	}
		public static void adminHome() throws SQLException {
			System.out.println("1.logout 2.Add User 3. Delete User 4.List Users 5. Add Role to user");
			String command = scanner.nextLine();
			switch (command) {
			case "1": {
				logout();
				break;
			}
			case "2":{
				//
				break;
			}
			case "3":{
				//deleteUser(username);
				break;
			}
			case "4":{
				USER_DATABASE_HELPER.displayUsersByAdmin();
				break;
			}
			case "5":{
				//addOrRemoveRole();
				break;
			}
			default:
				adminHome();
			}
			
		}
		public static void studentHome(){
			
				
			System.out.println("1. Logout");
			String choice = scanner.nextLine();
			
			switch (choice) {
				case "1":
					logout();
					break;
				default:
					studentHome();
			}

		}

		public static void instructorHome(){
			
				
				System.out.println("1. Logout");
				String choice = scanner.nextLine();
				
				switch (choice) {
					case "1":
						logout();
						break;
					default:
						instructorHome();
				}
			}

		

		public static void parentsHome(){
			
				
				System.out.println("1. Logout");
				String choice = scanner.nextLine();
				
				switch (choice) {
					case "1":
						logout();
						break;
					default:
						parentsHome();
				}
			}

	public static void sendGenericMessage(String message) {
		if (message != null && !message.isEmpty()) {
			try {
				String searches = "No Searches";
				if(!getSearchHistory().isEmpty()) {
					searches = "";
					for (String i : getSearchHistory()) {
						searches = searches + " " + i;
					}
				}
				MESSAGE_DATABASE_HELPER.addMessage(getUserId(getUsername()), 0, message, searches);
				System.out.println("Sending generic message to help system: " + message);
			} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
		} else {
			System.out.println("Message is empty. Cannot send.");
		}
	}

	public static void sendSpecificMessage(String message) {
		if (message != null && !message.isEmpty()) {
			try {
				String searches = "No Searches";
				if(!getSearchHistory().isEmpty()) {
					searches = "";
					for (String i : getSearchHistory()) {
						searches = searches + " " + i;
					}
				}
				MESSAGE_DATABASE_HELPER.addMessage(getUserId(getUsername()), 1, message, searches);
				System.out.println("Sending specific message to help system: " + message);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		} else {
			System.out.println("Message is empty. Cannot send.");
		}
	}
	public static void addToSearchHistory(String history){
		searchHistory.add(history);
	}
	public static void clearSearchHistory(){
		searchHistory.clear();
	}
	public static ArrayList<String> getSearchHistory(){
		return searchHistory;
	}
	public static void clearAllMessages() throws SQLException {
		MESSAGE_DATABASE_HELPER.clearAllMessages();
	}
    public static boolean canRemoveAdminRole(String username) {

        return false;
    }
}
