module local.furthestpointoptimization {
    requires javafx.controls;
    requires javafx.base;
    requires transitive javafx.graphics;
    requires commons.math3;

    opens local.ui to javafx.fxml;
    opens local.ui.mediumApps to javafx.fxml;

    exports local.computingMedia.media;
    exports local.computingMedia.cannings;
    
    exports local.ui;
    exports local.ui.mediumApps;
    exports local.savefileManagers;
    exports local.ui.utils;
    opens local.savefileManagers to javafx.fxml;
    exports local.computingMedia.sLoci;
    exports local.computingMedia.tLoci;
    exports local.computingMedia.cannings.coords.sCoords;
    exports local.computingMedia.cannings.coords.tCoords;
    exports local.computingMedia.cannings.vertexCannings;
    exports local.computingMedia.cannings.evaluation;
    exports local.simulatedAnnealing;
    exports local.misc.linkedList;
    exports local.computingMedia;
    exports local.simulatedAnnealing.acceptor;
    exports local.simulatedAnnealing.evaluator;
    exports local.simulatedAnnealing.neighborGenerator;
    exports local.simulatedAnnealing.temperatureRegulator;
    exports local.simulatedAnnealing.neighborGenerator.neighborSelector;
}