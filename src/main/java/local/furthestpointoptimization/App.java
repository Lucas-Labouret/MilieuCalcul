package local.furthestpointoptimization;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Application;
import javafx.stage.Stage;
import local.furthestpointoptimization.controller.CanvasPopUp;
import local.furthestpointoptimization.model.CalculEfficiency;
import local.furthestpointoptimization.model.Coord;
import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        int hexWidth = 7;
        int hexHeight = (int) Math.ceil(hexWidth / Math.sqrt(2));
        int hexCount = (int) ((hexWidth-1)*Math.ceil(hexHeight/2.) + (hexWidth-2)*Math.floor(hexHeight/2.));

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
        System.out.println("Hello world");
        
        launch();
        // test_perf_triangulation();
        // test();
        // System.out.println("Finito");
        // System.exit(0);
    }
}