package Interface;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RemoveUserPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Remove User");

        Label usernameLabel = new Label("Enter Username to Remove:");
        TextField usernameField = new TextField();
        Button removeButton = new Button("Remove");

        VBox vbox = new VBox(10, usernameLabel, usernameField, removeButton);
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
