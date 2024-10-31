package com.educationCenter.Layout;
import com.educationCenter.Articler_Database_Handler.ArticleDatabase;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.educationCenter.App;
import javafx.geometry.Insets;
import com.educationCenter.App;

public class LoginPage extends Application {
    // A list to store created users
    // new comment
    @Override
    public void start(Stage primaryStage) {

        System.out.println("Connecting to database:");
        if (App.connect()) {
            ArticleDatabase.connect_dataBase();
            if (App.getActiveRole() != null) {
                if (App.getActiveRole().equals("admin")) {
                    showAdminHomepage(primaryStage);
                } else if (App.getActiveRole().equals("instructor")) {
                    showInstructorPage(primaryStage);
                } else if (App.getActiveRole().equals("student")) {
                    showStudentPage(primaryStage);
                }
            }else {

                if (App.fristUser()) {
                    showNewUserCreation(primaryStage, null);
                } else {
                    showLoginPage(primaryStage);
                }
            }
        };
    }

    private void showLoginPage(Stage primaryStage) {
        primaryStage.setTitle("Login Page");

        // Creating the labels and input fields
        Label userLabel = new Label("Username:");
        TextField userTextField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();


        Button loginButton = new Button("Login");
        Button inviteCodeButton = new Button("Enter Invite Code");

        // Adding action to the login button
        loginButton.setOnAction(e -> {
            manageLogin(primaryStage,userTextField.getText(),passwordField.getText());
        });

        // Action for new user button

        // Action for invite code button
        inviteCodeButton.setOnAction(e -> {
            System.out.println("Navigating to Invite Code Page...");
            showInviteCodePage(primaryStage);  // Navigate to the invite code scene
        });

        // Layout for the login page
        VBox vbox = new VBox(10, userLabel, userTextField, passwordLabel, passwordField, loginButton, inviteCodeButton);
        Scene scene = new Scene(vbox, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void showSelectRoletologin(Stage primaryStage,String username) {

        // Label and TextField for username input
        Label updateUserRolesLabel = new Label("Enter application with :");
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
        Button roleButton = new Button("Enter");

        Button backButton = new Button("Back");

        // Action for the Confirm Change button
        roleButton.setOnAction(e -> {
            RadioButton selectedRole = (RadioButton) roleGroup.getSelectedToggle();

            if (username.isEmpty() || selectedRole == null) {
                System.out.println("Select a role.");
            } else {
                String role = selectedRole.getText().toLowerCase();
                App.setActiveRole(role);
                manageLogin(primaryStage,null,null);
            }
        });


        // Action for the Back button
        backButton.setOnAction(e -> showLoginPage(primaryStage));

        // Layout for the Update User Roles scene
        VBox updateRolesLayout = new VBox(10, updateUserRolesLabel,
                adminRole, instructorRole, studentRole,
                roleButton, backButton);
        Scene updateRolesScene = new Scene(updateRolesLayout, 300, 250);

        primaryStage.setScene(updateRolesScene);
        primaryStage.show();
    }
    // Method to navigate to the Admin Homepage
    private void manageLogin(Stage primaryStage,String username,String password) {

        switch (App.appLogin(username, password)) {
            case "multi":
                showSelectRoletologin(primaryStage,username);
                break;

            case "fristTimeLogin":
                showGettingUserInformation(primaryStage);
                break;
            case "admin":
                showAdminHomepage(primaryStage);
                break;
            case "student":
                showStudentPage(primaryStage);
                break;
            case "instructor":
                showInstructorPage(primaryStage);
            case "invalidLogin":
                System.out.println("Invalid username or password.");
                break;
        }

    }

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
            App.logout();
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
        emailField.setPromptText("Enter email");


        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Admin", "Instructor", "Student");
        roleComboBox.setPromptText("Select role");


        Button inviteButton = new Button("Send Invite");
        Button backButton = new Button("Back");

        inviteButton.setOnAction(e -> {
            String email = emailField.getText();

            String role = roleComboBox.getValue().toLowerCase();

            if (email.isEmpty()) {
                System.out.println("Please enter an email.");
            } else {
                if (App.createUser(email,role)){
                    System.out.println("Successfully emailed.");
                }
            }
        });

        backButton.setOnAction(e -> showAdminHomepage(primaryStage));

        VBox inviteLayout = new VBox(10, inviteUserLabel, emailField,roleComboBox, inviteButton, backButton);

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
                if (App.deleteUser(username)){
                    System.out.println("User " + username + " deleted.");
                };
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
                App.userResetEmail(username);
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
        String[][] datas = App.listOfUser();

        System.out.println(datas);
        for (int i = 0; i< datas.length;i++) {
            userListView.getItems().add(datas[i][0] + " - " + datas[i][1] + " - " + datas[i][3].substring(1,datas[i][3].length() -1));
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
        Button addRole = new Button("Add Role");
        Button removeRole = new Button("Delete Role");
        //Hello world
        Button backButton = new Button("Back");

        // Action for the Confirm Change button
        addRole.setOnAction(e -> {
            String username = usernameField.getText();
            RadioButton selectedRole = (RadioButton) roleGroup.getSelectedToggle();

            if (username.isEmpty() || selectedRole == null) {
                System.out.println("Please enter a username and select a role.");
            } else {
                String role = selectedRole.getText().toLowerCase();
                App.addOrRemoveRole(username,"add",role);
                System.out.println("Role of " + username + " updated to " + role);
            }
        });

        removeRole.setOnAction(e -> {
            String username = usernameField.getText();
            RadioButton selectedRole = (RadioButton) roleGroup.getSelectedToggle();

            if (username.isEmpty() || selectedRole == null) {
                System.out.println("Please enter a username and select a role.");
            } else {
                String role = selectedRole.getText();
                App.addOrRemoveRole(username,"remove",role);
                System.out.println("Role of " + username + " remove " + role);
            }
        });

        // Action for the Back button
        backButton.setOnAction(e -> showAdminHomepage(primaryStage));

        // Layout for the Update User Roles scene
        VBox updateRolesLayout = new VBox(10, updateUserRolesLabel, usernameField,
                adminRole, instructorRole, studentRole,
                addRole,removeRole, backButton);
        Scene updateRolesScene = new Scene(updateRolesLayout, 300, 250);

        primaryStage.setScene(updateRolesScene);
        primaryStage.show();
    }

    // Method to navigate to the new user creation page
    public void showNewUserCreation(Stage primaryStage,String email){
        Label createUserLabel = new Label("Create New User:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        PasswordField repasswordField = new PasswordField();
        repasswordField.setPromptText("Re-enter Password");

        Label statusLabel = new Label();  // Label for showing status messages
        Button createUserButton = new Button("Proceed");

        Button backButton = new Button("Back");

        createUserButton.setOnAction(e -> {
            String username = usernameField.getText();
            //String email = emailField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please fill in all fields.");
            } else {
                if (email != null){
                    if(App.setupStudent(username,password,email)){
                        start(primaryStage);
                    };
                }
                else if (App.setupAdministrator(username,password)){
                    start(primaryStage);
                }else{
                    statusLabel.setText("Username already exist.");
                }


            }
        });

        backButton.setOnAction(e -> start(primaryStage));

        VBox newUserLayout = new VBox(10, createUserLabel, usernameField, passwordField,repasswordField, createUserButton, statusLabel, backButton);
        Scene newUserScene = new Scene(newUserLayout, 300, 300);

        primaryStage.setScene(newUserScene);
        primaryStage.show();
    }

    //resetpassword

    public void showResetPassword(Stage primaryStage,String email){

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        PasswordField repasswordField = new PasswordField();
        repasswordField.setPromptText("Re-enter Password");

        Label statusLabel = new Label();  // Label for showing status messages
        Button createUserButton = new Button("Proceed");

        Button backButton = new Button("Back");

        createUserButton.setOnAction(e -> {
            //String email = emailField.getText();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please fill in all fields.");
            } else {
                if(App.resetPasword(email,password)){
                    start(primaryStage);
                }else{
                    statusLabel.setText("Invaild.");
                }


            }
        });

        backButton.setOnAction(e -> start(primaryStage));

        VBox newUserLayout = new VBox(10, passwordField,repasswordField, createUserButton, statusLabel, backButton);
        Scene newUserScene = new Scene(newUserLayout, 300, 300);

        primaryStage.setScene(newUserScene);
        primaryStage.show();
    }


    // Information page

    public void showGettingUserInformation(Stage primaryStage){
        Label createFristName = new Label("Create Frist Name");
        TextField fristNameField = new TextField();
        fristNameField.setPromptText("Username");

        Label createMiddleName = new Label("Create Middle Name:");
        TextField middleNameField = new TextField();
        middleNameField.setPromptText("Middle Name");

        Label createLastName = new Label("Create Last Name:");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");



        Label statusLabel = new Label();  // Label for showing status messages
        Button createUserButton = new Button("Proceed");

        Button backButton = new Button("Back");

        createUserButton.setOnAction(e -> {
            String fristName = fristNameField.getText();
            String middleName = middleNameField.getText();
            String lastName = lastNameField.getText();

            if (fristName.isEmpty() || lastName.isEmpty()) {
                statusLabel.setText("Please fill in all fields.");
            } else {
                if (App.setupUserInformation(fristName,middleName,lastName)){
                    start(primaryStage);
                }else{
                    statusLabel.setText("Username already exist.");
                }


            }
        });

        backButton.setOnAction(e -> start(primaryStage));

        VBox newUserLayout = new VBox(10, createFristName, fristNameField,createMiddleName,middleNameField,createLastName,lastNameField, createUserButton, statusLabel, backButton);
        Scene newUserScene = new Scene(newUserLayout, 300, 300);

        primaryStage.setScene(newUserScene);
        primaryStage.show();
    }

    // Method to navigate to the invite code page
    private void showInviteCodePage(Stage primaryStage) {
        Label emailLabel = new Label("Enter your Email :");
        TextField emailField = new TextField();

        Label inviteLabel = new Label("Enter Invite Code:");
        TextField inviteField = new TextField();

        Button enterInviteCodeButton = new Button("Enter Invite Code");
        Button backButton = new Button("Back");

        enterInviteCodeButton.setOnAction(e -> {
            String email = emailField.getText();
            String inviteCode = inviteField.getText();
            String check = App.loginInvitedUser(email,inviteCode);
            if (check.equals("new")) {
                showNewUserCreation(primaryStage,email);
            }else if(check.equals("reset")){
                showResetPassword(primaryStage,email);
            }
            else {
                System.out.println("Something Failed!");
            }
        });

        backButton.setOnAction(e -> start(primaryStage));

        VBox inviteLayout = new VBox(10,emailLabel,emailField, inviteLabel, inviteField, enterInviteCodeButton, backButton);
        Scene inviteScene = new Scene(inviteLayout, 300, 250);

        primaryStage.setScene(inviteScene);
        primaryStage.show();
    }

    private void showStudentPage(Stage primaryStage) {

        Button loggout = new Button("Logout");


        loggout.setOnAction(e -> {
            App.logout();
            start(primaryStage);
        });

        VBox newUserLayout = new VBox(10, loggout);
        Scene newUserScene = new Scene(newUserLayout, 300, 300);

        primaryStage.setScene(newUserScene);
        primaryStage.show();

    }

    private void showInstructorPage(Stage primaryStage) {


        Button createArticle = new Button("Create Article");
        Button editArticle = new Button("Edit Article");
        Button viewArticle = new Button("View Article");
        Button deleteArticle = new Button("Delete Article");
//        Button backup = new Button("Backup Data");
//        Button restore = new Button("restore Article");
        Button loggout = new Button("Logout");


        createArticle.setOnAction(e -> {
            showCreateArticlePage(primaryStage);
        });

        editArticle.setOnAction(e -> {
            showEditArticlePage(primaryStage);
        });

        viewArticle.setOnAction(e -> {
            showlistArticlePage(primaryStage);
        });

        deleteArticle.setOnAction(e -> {
            showdeleteArticlePage(primaryStage);
        });


//        backupData.setOnAction(e -> {
//            showbackupArticlePage(primaryStage);
//        });
//
//        restoreData.setOnAction(e -> {
//            showRestoreArticlePage(primaryStage);
//        });

        loggout.setOnAction(e -> {
            App.logout();
            start(primaryStage);
        });

        VBox newUserLayout = new VBox(10, createArticle,viewArticle,editArticle,deleteArticle,loggout);
        Scene newUserScene = new Scene(newUserLayout, 300, 300);

        primaryStage.setScene(newUserScene);
        primaryStage.show();

    }
    private void showlistArticlePage(Stage primaryStage) {
        Label listUsersLabel = new Label("List of Articles:");
        Button backButton = new Button("Back");

        ListView<String> ArticleListView = new ListView<>();
        String[][] datas = ArticleDatabase.returnListArticles();
        if (datas != null) {
            System.out.println(datas);
            for (String[] data : datas) {
                StringBuilder userData = new StringBuilder(data[0]);  // Start with user ID
                for (int j = 1; j < data.length; j++) {
                    userData.append(" ").append(data[j]);
                }
                ArticleListView.getItems().add(userData.toString());
            }

            // Handle click events on the user list items
            ArticleListView.setOnMouseClicked(event -> {
                String selectedArticle = ArticleListView.getSelectionModel().getSelectedItem();
                System.out.println(selectedArticle);
                if (selectedArticle != null) {
                    String userId = selectedArticle.split(" ")[0];  // Assuming ID is the first element
                    System.out.println(userId);
                    showViewArticlePage(primaryStage, userId);
                }
            });

            backButton.setOnAction(e -> start(primaryStage));

            VBox listUsersLayout = new VBox(10, listUsersLabel, ArticleListView, backButton);
            Scene listUsersScene = new Scene(listUsersLayout, 300, 300);

            primaryStage.setScene(listUsersScene);
            primaryStage.show();
        }
    }

    private void showViewArticlePage(Stage primaryStage,String articleId) {
        // create a label and text field for showing article title
        Label titleLabel = new Label("Title:");
        TextField titleField = new TextField();
        titleField.setEditable(false); //set non-editable

        //create label and text field for article's difficulty level
        Label levelLabel = new Label("Difficulty Level:");
        TextField levelField = new TextField();
        levelField.setEditable(false); //set non-editable

        // create label and text area for article's description
        Label descriptionLabel = new Label("Description:");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setEditable(false); //set non-editable
        descriptionArea.setWrapText(true);

        //create label and text field for keywords
        Label keywordsLabel = new Label("Keywords:");
        TextField keywordsField = new TextField();
        keywordsField.setEditable(false); //set non-editable

        //create label and text area for reference links
        Label linksLabel = new Label("Reference Links:");
        TextArea linksArea = new TextArea();
        linksArea.setEditable(false); //set non-editable
        linksArea.setWrapText(true);

        //label and text area for main content of the article
        Label contentLabel = new Label("Article Content:");
        TextArea contentArea = new TextArea();
        contentArea.setEditable(false);
        contentArea.setWrapText(true);

        //replace these with actual database calls to load real article data
        String[] data = ArticleDatabase.returnArticle(articleId);
        titleField.setText(data[2]);
        descriptionArea.setText(data[3]);
        levelField.setText(data[4]);
        keywordsField.setText(data[7]);
        linksArea.setText(data[8]);
        contentArea.setText("");

        // create a Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> start(primaryStage));
        //layout for arranging UI components vertically with padding and spacing
        VBox viewArticleLayout = new VBox(10, titleLabel, titleField,
                levelLabel, levelField,
                descriptionLabel, descriptionArea,
                keywordsLabel, keywordsField,
                linksLabel, linksArea,
                contentLabel, contentArea,
                backButton);
        viewArticleLayout.setPadding(new Insets(10)); //add padding for layout spacing

        Scene viewArticleScene = new Scene(viewArticleLayout, 400, 600);
        primaryStage.setScene(viewArticleScene);
        primaryStage.show(); // Show the scene
    }

    private void showdeleteArticlePage(Stage primaryStage) {
        Label deleteArticleLabel = new Label("Delete Article Id:");
        TextField deleteArticleField = new TextField();
        Button deleteArticle = new Button("Delete Article");
        Button backButton = new Button("Back");

        Label errorArea = new Label();

        deleteArticle.setOnAction(e -> {
            if (deleteArticleField.getText().isEmpty()) {
                errorArea.setText("Please enter a valid ID.");
            }else{
                ArticleDatabase.callDeleteArticleByKey(deleteArticleField.getText());
                errorArea.setText("Successfully deleted.");
            }
        });

        backButton.setOnAction(e -> { start(primaryStage); });
        VBox newUserLayout = new VBox(10,deleteArticleLabel,deleteArticleField,deleteArticle,backButton,errorArea);
        Scene newUserScene = new Scene(newUserLayout, 300, 300);

        primaryStage.setScene(newUserScene);
        primaryStage.show();

    }

    private void showEditArticlePage(Stage primaryStage) {
        Label editArticleLabel = new Label("Edit Article");

        //enter the article ID or select from a list
        TextField articleIdField = new TextField();
        articleIdField.setPromptText("Enter Article ID");

        // article title and content
        TextField titleField = new TextField();
        titleField.setPromptText("Enter Article Title");
        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Edit Article Content");
        contentArea.setWrapText(true);

        // button to load the existing article by ID
        Button loadButton = new Button("Load Article");
        loadButton.setOnAction(e -> {
            titleField.setText("Sample Title");
            contentArea.setText("Sample content of the article...");
        });

        //button to save edits
        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> {
            String articleId = articleIdField.getText();
            String title = titleField.getText();
            String content = contentArea.getText();

            // fields are not empty checker
            if (articleId.isEmpty() || title.isEmpty() || content.isEmpty()) {
                System.out.println("Please fill in all fields before saving.");
            } else {
                // Logic to update the article in the database
                System.out.println("Article with ID " + articleId + " updated successfully.");
            }
        });

        // back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> start(primaryStage));
        // layout
        VBox editArticleLayout = new VBox(10, editArticleLabel, articleIdField, loadButton, titleField, contentArea, saveButton, backButton);
        editArticleLayout.setPadding(new Insets(10)); // Add padding around the layout
        //scene and display
        Scene editArticleScene = new Scene(editArticleLayout, 400, 400);
        primaryStage.setScene(editArticleScene);
        primaryStage.show();
    }

