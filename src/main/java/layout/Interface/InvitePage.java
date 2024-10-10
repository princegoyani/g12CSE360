package layout.Interface;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InvitePage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Invite Page");

        // Create the role selection using radio buttons
        ToggleGroup roleGroup = new ToggleGroup();

        RadioButton adminRadio = new RadioButton("Admin");
        adminRadio.setToggleGroup(roleGroup);
        RadioButton instructorRadio = new RadioButton("Instructor");
        instructorRadio.setToggleGroup(roleGroup);
        RadioButton studentRadio = new RadioButton("Student");
        studentRadio.setToggleGroup(roleGroup);

        // Confirm Invite Button
        Button confirmInviteButton = new Button("Confirm Invite");

        // Label to display the generated invite code
        Label inviteCodeLabel = new Label();

        // Set action for the confirm button
        confirmInviteButton.setOnAction(e -> {
            // Generate invite code (you can change the logic for code generation)
            String inviteCode = "IS" + System.currentTimeMillis();
            
            // Display the invite code
            inviteCodeLabel.setText("Invite code successfully generated: " + inviteCode);
        });

        // Layout setup
        VBox layout = new VBox(10);
        layout.getChildren().addAll(new Label("Which roles do you want the user to have"), 
                                    adminRadio, instructorRadio, studentRadio, 
                                    confirmInviteButton, inviteCodeLabel);

        // Create the scene and show it
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
