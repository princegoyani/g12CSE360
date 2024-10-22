package com.example.cse360javaproject;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import com.educationCenter.Database.DatabaseHelper;

public class App {
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	private static final Scanner scanner = new Scanner(System.in);
	private static String username;
	private static String password;
	private static String activeRole;
	public static String getActiveRole(){
		return activeRole;
	}
	public static void setActiveRole(String role){
		activeRole = role;
	}

	private static String[] datas; 

	public static void main(String[] args) {

    	try {

    		databaseHelper.connectToDatabase(); // connecting to database

    		while (true){

	    		if (databaseHelper.isDatabaseEmpty()) {
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
    	databaseHelper.closeConnection();

    }


	public static boolean connect(){
		try {
			if (databaseHelper.connectToDatabase()) {
				return true;
			}
		}catch (SQLException e){
			System.out.println(e.getMessage());
		}
		return false;
	}

	public static boolean fristUser() {
		try {
			if (databaseHelper.isDatabaseEmpty()) {
				return true;
			}

		}catch (SQLException e) {
			System.out.println(e.getMessage());
	}
		return false;
	}


	public static boolean createUser(String testEmail,String testRole){

		String[] user ;

		user = databaseHelper.doesEmailExist(testEmail);
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
			databaseHelper.generateUserByAdmin(testEmail, userRoles, codeDetails);
			return true;

		}catch (SQLException e) {
			System.out.println(e);

		}
		return false;
		}
	public static boolean userResetEmail(String username){
		//date

		String[] user = databaseHelper.doesUserExist(username);

		if (user == null){
			System.out.println("User not exist!");
			return false;
		}

		char[] onetimeCode = generateRandomOneTimeCode();
		String onetimeCodeString = new String(onetimeCode);
		String expiringDateTime = LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ISO_DATE_TIME);
		String codeDetails = expiringDateTime+" "+onetimeCodeString;

		try {
			databaseHelper.resetbyemail(username, codeDetails);
			return true;

		}catch (SQLException e) {
			System.out.println(e);

		}
		return false;

	}


	public static boolean resetPasword(String email,String passWord){
		try {
			String[] collectedData = databaseHelper.doesEmailExist(email);

			if (collectedData == null) {
				System.out.println("No user with given USERNAME");
				return false;
			}

			if (!databaseHelper.updatePassword(email, passWord)) {
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
		}
    
	 public static boolean setupAdministrator(String userName, String passWord) {
		 try {
			 String[] roles = {"admin"};

			 databaseHelper.register(userName, passWord, roles);

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
			databaseHelper.studentRegister(userName, passWord,email);

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

				datas = databaseHelper.login(loginName, credentials);
				System.out.println(datas);
				if (datas != null) {
					System.out.println(datas[3]);
					username = loginName;
					password = credentials;
					String[] roles = databaseHelper.getRoleArray(username,password);
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




		public static String loginInvitedUser(String email, String inputCode){
			//date
			try {String onetimecode = databaseHelper.checkInvitedUser(email);

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
			String[] data = databaseHelper.doesEmailExist(email);
			if (data[1] == null){
				return "new";
			}
			return "reset";

			}catch (SQLException e){ System.out.println(e);return "invalid";}
		
		}


		public static boolean setupUserInformation(String fristName, String middleName, String lastName){

			try {
				databaseHelper.displayUsersByAdmin();
				String[] data = databaseHelper.doesUserExist(username);
				if (data != null) {
					System.out.println("Admin login successful.");
					databaseHelper.displayUsersByAdmin();
					String[] userData = {username, fristName, middleName, lastName};
					if (databaseHelper.updateUserInformation(username, userData)) {
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

			String[] collectedUser = databaseHelper.doesUserExist(inputUser);
			if (collectedUser == null) {
				System.out.println("No such user!");
				return false;
			}

			if (databaseHelper.deleteUser(inputUser)) {
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

				String[] collectedUser = databaseHelper.doesUserExist(inputUser);
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
				databaseHelper.updateRole(inputUser, updateRoles);
			}catch (SQLException e) {
				System.out.println(e);
			}
			return false;
		}

		public static String[][] listOfUser(){
			try {
				return databaseHelper.getAllUsers();
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
				databaseHelper.displayUsersByAdmin();
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

		

}
