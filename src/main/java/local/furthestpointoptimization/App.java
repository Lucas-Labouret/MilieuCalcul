package local.furthestpointoptimization;

import javafx.application.Application;
import javafx.stage.Stage;
import local.furthestpointoptimization.controller.CanvasPopUp;
import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;
import local.furthestpointoptimization.model.miseEnBoite.*;

import java.io.*;
import java.util.HashMap;

public class App extends Application {
    @Override
    public void start(Stage stage) {
//        int hexWidth = 5;
//        int hexHeight = (int) Math.ceil(hexWidth / Math.sqrt(2));
//        int hexCount = (int) ((hexWidth-1)*Math.ceil(hexHeight/2.) + (hexWidth-2)*Math.floor(hexHeight/2.));
//        double convergenceTolerance = 0.7;
//
//        VertexSet vertexSet = VertexSet.newHexBorderedSet(hexWidth, hexHeight, hexCount);
//        vertexSet.optimize(convergenceTolerance);

        String fileName = "save/w20_p20.vtxs";
//        try {
//            FileOutputStream fos = new FileOutputStream(fileName);
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            oos.writeObject(vertexSet);
//            oos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }

        VertexSet vertexSet2 = null;
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            vertexSet2 = (VertexSet) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        vertexSet2.delaunayTriangulate();
        vertexSet2.bandageBorderFix();

//        MiseEnBoite miseEnBoite = new TopDistanceXSortedLinesMeb();
//        HashMap<Vertex, Coord> miseEnBoiteResult = miseEnBoite.miseEnBoite(vertexSet2);
//        for (Vertex vertex : miseEnBoiteResult.keySet()) {
//            vertex.setId(miseEnBoiteResult.get(vertex).toString());
//        }
//
//        CalculEfficiency calculEfficiency = new CalculEfficiency(miseEnBoite, vertexSet2);
//        System.out.println("Efficiency: " + calculEfficiency.T());

        int size = 1000;
        CanvasPopUp.create(vertexSet2, size);
    }

    public static void main(String[] args) {
        launch();
    }
}