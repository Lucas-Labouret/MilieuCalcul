module local.furthestpointoptimization {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;


    opens local.furthestpointoptimization to javafx.fxml;
    exports local.furthestpointoptimization;
}