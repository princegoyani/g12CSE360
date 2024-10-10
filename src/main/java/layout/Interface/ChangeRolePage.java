package layout.Interface;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChangeRolePage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Change Role");

        Label label = new Label("Select a new role:");
        ListView<String> roleList = new ListView<>();
        roleList.getItems().addAll("Admin", "Student", "Instructor");

        Button confirmBtn = new Button("Confirm");
        Button cancelBtn = new Button("Cancel");

        VBox vbox = new VBox(10, label, roleList, confirmBtn, cancelBtn);
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
