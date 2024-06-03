package local.furthestpointoptimization;

import javafx.application.Application;
import javafx.stage.Stage;
import local.furthestpointoptimization.controller.CanvasPopUp;
import local.furthestpointoptimization.model.VertexSet;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        int hexWidth = 9;
        int hexHeight = (int) Math.ceil(hexWidth / Math.sqrt(2));

        int numVertices = 100;
        double convergenceTolerance = 0.95;

        VertexSet onlyBorder = VertexSet.newHexBorderedSet(hexWidth, hexHeight, 0);

        VertexSet unoptimizedShort = VertexSet.newHexBorderedSet(hexWidth, 1, numVertices);
        unoptimizedShort.delaunayTriangulate();

        VertexSet unoptimizedTall = VertexSet.newHexBorderedSet(hexWidth, hexHeight, numVertices);
        unoptimizedTall.delaunayTriangulate();

        int size = 100;
        CanvasPopUp.create(onlyBorder, size);
        CanvasPopUp.create(unoptimizedShort, size);
        CanvasPopUp.create(unoptimizedTall, size);
    }

    public static void main(String[] args) {
        launch();
    }
}