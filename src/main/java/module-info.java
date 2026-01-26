module local.furthestpointoptimization {
    requires javafx.controls;
    requires javafx.base;
    requires transitive javafx.graphics;
    requires commons.math3;
    requires java.desktop;

    opens ui.optimization.mediumApps to javafx.fxml;

    exports computingMedia.media;
    exports cannings;

    exports ui.optimization.mediumApps;
    exports savefileManagers;
    exports ui.common;
    opens savefileManagers to javafx.fxml;
    exports computingMedia.sLoci;
    exports computingMedia.tLoci;
    exports cannings.coords.sCoords;
    exports cannings.coords.tCoords;
    exports cannings.vertexCannings;
    exports cannings.evaluation;
    exports simulatedAnnealing;
    exports misc.linkedList;
    exports computingMedia;
    exports simulatedAnnealing.acceptor;
    exports simulatedAnnealing.evaluator;
    exports simulatedAnnealing.neighborGenerator;
    exports simulatedAnnealing.temperatureRegulator;
    exports simulatedAnnealing.neighborGenerator.neighborSelector;
    exports ui.optimization;
    opens ui.optimization to javafx.fxml;
    exports ui.optimization.utils;
    exports ui;
    opens ui to javafx.fxml;
}