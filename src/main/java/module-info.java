module com.cg {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.cg.ui to javafx.fxml;
    exports com.cg.ui;
}