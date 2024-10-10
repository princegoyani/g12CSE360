package Interface;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ListUsersPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("List of Users");

        ListView<String> userList = new ListView<>();
        userList.getItems().addAll("User1", "User2", "User3");

        VBox vbox = new VBox(userList);
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
