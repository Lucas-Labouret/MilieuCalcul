module local.furthestpointoptimization {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.desktop;


    opens local.furthestpointoptimization to javafx.fxml;
    exports local.furthestpointoptimization;
    opens local.Ui to javafx.fxml;
    // exports local.Ui to javafx.graphics;
    exports local.Ui;
}