module com.cg {
    requires javafx.controls;
    requires javafx.fxml;
    requires vecmath;
    requires java.desktop;


    opens com.cg to javafx.fxml;
    exports com.cg.ui;
}