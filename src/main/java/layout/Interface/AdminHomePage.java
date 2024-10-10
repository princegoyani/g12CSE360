package layout.Interface;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminHomePage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Admin Homepage");

        Button manageUsersBtn = new Button("Manage Users");
        Button backupRestoreBtn = new Button("Backup & Restore");
        Button logoutBtn = new Button("Logout");

        // Layout and scene setup
        VBox vbox = new VBox(10, manageUsersBtn, backupRestoreBtn, logoutBtn);
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
