package local.savefileManagers;

import local.computingMedia.sLoci.Vertex;
import local.computingMedia.media.Medium;
import local.misc.linkedList.LinkedList;
import local.misc.linkedList.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages the saving and loading of media savefiles.
 * This class saves and loads from MilieuCalcul/save/
 * It automatically appends the default extension ".vtxs" to the savefile name, therefore it should not be specified in the file's name.
 */
public abstract class SavefileManager {
    public static final String DEFAULT_LOCATION  = "save/";
    public static final String DEFAULT_EXTENSION = ".vtxs";

    public SavefileManager() {}

    /**
     * Creates a new instance of the Medium class.
     * This method should be overridden by subclasses to return the specific type of Medium they handle.
     *
     * @return A new instance of the Medium class.
     */
    protected abstract Medium makeMedium();

    /**
     * Saves the given Medium to a file with the specified name.
     * The file will be saved in the default location with the default extension.
     *
     * @param medium The Medium to save.
     * @param name   The name of the savefile (without extension).
     * @throws IOException If an error occurs during saving.
     */
    public void save(Medium medium, String name) throws IOException {
        if (medium == null) return;
        String fullName = DEFAULT_LOCATION + name + DEFAULT_EXTENSION;

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

        BufferedWriter writer = new BufferedWriter(new FileWriter(fullName));
        writer.write(saveStr.toString());
        writer.close();
    }

    /**
     * Loads a Medium from a file with the specified name.
     * The file should be in the default location with the default extension.
     *
     * @param name The name of the savefile (without extension).
     * @return The loaded Medium.
     * @throws IOException If an error occurs during loading.
     */
    public Medium load(String name) throws IOException {
        String fullName = DEFAULT_LOCATION + name + DEFAULT_EXTENSION;

        Medium medium = makeMedium();
        HashMap<Integer, Vertex> indexToVertex = new HashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(fullName));
        int lineCounter = 0;
        String line = reader.readLine();
        lineCounter++;
        if(line == null) {
            reader.close();
            throw new IOException("Empty file");
        }
        line = line.trim();
        if (!line.equals("-- Dimensions --")) {
            reader.close();
            throw new IOException("Expected '-- Dimensions --' but got '" + line + "' on line " + lineCounter);
        }

        line = reader.readLine();
        lineCounter++;
        if (line == null) {
            reader.close();
            throw new IOException("Unexpected end of file");
        }
        line = line.trim();

        String[] dimensions = line.split(" ");
        if (dimensions.length != 2) {
            reader.close();
            throw new IOException("Expected dimensions but got '" + line + "' on line " + lineCounter);
        }
        try {
            medium.setWidth(Double.parseDouble(dimensions[0]));
            medium.setHeight(Double.parseDouble(dimensions[1]));
        }
        catch (NumberFormatException e) {
            reader.close();
            throw new IOException("Expected dimensions but got '" + line + "' on line " + lineCounter);
        }

        line = reader.readLine();
        lineCounter++;
        if (line == null){
            reader.close();
            throw new IOException("Unexpected end of file");
        }
        line = line.trim();
        if (!line.isEmpty()) {
            reader.close();
            throw new IOException("Expected an empty line but got '" + line + "' on line " + lineCounter);
        }

        line = reader.readLine();
        lineCounter++;
        if (line == null){
            reader.close();
            throw new IOException("Unexpected end of file");
        }
        line = line.trim();
        if (!line.equals("-- Vertices --")) {
            reader.close();
            throw new IOException("Expected '-- Vertices --' but got '" + line + "' on line " + lineCounter);
        }

        try {
            while (!(line = reader.readLine()).isEmpty()) {
                lineCounter++;
                line = line.trim();

                String[] vertexLine = line.split(" ");
                if (vertexLine.length != 8) {
                    reader.close();
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
                    reader.close();
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
            reader.close();
            throw new IOException("Unexpected end of file");
        }
        line = line.trim();
        if (!line.equals("-- Neighbors --")) {
            reader.close();
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
                    reader.close();
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
            reader.close();
            throw new IOException("Unexpected end of file");
        }
        line = line.trim();
        if (!line.equals("-- Hard Border --")) {
            reader.close();
            throw new IOException("Expected '-- Hard Border --' but got '" + line + "' on line " + lineCounter);
        }

        line = reader.readLine();
        lineCounter++;

        if (line == null){
            reader.close();
            throw new IOException("Unexpected end of file");
        }
        line = line.trim();
        if (line.isEmpty()) {
            reader.close();
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
            reader.close();
            throw new IOException(
                    "Expected line of the form '<index 1> <index 2> ...' " +
                    "but got '" + line + "' on line " + lineCounter
            );
        }

        line = reader.readLine();
        lineCounter++;

        if (line == null){
            reader.close();
            throw new IOException("Unexpected end of file");
        }
        if (!line.isEmpty()) {
            reader.close();
            throw new IOException("Expected an empty line but got '" + line + "' on line " + lineCounter);
        }

        line = reader.readLine();
        lineCounter++;
        if (line == null){
            reader.close();
            throw new IOException("Unexpected end of file");
        }
        line = line.trim();
        if (!line.equals("-- Soft Border --")) {
            reader.close();
            throw new IOException("Expected '-- Soft Border --' but got '" + line + "' on line " + lineCounter);
        }

        line = reader.readLine();
        lineCounter++;
        if (line == null){
            reader.close();
            throw new IOException("Unexpected end of file");
        }
        line = line.trim();
        if (line.isEmpty()) {
            reader.close();
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
            reader.close();
            throw new IOException(
                    "Expected line of the form '<index 1> <index 2> ...' " +
                    "but got '" + line + "' on line " + lineCounter
            );
        }

        reader.close();
        return medium;
    }

    /**
     * Creates a Vertex from a line of the savefile.
     * The line should be in the format: "<index>: <x> <y> <border> <top> <left> <right> <bottom>".
     *
     * @param vertexLine The line containing vertex information.
     * @return A Vertex object created from the line.
     */
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