    private void showCreateArticlePage(Stage primaryStage) {
        Label createArticleLabel = new Label("Create New Article");

        // Article fields
        TextField titleField = new TextField();
        titleField.setPromptText("Enter Article Title");

        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Enter Article Content");
        contentArea.setWrapText(true);

        TextField authorField = new TextField();
        authorField.setPromptText("Enter Author Name");

        TextField abstractField = new TextField();
        abstractField.setPromptText("Enter Article Abstract");

        TextField keywordsField = new TextField();
        keywordsField.setPromptText("Enter Keywords (comma-separated)");

        TextField referencesField = new TextField();
        referencesField.setPromptText("Enter References");

        ComboBox<String> difficultyComboBox = new ComboBox<>();
        difficultyComboBox.getItems().addAll("Beginner", "Intermediate", "Expert");
        difficultyComboBox.setPromptText("Select Difficulty");

        TextField groupingField = new TextField();
        groupingField.setPromptText("Enter Grouping");

        // Sensitive data fields
        CheckBox sensitiveCheckBox = new CheckBox("Sensitive Data");
        Label sensitiveInfoLabel = new Label("Sensitive Information:");
        TextField nonSensTitleField = new TextField();
        nonSensTitleField.setPromptText("Enter Non-Sensitive Title");
        TextField nonSensAbstractField = new TextField();
        nonSensAbstractField.setPromptText("Enter Non-Sensitive Abstract");
        TextField sensitiveKeyField = new TextField();
        sensitiveKeyField.setPromptText("Enter Sensitive Key");

        // Hide sensitive fields initially
        sensitiveInfoLabel.setVisible(false);
        nonSensTitleField.setVisible(false);
        nonSensAbstractField.setVisible(false);
        sensitiveKeyField.setVisible(false);

        sensitiveCheckBox.setOnAction(e -> {
            boolean isSensitive = sensitiveCheckBox.isSelected();
            sensitiveInfoLabel.setVisible(isSensitive);
            nonSensTitleField.setVisible(isSensitive);
            nonSensAbstractField.setVisible(isSensitive);
            sensitiveKeyField.setVisible(isSensitive);
        });

        Button createButton = new Button("Create Article");
        createButton.setOnAction(e -> {
            String title = titleField.getText();
            String content = contentArea.getText();
            String author = authorField.getText();
            String abstrac = abstractField.getText();
            String keywords = keywordsField.getText();
            String references = referencesField.getText();
            String difficulty = difficultyComboBox.getValue();
            String grouping = groupingField.getText();
            boolean isSensitive = sensitiveCheckBox.isSelected();

            if (title.isEmpty() || content.isEmpty() || author.isEmpty() || abstrac.isEmpty() || keywords.isEmpty() ||
                    references.isEmpty() || difficulty == null || grouping.isEmpty()) {
                System.out.println("Please fill in all fields before creating the article.");
                return;
            }

            try {
                if (isSensitive) {
                    String nonSensTitle = nonSensTitleField.getText();
                    String nonSensAbstrac = nonSensAbstractField.getText();
                    String sensKey = sensitiveKeyField.getText();

                    if (nonSensTitle.isEmpty() || nonSensAbstrac.isEmpty() || sensKey.isEmpty()) {
                        System.out.println("Please fill in all sensitive information fields.");
                        return;
                    }

                    // Call sensitive article creation method in App
                    ArticleDatabase.callCreateArticle(title, content, author, abstrac, keywords, references, difficulty, grouping, nonSensTitle, nonSensAbstrac, sensKey);
                    System.out.println("Sensitive article created successfully.");
                } else {
                    // Call non-sensitive article creation method in App
                    ArticleDatabase.callCreateArticle(title, content, author, abstrac, keywords, references, difficulty, grouping);
                    System.out.println("Non-sensitive article created successfully.");
                }
            } catch (Exception ex) {
                System.out.println("Error creating article: " + ex.getMessage());
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showInstructorPage(primaryStage));

        VBox createArticleLayout = new VBox(10, createArticleLabel, titleField, contentArea, authorField, abstractField,
                keywordsField, referencesField, difficultyComboBox, groupingField, sensitiveCheckBox, sensitiveInfoLabel,
                nonSensTitleField, nonSensAbstractField, sensitiveKeyField, createButton, backButton);
        createArticleLayout.setPadding(new Insets(10));

        Scene createArticleScene = new Scene(createArticleLayout, 400, 600);
        primaryStage.setScene(createArticleScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
