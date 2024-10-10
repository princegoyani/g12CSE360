package Interface;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NewUserCreation extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("New User Creation");

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Button createButton = new Button("Create User");

        VBox vbox = new VBox(10, usernameLabel, usernameField, emailLabel, emailField, createButton);
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
