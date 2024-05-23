module local.furthestpointoptimization {
    requires javafx.controls;
    requires javafx.fxml;


    opens local.furthestpointoptimization to javafx.fxml;
    exports local.furthestpointoptimization;
}