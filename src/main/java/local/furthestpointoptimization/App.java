package local.furthestpointoptimization;

import javafx.application.Application;
import javafx.stage.Stage;
import local.furthestpointoptimization.controller.CanvasPopUp;
import local.furthestpointoptimization.model.VertexSet;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        int hexWidth = 8;
        int hexHeight = (int) Math.ceil(hexWidth / Math.sqrt(2));

        int numVertices = 200;
        double convergenceTolerance = 0.95;

        VertexSet unoptimized = VertexSet.newHexBorderedSet(8, 1, numVertices);
        unoptimized.delaunayTriangulate();

        VertexSet optimized = new VertexSet(unoptimized);
        //optimized.optimize(convergenceTolerance);

        VertexSet bordered = new VertexSet(optimized);
        //bordered.addBorder();

        int size = 100;
        CanvasPopUp.create(unoptimized, size);
        //CanvasPopUp.create(optimized, size);
        //CanvasPopUp.create(bordered, size);
    }

    public static void main(String[] args) {
        launch();
    }
}