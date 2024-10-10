package layout.Interface;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InvitePage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Invite Page");

        Label inviteLabel = new Label("Enter your invite code:");
        TextField inviteField = new TextField();
        Button submitButton = new Button("Submit");

        VBox vbox = new VBox(10, inviteLabel, inviteField, submitButton);
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
