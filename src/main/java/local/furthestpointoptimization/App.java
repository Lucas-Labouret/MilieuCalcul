package local.furthestpointoptimization;

import javafx.application.Application;
import javafx.stage.Stage;
import local.furthestpointoptimization.controller.CanvasPopUp;
import local.furthestpointoptimization.model.VertexSet;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        int hexWidth = 7;
        int hexHeight = (int) Math.ceil(hexWidth / Math.sqrt(2));
        int hexCount = (int) ((hexWidth-1)*Math.ceil(hexHeight/2.) + (hexWidth-2)*Math.floor(hexHeight/2.));

        int numVertices = 100;
        double convergenceTolerance = 0.95;

        VertexSet vertexSet = VertexSet.newHexBorderedSet(hexWidth, hexHeight, hexCount);
        vertexSet.delaunayTriangulate();
        vertexSet.optimize(convergenceTolerance);

        int size = 500;
        CanvasPopUp.create(vertexSet, size);
    }

    public static void main(String[] args) {
        launch();
    }
}