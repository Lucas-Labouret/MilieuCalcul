module local.furthestpointoptimization {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.desktop;

    opens local.ui to javafx.fxml;
    opens local.ui.vertexSetScene to javafx.fxml;

    exports local.computingMedium;
    exports local.computingMedium.vertexSets;
    exports local.computingMedium.miseEnBoite;

    exports local.misc;
    
    exports local.ui;
    exports local.ui.vertexSetScene;
    exports local.ui.savefileManager;
    exports local.ui.view;
    opens local.ui.savefileManager to javafx.fxml;
}