module local.furthestpointoptimization {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.desktop;

    opens local.ui to javafx.fxml;
    opens local.ui.mediumScene to javafx.fxml;

    exports local.computingMedium;
    exports local.computingMedium.media;
    exports local.computingMedium.miseEnBoite;

    exports local.misc;
    
    exports local.ui;
    exports local.ui.mediumScene;
    exports local.ui.savefileManager;
    exports local.ui.view;
    opens local.ui.savefileManager to javafx.fxml;
    exports local.misc.geometry;
}