package savefileManagers;

import cannings.Canning;
import cannings.VertexCanningCompleter;
import cannings.coords.sCoords.*;
import cannings.coords.tCoords.*;
import cannings.vertexCannings.RoundedCoordIncrementalVCanning;
import cannings.vertexCannings.SimpleVertexCanning;
import computingMedia.sLoci.*;
import computingMedia.media.Medium;
import computingMedia.tLoci.*;
import misc.linkedList.LinkedList;
import misc.linkedList.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages the saving and loading of media savefiles.
 * This class saves and loads from MilieuCalcul/save/
 * It automatically appends the default extension ".vtxs" to the savefile name, therefore it should not be specified in the file's name.
 */
public abstract class SavefileManager {
    public static final String DEFAULT_SAVE_LOCATION = "save/";
    public static final String DEFAULT_EXPORT_LOCATION = "export/";
    public static final String DEFAULT_SAVE_EXTENSION = ".vtxs";
    public static final String DEFAULT_EXPORT_EXTENSION = ".can";


    public SavefileManager() {}

    /**
     * Creates a new instance of the Medium class.
     * This method should be overridden by subclasses to return the specific type of Medium they handle.
     *
     * @return A new instance of the Medium class.
     */
    protected abstract Medium makeMedium();

    /**
     * Saves the given Canning to a file with the specified name.
     * The file will be saved in the default location with the default extension.
     * Note that only the vertex canning is saved, as the rest of the canning can be easily reconstructed with the completer.
     *
     * @param canning The Canning to save.
     * @param name The name of the savefile (without extension).
     * @throws IOException If an error occurs during saving.
     */
    public void save(Canning canning, String name) throws IOException {
        if (canning == null) return;

        Medium medium = canning.getMedium();
        HashMap<Vertex, VertexCoord> vertexCanning = canning.getVertexCanning();

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

        saveStr.append("\n-- Canning --\n");
        for (Vertex v : medium) {
            int index = vertexToIndex.get(v);
            VertexCoord coord = vertexCanning.get(v);
            saveStr.append(index).append(":").append(" ")
                   .append(coord.Y()).append(" ")
                   .append(coord.X());
            saveStr.append("\n");
        }

        String fullName = DEFAULT_SAVE_LOCATION + name + DEFAULT_SAVE_EXTENSION;
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
    public Canning load(String name) throws IOException {
        String fullName = DEFAULT_SAVE_LOCATION + name + DEFAULT_SAVE_EXTENSION;

        Medium medium = makeMedium();
        Canning canning;

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
            reader.close();
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
            reader.close();
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
        if (!line.equals("-- Canning --")) {
            reader.close();
            throw new IOException("Expected '-- Canning --' but got '" + line + "' on line " + lineCounter);
        }
        try {
            line = reader.readLine();
            if (line.equals("null")) {
                canning = new VertexCanningCompleter(new RoundedCoordIncrementalVCanning(medium));
                canning.can();
            }
            else {
                HashMap<Vertex, VertexCoord> vertexCoords = new HashMap<>();
                int width = 0;
                int height = 0;
                do {
                    lineCounter++;
                    line = line.trim();

                    String[] coordLine = line.split(" ");
                    try {
                        int index = Integer.parseInt(coordLine[0].substring(0, coordLine[0].length() - 1));
                        Vertex vertex = indexToVertex.get(index);
                        int y = Integer.parseInt(coordLine[1]);
                        int x = Integer.parseInt(coordLine[2]);
                        vertexCoords.put(vertex, new VertexCoord(y, x));

                        if (y >= height) height = y + 1;
                        if (x >= width) width = x + 1;

                    } catch (NumberFormatException e) {
                        reader.close();
                        throw new IOException(
                                "Expected line of the form '<index>: <Y> <X>' " +
                                        "but got '" + line + "' on line " + lineCounter
                        );
                    }
                } while ((line = reader.readLine()) != null && !line.isEmpty());
                canning = new VertexCanningCompleter(new SimpleVertexCanning(medium, vertexCoords, width, height));
                canning.can();
            }
        }
        catch (NullPointerException e) {
            reader.close();
            throw new IOException("Unexpected end of file");
        }

        reader.close();
        return canning;
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

    /**
     * Export the full canning.
     * The file will be saved in the default location with the default extension.
     * The whole canning (along with physical coordinates) is exported for use by other applications.
     *
     * @param canning The Canning to save.
     * @param name The name of the export file (without extension).
     * @throws IOException If an error occurs during saving.
     */
    public void export(Canning canning, String name) throws IOException {
        HashMap<Vertex, VertexCoord> vertexCanning = canning.getVertexCanning();
        HashMap<Ve, VeCoord> veCanning = canning.getVeCanning();
        HashMap<Vf, VfCoord> vfCanning = canning.getVfCanning();

        HashMap<Edge, EdgeCoord> edgeCanning = canning.getEdgeCanning();
        HashMap<Ev, EvCoord> evCanning = canning.getEvCanning();
        HashMap<Ef, EfCoord> efCanning = canning.getEfCanning();

        HashMap<Face, FaceCoord> faceCanning = canning.getFaceCanning();
        HashMap<Fv, FvCoord> fvCanning = canning.getFvCanning();
        HashMap<Fe, FeCoord> feCanning = canning.getFeCanning();

        HashMap<Integer, Ve> indexToVe = new HashMap<>();
        HashMap<Ve, Integer> veToIndex = new HashMap<>();
        HashMap<Integer, Vf> indexToVf = new HashMap<>();
        HashMap<Vf, Integer> vfToIndex = new HashMap<>();

        HashMap<Ev, Integer> evToIndex = new HashMap<>();
        HashMap<Integer, Ef> indexToEf = new HashMap<>();
        HashMap<Ef, Integer> efToIndex = new HashMap<>();

        HashMap<Fv, Integer> fvToIndex = new HashMap<>();
        HashMap<Fe, Integer> feToIndex = new HashMap<>();

        StringBuilder exportStr = new StringBuilder();

        exportStr.append("-- Dimensions --\n");
        exportStr.append(canning.getMedium().getHeight()).append(" ").append(canning.getMedium().getWidth()).append("\n\n");

        int i;

        i=0;
        exportStr.append("-- Vertices --\n");
        for (Vertex vertex: vertexCanning.keySet()) {
            double y = vertex.getY();
            double x = vertex.getX();
            VertexCoord vertexCoord = vertexCanning.get(vertex);
            String border = "";
            border += vertex.isTopBorder()? "T" : "F";
            border += vertex.isLeftBorder()? "T" : "F";
            border += vertex.isRightBorder()? "T" : "F";
            border += vertex.isBottomBorder()? "T" : "F";
            exportStr.append(i).append(" ").append(y).append(" ").append(x).append(" ")
                     .append(vertexCoord.Y()).append(" ").append(vertexCoord.X()).append(" ")
                     .append(border)
                     .append("\n");
            i++;
        }
        exportStr.append("\n");

        i=0;
        exportStr.append("-- Ve --\n");
        for (Ve ve: veCanning.keySet()) {
            VeCoord veCoord = veCanning.get(ve);
            indexToVe.put(i, ve);
            veToIndex.put(ve, i);
            double y = (2/3d) * ve.v.getY() + (1/3d) * ve.e.getCenter().getY();
            double x = (2/3d) * ve.v.getX() + (1/3d) * ve.e.getCenter().getX();
            exportStr.append(i).append(" ").append(y).append(" ").append(x).append(" ")
                     .append(veCoord.vertex().Y()).append(" ").append(veCoord.vertex().X()).append(" ").append(veCoord.theta())
                     .append("\n");
            i++;
        }
        exportStr.append("\n");

        i=0;
        exportStr.append("-- Vf --\n");
        for (Vf vf: vfCanning.keySet()) {
            VfCoord vfCoord = vfCanning.get(vf);
            vfToIndex.put(vf, i);
            indexToVf.put(i, vf);
            double y = (2/3d) * vf.v.getY() + (1/3d) * vf.f.getCentroid().getY();
            double x = (2/3d) * vf.v.getX() + (1/3d) * vf.f.getCentroid().getX();
            exportStr.append(i).append(" ").append(y).append(" ").append(x).append(" ")
                     .append(vfCoord.vertex().Y()).append(" ").append(vfCoord.vertex().X()).append(" ").append(vfCoord.theta())
                     .append("\n");
            i++;
        }
        exportStr.append("\n");

        i=0;
        exportStr.append("-- Edges --\n");
        for (Edge edge: edgeCanning.keySet()) {
            double y = edge.getCenter().getY();
            double x = edge.getCenter().getX();
            EdgeCoord edgeCoord = edgeCanning.get(edge);
            String border = "";
            border += (edge.getEnds().stream().allMatch(Vertex::isTopBorder))? "T" : "F";
            border += (edge.getEnds().stream().allMatch(Vertex::isLeftBorder))? "T" : "F";
            border += (edge.getEnds().stream().allMatch(Vertex::isRightBorder))? "T" : "F";
            border += (edge.getEnds().stream().allMatch(Vertex::isBottomBorder))? "T" : "F";
            exportStr.append(i).append(" ").append(y).append(" ").append(x).append(" ")
                     .append(edgeCoord.vertex().Y()).append(" ").append(edgeCoord.vertex().X()).append(" ").append(edgeCoord.theta()).append(" ")
                     .append(border)
                     .append("\n");
            i++;
        }
        exportStr.append("\n");

        i=0;
        exportStr.append("-- Ev --\n");
        for (Ev ev: evCanning.keySet()) {
            EvCoord evCoord = evCanning.get(ev);
            evToIndex.put(ev, i);
            double y = (2/3d) * ev.e.getCenter().getY() + (1/3d) * ev.v.getY();
            double x = (2/3d) * ev.e.getCenter().getX() + (1/3d) * ev.v.getX();
            exportStr.append(i).append(" ").append(y).append(" ").append(x).append(" ")
                     .append(evCoord.edge().vertex().Y()).append(" ").append(evCoord.edge().vertex().X()).append(" ")
                     .append(evCoord.edge().theta()).append(" ").append(evCoord.side())
                     .append("\n");
            i++;
        }
        exportStr.append("\n");

        i=0;
        exportStr.append("-- Ef --\n");
        for (Ef ef: efCanning.keySet()) {
            EfCoord efCoord = efCanning.get(ef);
            efToIndex.put(ef, i);
            indexToEf.put(i, ef);
            double y = (2/3d) * ef.e.getCenter().getY() + (1/3d) * ef.f.getCentroid().getY();
            double x = (2/3d) * ef.e.getCenter().getX() + (1/3d) * ef.f.getCentroid().getX();
            exportStr.append(i).append(" ").append(y).append(" ").append(x).append(" ")
                     .append(efCoord.edge().vertex().Y()).append(" ").append(efCoord.edge().vertex().X()).append(" ")
                     .append(efCoord.edge().theta()).append(" ").append(efCoord.side())
                     .append("\n");
            i++;
        }
        exportStr.append("\n");

        i=0;
        exportStr.append("-- Faces --\n");
        for (Face face: faceCanning.keySet()) {
            FaceCoord faceCoord = faceCanning.get(face);
            exportStr.append(i).append(" ").append(face.getCentroid().getY()).append(" ").append(face.getCentroid().getX()).append(" ")
                     .append(faceCoord.vertex().Y()).append(" ").append(faceCoord.vertex().X()).append(" ").append(faceCoord.theta())
                     .append("\n");
            i++;
        }
        exportStr.append("\n");

        i=0;
        exportStr.append("-- Fv --\n");
        for (Fv fv: fvCanning.keySet()) {
            FvCoord fvCoord = fvCanning.get(fv);
            fvToIndex.put(fv, i);
            double y = (2/3d) * fv.f.getCentroid().getY() + (1/3d) * fv.v.getY();
            double x = (2/3d) * fv.f.getCentroid().getX() + (1/3d) * fv.v.getX();
            exportStr.append(i).append(" ").append(y).append(" ").append(x).append(" ")
                     .append(fvCoord.face().vertex().Y()).append(" ").append(fvCoord.face().vertex().X()).append(" ")
                     .append(fvCoord.face().theta()).append(" ").append(fvCoord.side())
                     .append("\n");
            i++;
        }
        exportStr.append("\n");
        i=0;
        exportStr.append("-- Fe --\n");
        for (Fe fe: feCanning.keySet()) {
            FeCoord feCoord = feCanning.get(fe);
            feToIndex.put(fe, i);
            double y = (2/3d) * fe.f.getCentroid().getY() + (1/3d) * fe.e.getCenter().getY();
            double x = (2/3d) * fe.f.getCentroid().getX() + (1/3d) * fe.e.getCenter().getX();
            exportStr.append(i).append(" ").append(y).append(" ").append(x).append(" ")
                     .append(feCoord.face().vertex().Y()).append(" ").append(feCoord.face().vertex().X()).append(" ")
                     .append(feCoord.face().theta()).append(" ").append(feCoord.side())
                     .append("\n");
            i++;
        }
        exportStr.append("\n");

        exportStr.append("-- Ve <-> Ev --\n");
        for (int j = 0; j < indexToVe.size(); j++) {
            Ve ve = indexToVe.get(j);
            Ev ev = ve.getDual();
            int evIndex = evToIndex.get(ev);
            exportStr.append(j).append(" ").append(evIndex).append("\n");
        }
        exportStr.append("\n");

        exportStr.append("-- Vf <-> Fv --\n");
        for (int j = 0; j < indexToVf.size(); j++) {
            Vf vf = indexToVf.get(j);
            Fv fv = vf.getDual();
            int fvIndex = fvToIndex.get(fv);
            exportStr.append(j).append(" ").append(fvIndex).append("\n");
        }
        exportStr.append("\n");

        exportStr.append("-- Ef <-> Fe --\n");
        for (int j = 0; j < indexToEf.size(); j++) {
            Ef ef = indexToEf.get(j);
            Fe fe = ef.getDual();
            int feIndex = feToIndex.get(fe);
            exportStr.append(j).append(" ").append(feIndex).append("\n");
        }
        exportStr.append("\n");

        String fullName = DEFAULT_EXPORT_LOCATION + name + DEFAULT_EXPORT_EXTENSION;
        BufferedWriter writer = new BufferedWriter(new FileWriter(fullName));
        writer.write(exportStr.toString());
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        HardRectangleManager rectangleManager = new HardRectangleManager();
        Canning large = rectangleManager.load("HardRectangle4096_ORCI2D_9");
        Canning medium = rectangleManager.load("HardSquare256_ORCI_0");
            Canning small = rectangleManager.load("HardSquare36_ORCI_0");

        rectangleManager.export(large, "large");
        rectangleManager.export(medium, "medium");
        rectangleManager.export(small, "small");

        System.out.println("Done");
    }
}
