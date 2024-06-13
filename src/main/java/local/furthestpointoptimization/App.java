package local.furthestpointoptimization;

import javafx.application.Application;
import javafx.stage.Stage;
import local.furthestpointoptimization.controller.CanvasPopUp;
import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;
import local.furthestpointoptimization.model.miseEnBoite.Coord;
import local.furthestpointoptimization.model.miseEnBoite.MiseEnBoite;
import local.furthestpointoptimization.model.miseEnBoite.TopLeftDistanceMeb;

import java.io.*;
import java.util.HashMap;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        int hexWidth = 20;
        int hexHeight = (int) Math.ceil(hexWidth / Math.sqrt(2));
        int hexCount = (int) ((hexWidth-1)*Math.ceil(hexHeight/2.) + (hexWidth-2)*Math.floor(hexHeight/2.));
        double convergenceTolerance = 0.95;

        VertexSet vertexSet = VertexSet.newHexBorderedSet(hexWidth, hexHeight, hexCount+20);
        vertexSet.optimize(convergenceTolerance);

        String fileName = "save/w20_p10.vtxs";
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(vertexSet);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        VertexSet vertexSet2 = null;
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            vertexSet2 = (VertexSet) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        int size = 500;
        CanvasPopUp.create(vertexSet2, size);
    }

    public static void main(String[] args) {
        launch();
    }
}