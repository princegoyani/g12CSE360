package layout.Interface;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.educationCenter.javafx_maven_project.App;
//import com.educationCenter.javafx_maven_project.DatabaseHelper;

public class LoginPage extends Application {

    // A list to store created users
    private List<User> users = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws SQLException{
        primaryStage.setTitle("Login Page");

        // Creating the labels and input fields
        Label userLabel = new Label("Username:");
        TextField userTextField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        
        Button loginButton = new Button("Login");
        Button newUserButton = new Button("New user? Click here.");
        Button inviteCodeButton = new Button("Enter Invite Code");

        // Adding action to the login button
        loginButton.setOnAction(e -> {
            String username = userTextField.getText();
            String password = passwordField.getText();
            if (authenticateUser(username, password)) {
                System.out.println("User authenticated successfully!");
                showAdminHomepage(primaryStage);  // Navigate to Admin Homepage on successful login
            } else {
                System.out.println("Invalid username or password.");
            }
        });

        // Action for new user button
        newUserButton.setOnAction(e -> {
            System.out.println("Navigating to New User Creation Page...");
            showNewUserCreation(primaryStage);  // Navigate to the new user creation scene
        });

        // Action for invite code button
        inviteCodeButton.setOnAction(e -> {
            System.out.println("Navigating to Invite Code Page...");
            showInviteCodePage(primaryStage);  // Navigate to the invite code scene
        });

        // Layout for the login page
        VBox vbox = new VBox(10, userLabel, userTextField, passwordLabel, passwordField, loginButton, newUserButton, inviteCodeButton);
        Scene scene = new Scene(vbox, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Placeholder authentication logic
    private boolean authenticateUser(String username, String password) {
        // Iterate through users to find a match
        if (App.appLogin(username, password)){
            return true;
        };
        
        return false;
    }

    // Method to navigate to the Admin Homepage
    private void showAdminHomepage(Stage primaryStage) {
        // Create buttons for the admin options
        Button logoutButton = new Button("Logout");
        Button inviteUserButton = new Button("Invite User");
        Button deleteUserButton = new Button("Delete User");
        Button resetUserButton = new Button("Reset User");
        Button listUsersButton = new Button("List Users");
        Button updateUserRolesButton = new Button("Update User Roles");

        // Action for logout button to return to the login page
        logoutButton.setOnAction(e -> {
            start(primaryStage);  // Navigate back to the login page
        });

        // Button actions
        inviteUserButton.setOnAction(e -> showInviteUserPage(primaryStage));
        deleteUserButton.setOnAction(e -> showDeleteUserPage(primaryStage));
        resetUserButton.setOnAction(e -> showResetUserPage(primaryStage));
        listUsersButton.setOnAction(e -> showListUsersPage(primaryStage));
        updateUserRolesButton.setOnAction(e -> showUpdateUserRolesPage(primaryStage));

        // Layout for the Admin Homepage
        VBox adminLayout = new VBox(10, logoutButton, inviteUserButton, deleteUserButton, resetUserButton, listUsersButton, updateUserRolesButton);
        Scene adminScene = new Scene(adminLayout, 300, 300);

        primaryStage.setScene(adminScene);
        primaryStage.show();
    }

    // Method to navigate to the invite user page
    private void showInviteUserPage(Stage primaryStage) {
        Label inviteUserLabel = new Label("Invite New User:");
        TextField emailField = new TextField();
        TextField roleField = new TextField();
        emailField.setPromptText("Enter email");
        roleField.setPromptText("Enter role");
        Button inviteButton = new Button("Send Invite");
        Button backButton = new Button("Back");

        inviteButton.setOnAction(e -> {
            String email = emailField.getText();
            String role = roleField.getText();
            if (email.isEmpty()) {
                System.out.println("Please enter an email.");
            } else {
                try{App.generateUser(email,role);}catch (SQLException f){}
                
            }
        });

        backButton.setOnAction(e -> showAdminHomepage(primaryStage));

        VBox inviteLayout = new VBox(10, inviteUserLabel, emailField, inviteButton, backButton);
        Scene inviteScene = new Scene(inviteLayout, 300, 200);

        primaryStage.setScene(inviteScene);
        primaryStage.show();
    }

    // Method to navigate to the delete user page
    private void showDeleteUserPage(Stage primaryStage) {
        Label deleteUserLabel = new Label("Delete User:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username to delete");
        Button deleteButton = new Button("Delete User");
        Button backButton = new Button("Back");

        deleteButton.setOnAction(e -> {
            String username = usernameField.getText();
            if (username.isEmpty()) {
                System.out.println("Please enter a username.");
            } else {
                System.out.println("User " + username + " deleted.");
            }
        });

        backButton.setOnAction(e -> showAdminHomepage(primaryStage));

        VBox deleteLayout = new VBox(10, deleteUserLabel, usernameField, deleteButton, backButton);
        Scene deleteScene = new Scene(deleteLayout, 300, 200);

        primaryStage.setScene(deleteScene);
        primaryStage.show();
    }

    // Method to navigate to the reset user page
    private void showResetUserPage(Stage primaryStage) {
        Label resetUserLabel = new Label("Reset User Password:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username to reset");
        Button resetButton = new Button("Reset Password");
        Button backButton = new Button("Back");

        resetButton.setOnAction(e -> {
            String username = usernameField.getText();
            if (username.isEmpty()) {
                System.out.println("Please enter a username.");
            } else {
                System.out.println("Password reset for user " + username);
            }
        });

        backButton.setOnAction(e -> showAdminHomepage(primaryStage));

        VBox resetLayout = new VBox(10, resetUserLabel, usernameField, resetButton, backButton);
        Scene resetScene = new Scene(resetLayout, 300, 200);

        primaryStage.setScene(resetScene);
        primaryStage.show();
    }

    // Method to navigate to the list users page
    private void showListUsersPage(Stage primaryStage) {
        Label listUsersLabel = new Label("List of Users:");
        Button backButton = new Button("Back");

        ListView<String> userListView = new ListView<>();
        for (User user : users) {
            userListView.getItems().add(user.getUsername() + " - " + user.getEmail());
        }

        backButton.setOnAction(e -> showAdminHomepage(primaryStage));

        VBox listUsersLayout = new VBox(10, listUsersLabel, userListView, backButton);
        Scene listUsersScene = new Scene(listUsersLayout, 300, 300);

        primaryStage.setScene(listUsersScene);
        primaryStage.show();
    }

    // Method to navigate to the update user roles page
    private void showUpdateUserRolesPage(Stage primaryStage) {
        // Label and TextField for username input
        Label updateUserRolesLabel = new Label("Enter the username you want to change:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        // Radio buttons for selecting roles
        RadioButton adminRole = new RadioButton("Admin");
        RadioButton instructorRole = new RadioButton("Instructor");
        RadioButton studentRole = new RadioButton("Student");

        // Group the radio buttons so only one can be selected at a time
        ToggleGroup roleGroup = new ToggleGroup();
        adminRole.setToggleGroup(roleGroup);
        instructorRole.setToggleGroup(roleGroup);
        studentRole.setToggleGroup(roleGroup);

        // Confirm button
        Button confirmChangeButton = new Button("Confirm Change");
        Button backButton = new Button("Back");

        // Action for the Confirm Change button
        confirmChangeButton.setOnAction(e -> {
            String username = usernameField.getText();
            RadioButton selectedRole = (RadioButton) roleGroup.getSelectedToggle();

            if (username.isEmpty() || selectedRole == null) {
                System.out.println("Please enter a username and select a role.");
            } else {
                String role = selectedRole.getText();
                // Logic to update the user's role
                System.out.println("Role of " + username + " updated to " + role);
            }
        });

        // Action for the Back button
        backButton.setOnAction(e -> showAdminHomepage(primaryStage));

        // Layout for the Update User Roles scene
        VBox updateRolesLayout = new VBox(10, updateUserRolesLabel, usernameField, 
                                          adminRole, instructorRole, studentRole, 
                                          confirmChangeButton, backButton);
        Scene updateRolesScene = new Scene(updateRolesLayout, 300, 250);

        primaryStage.setScene(updateRolesScene);
        primaryStage.show();
    }

    // Method to navigate to the new user creation page
    private void showNewUserCreation(Stage primaryStage) throws SQLException{
        Label createUserLabel = new Label("Create New User:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Label statusLabel = new Label();  // Label for showing status messages
        Button createUserButton = new Button("Create User");
        Button backButton = new Button("Back");

        createUserButton.setOnAction(e -> {
            String username = usernameField.getText();
            //String email = emailField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please fill in all fields.");
            } else {
                App.setupAdministrator(username, password);
                start(primaryStage);
            }
        });

        backButton.setOnAction(e -> start(primaryStage));

        VBox newUserLayout = new VBox(10, createUserLabel, usernameField, emailField, passwordField, createUserButton, statusLabel, backButton);
        Scene newUserScene = new Scene(newUserLayout, 300, 300);

        primaryStage.setScene(newUserScene);
        primaryStage.show();
    }

    // Method to navigate to the invite code page
    private void showInviteCodePage(Stage primaryStage) {
        Label inviteLabel = new Label("Enter Invite Code:");
        TextField inviteField = new TextField();
        Button enterInviteCodeButton = new Button("Enter Invite Code");
        Button backButton = new Button("Back");

        enterInviteCodeButton.setOnAction(e -> {
            String inviteCode = inviteField.getText();
            InviteCode inviteCodeObj = new InviteCode(inviteCode);
            if (inviteCodeObj.isValidCode()) {
                System.out.println("Invite code is valid!");
            } else {
                System.out.println("Invalid invite code.");
            }
        });

        backButton.setOnAction(e -> start(primaryStage));

        VBox inviteLayout = new VBox(10, inviteLabel, inviteField, enterInviteCodeButton, backButton);
        Scene inviteScene = new Scene(inviteLayout, 300, 250);

        primaryStage.setScene(inviteScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
