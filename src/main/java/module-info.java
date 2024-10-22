module com.example.cse360javaproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires javafx.graphics;

    opens com.example.cse360javaproject to javafx.fxml;
    exports com.educationCenter.Layout to javafx.graphics;
    exports com.example.cse360javaproject;

}
