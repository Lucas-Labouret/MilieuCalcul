package local.furthestpointoptimization;

import javafx.application.Application;
import javafx.stage.Stage;
import local.furthestpointoptimization.controller.CanvasPopUp;
import local.furthestpointoptimization.model.vertexSets.VertexSet;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        int hexWidth = 5;
        int hexHeight = (int) Math.ceil(hexWidth / Math.sqrt(2));
        int hexCount = (int) ((hexWidth-1)*Math.ceil(hexHeight/2.) + (hexWidth-2)*Math.floor(hexHeight/2.));
        double convergenceTolerance = 0.7;

        VertexSet vertexSet = VertexSet.newHexBorderedSet(hexWidth, hexHeight, 0);
        vertexSet.optimize(convergenceTolerance);

        String fileName = "save/w20_p20.vtxs";
        //vertexSet.fromFile(fileName);
        // VertexSet vertexSet = VertexSet.fromFile(fileName);

//        MiseEnBoiteUI miseEnBoite = new TopDistanceXSortedLinesMeb();
//        HashMap<Vertex, Coord> miseEnBoiteResult = miseEnBoite.miseEnBoite(vertexSet2);
//        for (Vertex vertex : miseEnBoiteResult.keySet()) {
//            vertex.setId(miseEnBoiteResult.get(vertex).toString());
//        }
//
//        CalculEfficiency calculEfficiency = new CalculEfficiency(miseEnBoite, vertexSet2);
//        System.out.println("Efficiency: " + calculEfficiency.T());

        int size = 1000;
        CanvasPopUp.create(vertexSet, size);
    }

    public static void main(String[] args) {
        launch();
    }
}