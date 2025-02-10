module local.furthestpointoptimization {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.desktop;


    opens local.furthestpointoptimization to javafx.fxml;
    exports local.furthestpointoptimization;
    opens local.ui to javafx.fxml;
    // exports local.Ui to javafx.graphics;
    exports local.ui;
    exports local.ui.vertexSetScene;
    opens local.ui.vertexSetScene to javafx.fxml;
}