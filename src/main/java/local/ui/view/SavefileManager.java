package local.ui.view;

import local.computingMedium.Vertex;
import local.computingMedium.vertexSets.VertexSet;
import local.ui.vertexSetScene.VertexSetScene;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class SavefileManager {
    public static String DEFAULT_LOCATION  = "save/";
    public static String DEFAULT_EXTENSION = ".vtxs";

    private final VertexSetScene vertexSetScene;
    private final InformationBar info;

    public SavefileManager(VertexSetScene vertexSetScene, InformationBar info) {
        this.vertexSetScene = vertexSetScene;
        this.info = info;
    }

    public void save() {
        if (vertexSetScene == null) return;

        String tmpName = vertexSetScene.getFileName();
        if (tmpName == null || tmpName.isEmpty()) {
            info.setText("Please enter a file name.");
            return;
        };
        String name = DEFAULT_LOCATION + tmpName + DEFAULT_EXTENSION;

        VertexSet vertexSet = vertexSetScene.getVertexSet();
        if (vertexSet == null){
            info.setText("Noting to save.");
            return;
        };

        HashMap<Integer, Vertex> indexToVertex = new HashMap<>();
        HashMap<Vertex, Integer> vertexToIndex = new HashMap<>();

        int counter = 0;
        for (Vertex v : vertexSet) {
            indexToVertex.put(counter, v);
            vertexToIndex.put(v, counter);
            counter++;
        }

        StringBuilder saveStr = new StringBuilder("-- Dimensions --\n");

        String width = Double.toString(vertexSet.getWidth());
        String height = Double.toString(vertexSet.getHeight());
        saveStr.append(width) .append(" ")
               .append(height).append("\n");

        saveStr.append("\n-- Vertices --\n");
        for (int index = 0; index < counter; index++) {
            String x = Double.toString(indexToVertex.get(index).getX());
            String y = Double.toString(indexToVertex.get(index).getY());
            saveStr.append(index).append(": ")
                   .append(x).append(" ")
                   .append(y).append("\n");
        }

        saveStr.append("\n-- Neighbors --\n");
        for (int index = 0; index < counter; index++) {
            saveStr.append(index).append(": ");
            for (Vertex neighbor: vertexSet){
                saveStr.append(vertexToIndex.get(neighbor)).append(" ");
            }
        }


        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(name));
            writer.write(saveStr.toString());
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public void load() {
        if (vertexSetScene == null) return;

        String name = vertexSetScene.getFileName();
        if (name == null || name.isEmpty()) {
            info.setText("Please enter a file name.");
            return;
        };
    }
}
