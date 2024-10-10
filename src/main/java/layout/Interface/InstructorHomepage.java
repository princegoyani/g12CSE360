package layout.Interface;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InstructorHomepage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Instructor Homepage");

        Button viewStudentsBtn = new Button("View Students");
        Button viewReportsBtn = new Button("View Reports");
        Button logoutBtn = new Button("Logout");

        VBox vbox = new VBox(10, viewStudentsBtn, viewReportsBtn, logoutBtn);
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
