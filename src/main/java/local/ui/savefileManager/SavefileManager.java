package local.ui.savefileManager;

import local.computingMedium.Vertex;
import local.computingMedium.media.Medium;
import local.misc.LinkedList;
import local.misc.Node;
import local.ui.mediumScene.MediumScene;
import local.ui.view.InformationBar;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class SavefileManager {
    public static String DEFAULT_LOCATION  = "save/";
    public static String DEFAULT_EXTENSION = ".vtxs";

    private final MediumScene mediumScene;
    private final InformationBar info;

    public SavefileManager(MediumScene mediumScene, InformationBar info) {
        this.mediumScene = mediumScene;
        this.info = info;
    }

    protected abstract Medium makeVertexSet();

    public void save() {
        if (mediumScene == null) return;

        String tmpName = mediumScene.getFileName();
        if (tmpName == null || tmpName.isEmpty()) {
            info.setText("Please enter a file name.");
            return;
        }
        String name = DEFAULT_LOCATION + tmpName + DEFAULT_EXTENSION;

        Medium medium = mediumScene.getVertexSet();
        if (medium == null){
            info.setText("Noting to save.");
            return;
        }

        HashMap<Integer, Vertex> indexToVertex = new HashMap<>();
        HashMap<Vertex, Integer> vertexToIndex = new HashMap<>();

        int counter = 0;
        for (Vertex v : medium) {
            indexToVertex.put(counter, v);
            vertexToIndex.put(v, counter);
            counter++;
        }

        StringBuilder saveStr = new StringBuilder("-- Dimensions --\n");

        String width = Double.toString(medium.getWidth());
        String height = Double.toString(medium.getHeight());
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
            for (Vertex neighbor: indexToVertex.get(index).getNeighbors()) {
                int neighborIndex = vertexToIndex.get(neighbor);
                saveStr.append(neighborIndex).append(" ");
            }
            saveStr.append("\n");
        }

        saveStr.append("\n-- Hard Border --\n");
        if ( medium.getHardBorder() == null ) saveStr.append("null\n");
        else {
            for (Vertex vertex: medium.getHardBorder()) {
                int index = vertexToIndex.get(vertex);
                saveStr.append(index).append(" ");
            }
            saveStr.append("\n");
        }

        saveStr.append("\n-- Soft Border --\n");
        if ( medium.getSoftBorder() == null ) saveStr.append("null\n");
        else {
            for (Node<Vertex> current = medium.getSoftBorder().head; current != null; current = current.next) {
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
        if (mediumScene == null) return;

        String tmpName = mediumScene.getFileName();
        if (tmpName == null || tmpName.isEmpty()) {
            info.setText("Please enter a file name.");
            return;
        }
        String name = DEFAULT_LOCATION + tmpName + DEFAULT_EXTENSION;

        Medium medium = makeVertexSet();
        HashMap<Integer, Vertex> indexToVertex = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(name));
            int lineCounter = 0;
            String line = reader.readLine();
            lineCounter++;
            if(line == null) {
                throw new IOException("Empty file");
            }
            line = line.trim();
            if (!line.equals("-- Dimensions --")) {
                throw new IOException("Expected '-- Dimensions --' but got '" + line + "' on line " + lineCounter);
            }

            line = reader.readLine();
            lineCounter++;
            if (line == null) {
                throw new IOException("Unexpected end of file");
            }
            line = line.trim();

            String[] dimensions = line.split(" ");
            if (dimensions.length != 2) {
                throw new IOException("Expected dimensions but got '" + line + "' on line " + lineCounter);
            }
            try { 
                medium.setWidth(Double.parseDouble(dimensions[0]));
                medium.setHeight(Double.parseDouble(dimensions[1]));
            }
            catch (NumberFormatException e) {
                throw new IOException("Expected dimensions but got '" + line + "' on line " + lineCounter);
            }

            line = reader.readLine();
            lineCounter++;
            if (line == null){
                throw new IOException("Unexpected end of file");
            }
            line = line.trim();
            if (!line.isEmpty()) {
                throw new IOException("Expected an empty line but got '" + line + "' on line " + lineCounter);
            }
            
            line = reader.readLine();
            lineCounter++;
            if (line == null){
                throw new IOException("Unexpected end of file");
            }
            line = line.trim();
            if (!line.equals("-- Vertices --")) {
                throw new IOException("Expected '-- Vertices --' but got '" + line + "' on line " + lineCounter);
            }

            try {
                while (!(line = reader.readLine()).isEmpty()) {
                    lineCounter++;
                    line = line.trim();

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
                        medium.add(vertex);
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
            line = line.trim();
            if (!line.equals("-- Neighbors --")) {
                throw  new IOException("Expected '-- Neighbors --' but got '" + line + "' on line " + lineCounter);
            }
            try {
                while (!(line = reader.readLine()).isEmpty()) {
                    lineCounter++;
                    line = line.trim();

                    String[] neighborLine = line.split(" ");
                    try {
                        int index = Integer.parseInt(neighborLine[0].substring(0, neighborLine[0].length() - 1));
                        for (int i = 1; i < neighborLine.length; i++) {
                            int neighborIndex = Integer.parseInt(neighborLine[i]);
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

            line = reader.readLine();
            lineCounter++;

            if (line == null){
                throw new IOException("Unexpected end of file");
            }
            line = line.trim();
            if (!line.equals("-- Hard Border --")) {
                throw new IOException("Expected '-- Hard Border --' but got '" + line + "' on line " + lineCounter);
            }

            line = reader.readLine();
            lineCounter++;

            if (line == null){
                throw new IOException("Unexpected end of file");
            }
            line = line.trim();
            if (line.isEmpty()) {
                throw new IOException(
                        "Expected line of the form '<index 1> <index 2> ...' " +
                        "but got '" + line + "' on line " + lineCounter
                );
            }

            if (line.equals("null")) medium.setHardBorder(null);
            else try {
                String[] borderLine = line.split(" ");
                ArrayList<Vertex> hardBorder = new ArrayList<>(borderLine.length);
                for (String stringIndex: borderLine) {
                    int index = Integer.parseInt(stringIndex);
                    hardBorder.add(indexToVertex.get(index));
                }
                medium.setHardBorder(hardBorder);
            } catch (NumberFormatException e) {
                throw new IOException(
                        "Expected line of the form '<index 1> <index 2> ...' " +
                        "but got '" + line + "' on line " + lineCounter
                );
            }

            line = reader.readLine();
            lineCounter++;

            if (line == null){
                throw new IOException("Unexpected end of file");
            }
            if (!line.isEmpty()) {
                throw new IOException("Expected an empty line but got '" + line + "' on line " + lineCounter);
            }

            line = reader.readLine();
            lineCounter++;
            if (line == null){
                throw new IOException("Unexpected end of file");
            }
            line = line.trim();
            if (!line.equals("-- Soft Border --")) {
                throw new IOException("Expected '-- Soft Border --' but got '" + line + "' on line " + lineCounter);
            }

            line = reader.readLine();
            lineCounter++;
            if (line == null){
                throw new IOException("Unexpected end of file");
            }
            line = line.trim();
            if (line.isEmpty()) {
                throw new IOException(
                        "Expected line of the form '<index 1> <index 2> ...' " +
                        "but got '" + line + "' on line " + lineCounter
                );
            }

            if (line.equals("null")) medium.setSoftBorder(null);
            else try {
                String[] borderLine = line.split(" ");
                LinkedList<Vertex> softBorder = new LinkedList<>();
                Node<Vertex> current = null;
                for (String stringIndex: borderLine) {
                    int index = Integer.parseInt(stringIndex);
                    Node<Vertex> node = new Node<>(indexToVertex.get(index));
                    if (current == null) {
                        current = node;
                        softBorder.head = current;
                    }
                    else {
                        current.next = node;
                        current = current.next;
                    }
                }
                medium.setSoftBorder(softBorder);
            } catch (NumberFormatException e) {
                throw new IOException(
                        "Expected line of the form '<index 1> <index 2> ...' " +
                        "but got '" + line + "' on line " + lineCounter
                );
            }

            reader.close();
        }
        catch (FileNotFoundException e) { info.setText("File not found."); }
        catch (IOException e) {
            info.setText("File exists but couldn't be read.");
            e.printStackTrace();
        }

        mediumScene.setVertexSet(medium);
    }

    private static Vertex getVertex(String[] vertexLine) {
        double x = Double.parseDouble(vertexLine[1]);
        double y = Double.parseDouble(vertexLine[2]);

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
