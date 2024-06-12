package local.furthestpointoptimization;

import javafx.application.Application;
import javafx.stage.Stage;
import local.furthestpointoptimization.controller.CanvasPopUp;
import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;
import local.furthestpointoptimization.model.miseEnBoite.Coord;
import local.furthestpointoptimization.model.miseEnBoite.MiseEnBoite;
import local.furthestpointoptimization.model.miseEnBoite.TopLeftDistanceMeb;

import java.util.HashMap;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        int hexWidth = 15;
        int hexHeight = (int) Math.ceil(hexWidth / Math.sqrt(2));
        int hexCount = (int) ((hexWidth-1)*Math.ceil(hexHeight/2.) + (hexWidth-2)*Math.floor(hexHeight/2.));
        double convergenceTolerance = 1;

        VertexSet vertexSet = VertexSet.newHexBorderedSet(hexWidth, hexHeight, hexCount);
        vertexSet.delaunayTriangulate();
        //vertexSet.optimize(convergenceTolerance);

//        MiseEnBoite meb = new TopLeftDistanceMeb();
//        HashMap<Vertex, Coord> hm = new HashMap<>(meb.miseEnBoite(vertexSet));
//        for (Vertex v : vertexSet) {
//            Coord c = hm.get(v);
//            if (c != null) {
//                v.setId(c.toString());
//            } else {
//                v.setId("null");
//            }
//        }

        int size = 500;
        CanvasPopUp.create(vertexSet, size);
    }

    public static void main(String[] args) {
        launch();
    }
}