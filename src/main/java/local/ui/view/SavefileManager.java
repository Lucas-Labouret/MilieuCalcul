package local.ui.view;

import local.computingMedium.Vertex;
import local.computingMedium.vertexSets.VertexSet;
import local.misc.Node;
import local.ui.vertexSetScene.VertexSetScene;

import java.io.*;
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
        }
        String name = DEFAULT_LOCATION + tmpName + DEFAULT_EXTENSION;

        VertexSet vertexSet = vertexSetScene.getVertexSet();
        if (vertexSet == null){
            info.setText("Noting to save.");
            return;
        }

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
            Vertex v = indexToVertex.get(index);
            String x = Double.toString(v.getX());
            String y = Double.toString(v.getY());
            saveStr.append(index).append(": ")
                   .append(x).append(" ")
                   .append(y).append(" ");

            if (v.isBorder()) saveStr.append("True ");
            else saveStr.append("False ");
            
            if (v.isTopBorder()) saveStr.append("True ");
            else saveStr.append("False ");

            if (v.isLeftBorder()) saveStr.append("True ");
            else saveStr.append("False ");

            if (v.isRightBorder()) saveStr.append("True ");
            else saveStr.append("False ");

            if (v.isBottomBorder()) saveStr.append("True ");
            else saveStr.append("False ");
            
            saveStr.append("\n");
        }

        saveStr.append("\n-- Neighbors --\n");
        for (int index = 0; index < counter; index++) {
            saveStr.append(index).append(": ");
            for (Vertex neighbor: vertexSet){
                int neighborIndex = vertexToIndex.get(neighbor);
                saveStr.append(neighborIndex).append(" ");
            }
            saveStr.append("\n");
        }

        saveStr.append("\n-- Hard Border --\n");
        if ( vertexSet.getHardBorder() == null ) saveStr.append("null\n");
        else {
            for (Vertex vertex: vertexSet.getHardBorder()) {
                int index = vertexToIndex.get(vertex);
                saveStr.append(index).append(" ");
            }
            saveStr.append("\n");
        }

        saveStr.append("\n-- Soft-Border --\n");
        if ( vertexSet.getSoftBorder() == null ) saveStr.append("null\n");
        else {
            for (Node<Vertex> current = vertexSet.getSoftBorder().head; current != null; current = current.next) {
                int index = vertexToIndex.get(current.value);
                saveStr.append(index).append(" ");
            }
            saveStr.append("\n");
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(name));
            writer.write(saveStr.toString());
            writer.close();
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public void load() {
        if (vertexSetScene == null) return;

        String name = vertexSetScene.getFileName();
        if (name == null || name.isEmpty()) {
            info.setText("Please enter a file name.");
            return;
        }

        VertexSet vertexSet = new VertexSet();
        HashMap<Integer, Vertex> indexToVertex = new HashMap<>();
        HashMap<Vertex, Integer> vertexToIndex = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(name));
            int lineCounter = 0;
            String line = reader.readLine();
            lineCounter++;
            if (!line.equals("-- Dimensions --")) {
                throw new IOException("Expected '-- Dimensions --' but got '" + line + "' on line " + lineCounter);
            }
            if(line == null) {
                throw new IOException("Empty file");
            }

            line = reader.readLine();
            lineCounter++;
            if (line == null) {
                throw new IOException("Unexpected end of file");
            }
            String[] dimensions = line.split(" ");
            if (dimensions.length != 2) {
                throw new IOException("Expected dimensions but got '" + line + "' on line " + lineCounter);
            }
            try { 
                vertexSet.setWidth(Double.parseDouble(dimensions[0]));
                vertexSet.setHeight(Double.parseDouble(dimensions[1]));
            }
            catch (NumberFormatException e) {
                throw new IOException("Expected dimensions but got '" + line + "' on line " + lineCounter);
            }

            reader.readLine();
            lineCounter++;
            if (line == null){
                throw new IOException("Unexpected end of file");
            }
            if (!line.isEmpty()) {
                throw new IOException("Expected an empty line but got '" + line + "' on line " + lineCounter);
            }
            
            line = reader.readLine();
            lineCounter++;
            if (!line.equals("-- Vertices --")) {
                throw new IOException("Expected '-- Vertices --' but got '" + line + "' on line " + lineCounter);
            }
            try {
                while (!(line = reader.readLine()).isEmpty()) {
                    lineCounter++;

                    String[] vertexLine = line.split(" ");
                    if (vertexLine.length != 8) {
                        throw new IOException(
                                "Expected a line of the form '<index>: <x> <y> <border> <top> <left> <right> <bottom>' " +
                                "but got '" + line + "' on line " + lineCounter
                        );
                    }
                    try {
                        int index = Integer.parseInt(vertexLine[0].substring(0, vertexLine[0].length() - 1));
                        Vertex vertex = getVertex(vertexLine);
                        indexToVertex.put(index, vertex);
                        vertexToIndex.put(vertex, index);
                        vertexSet.add(vertex);
                    } catch (NumberFormatException e) {
                        throw new IOException(
                                "Expected a line of the form '<index>: <x> <y> <border> <top> <left> <right> <bottom>' " +
                                "but got '" + line + "' on line " + lineCounter
                        );
                    }
                }
            }
            catch (NullPointerException e) {
                throw new IOException("Unexpected end of file");
            }

            line = reader.readLine();
            lineCounter++;
            if (line == null){
                throw new IOException("Unexpected end of file");
            }
            if (!line.equals("-- Neighbors --")) {
                throw  new IOException("Expected '-- Neighbors --' but got '" + line + "' on line " + lineCounter);
            }
            try {
                while (!(line = reader.readLine()).isEmpty()) {
                    lineCounter++;

                    String[] neighborLine = line.split(" ");
                    try {
                        int index = Integer.parseInt(neighborLine[0].substring(0, neighborLine[0].length() - 1));
                        for (int i = 1; i < neighborLine.length; i++) {
                            int neighborIndex = Integer.parseInt(neighborLine[i].substring(0, neighborLine[i].length() - 1));
                            indexToVertex.get(index).addNeighbor(indexToVertex.get(neighborIndex));
                        }
                    } catch (NumberFormatException e) {
                        throw new IOException(
                                "Expected line of the form '<index>: <neighbor index 1> <neighbor index 2> ...' " +
                                "but got '" + line + "' on line " + lineCounter
                        );
                    }
                }
            }
            catch (NullPointerException e) {
                throw new IOException("Unexpected end of file");
            }
        }
        catch (FileNotFoundException e) { info.setText("File not found."); }
        catch (IOException e) {
            info.setText("File exists but couldn't be read.");
            e.printStackTrace();
        }

        vertexSetScene.setVertexSet(vertexSet);
    }

    private static Vertex getVertex(String[] vertexLine) {
        int x = Integer.parseInt(vertexLine[1]);
        int y = Integer.parseInt(vertexLine[2]);

        boolean border = Boolean.parseBoolean(vertexLine[3]);
        boolean topBorder = Boolean.parseBoolean(vertexLine[4]);
        boolean leftBorder = Boolean.parseBoolean(vertexLine[5]);
        boolean rightBorder = Boolean.parseBoolean(vertexLine[6]);
        boolean bottomBorder = Boolean.parseBoolean(vertexLine[7]);

        return new Vertex(
                x, y,
                border,
                topBorder, leftBorder, rightBorder, bottomBorder
        );
    }
}
