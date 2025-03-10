module local.furthestpointoptimization {
    requires javafx.controls;
    requires javafx.base;
    requires transitive javafx.graphics;

    opens local.ui to javafx.fxml;
    opens local.ui.mediumApps to javafx.fxml;

    exports local.computingMedia.media;
    exports local.computingMedia.miseEnBoite;

    exports local.misc;
    
    exports local.ui;
    exports local.ui.mediumApps;
    exports local.savefileManagers;
    exports local.ui.utils;
    opens local.savefileManagers to javafx.fxml;
    exports local.computingMedia.geometry;
}