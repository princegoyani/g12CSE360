module com.example.cse360javaproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires javafx.graphics;
    requires org.bouncycastle.provider;
    requires com.h2database;

    exports com.educationCenter.Layout to javafx.graphics;
    exports com.educationCenter;
    opens com.educationCenter to javafx.fxml;

}
