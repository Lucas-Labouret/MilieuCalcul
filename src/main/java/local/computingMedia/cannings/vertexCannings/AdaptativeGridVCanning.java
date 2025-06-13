package local.computingMedia.cannings.vertexCannings;

import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.media.Medium;
import local.computingMedia.sLoci.Vertex;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Implements a VCanning using a grid of horizontal lines and vertical columns to define the grid cells.
 * The grid is adaptative, meaning that the positions and amounts of lines and columns can be adjusted.
 * Additionally, we allow a slight (virtual) inaccuracy in the position of the vertices if it helps compacting the canning.
 */
public class AdaptativeGridVCanning implements VertexCanning {
    private final Medium medium;
    private final VertexCanning base;
    private int width, height;
    private HashMap<Vertex, VertexCoord> vertexCanning;

    private static final double MAX_EPSILON = 1e-4; // Maximum allowed virtual epsilon for vertex positions

    private ArrayList<Double> lines;
    private ArrayList<Double> columns;
    private HashMap<Vertex, double[]> virtualEpsilons;

    @Override public Medium getMedium() { return medium; }
    @Override public int getWidth() { return width; }
    @Override public int getHeight() { return height; }

    @Override public HashMap<Vertex, VertexCoord> getVertexCanning() { return vertexCanning; }

    AdaptativeGridVCanning(VertexCanning base) {
        this.medium = base.getMedium();
        this.base = base;
    }

    @Override
    public void can(){
        base.can();

        width = base.getWidth();
        height = base.getHeight();

        vertexCanning = new HashMap<>(base.getVertexCanning());

        // Initialize the virtual changes of position for each vertex to the zero vector, indicating no changes.
        virtualEpsilons = new HashMap<>();
        for (Vertex v : medium) virtualEpsilons.put(v, new double[]{0, 0});

        // Remove empty lines and columns from the canning
        reduce();

        // Computes the positions of the separation lines and columns of the grid.
        computeSeparations();

        // Tries to reduce the maximum delta to 2
        collapseDeltas();
    }

    /** Removes all empty lines and columns from the canning, adjusting the vertices canning coordinates accordingly. */
    private void reduce(){
        HashMap<VertexCoord, Vertex> coordToVertex = new HashMap<>();
        for (Vertex v: medium) coordToVertex.put(vertexCanning.get(v), v);

        // Find the y indexes of empty lines
        // emptyLines is sorted by construction
        ArrayList<Integer> emptyLines = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            boolean isEmpty = true;
            for (int x = 0; x < width; x++) {
                Vertex v = coordToVertex.get(new VertexCoord(y, x));
                if (v != null) {
                    isEmpty = false;
                    break;
                }
            }
            if (isEmpty) emptyLines.add(y);
        }

        // Find the x indexes of empty columns
        // emptyColumns is sorted by construction
        ArrayList<Integer> emptyColumns = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            boolean isEmpty = true;
            for (int y = 0; y < height; y++) {
                Vertex v = coordToVertex.get(new VertexCoord(y, x));
                if (v != null) {
                    isEmpty = false;
                    break;
                }
            }
            if (isEmpty) emptyColumns.add(x);
        }

        if (emptyLines.isEmpty() && emptyColumns.isEmpty()) return; // Nothing to reduce

        // Create a new canning with the reduced coordinates
        HashMap<Vertex, VertexCoord> reducedCanning = new HashMap<>();

        int currentLineBound = 0;
        for (int y = 0; y < height; y++) {
            if (emptyLines.get(currentLineBound) == y) currentLineBound++;

            int currentColumnBound = 0;
            for (int x = 0; x < width; x++) {
                if (emptyColumns.get(currentColumnBound) == x) currentColumnBound++;

                Vertex v = coordToVertex.get(new VertexCoord(y, x));
                if (v == null) continue;
                reducedCanning.put(v, new VertexCoord(
                        y - currentLineBound, // We move the vertex up by the number of empty lines above it
                        x - currentColumnBound)  // We move the vertex left by the number of empty columns to its left
                );
            }
        }

        vertexCanning = reducedCanning;
        width -= emptyColumns.size();
        height -= emptyLines.size();
    }

    /** Computes the positions of the separation lines and columns of the grid based on the vertices' positions. */
    private void computeSeparations(){
        HashMap<VertexCoord, Vertex> coordToVertex = new HashMap<>();
        for (Vertex v: medium) coordToVertex.put(vertexCanning.get(v), v);

        double[][] minMaxLines = new double[height][2];
        for (int y = 0; y < height; y++) {
            minMaxLines[y][0] = Double.MAX_VALUE; // min
            minMaxLines[y][1] = Double.MIN_VALUE; // max

            for (int x = 0; x < width; x++) {
                Vertex v = coordToVertex.get(new VertexCoord(y, x));
                if (v == null) continue;
                if (v.getY() < minMaxLines[y][0]) minMaxLines[y][0] = v.getY();
                if (v.getY() > minMaxLines[y][1]) minMaxLines[y][1] = v.getY();
            }
        }

        double[][] minMaxColumns = new double[width][2];
        for (int x = 0; x < width; x++) {
            minMaxColumns[x][0] = Double.MAX_VALUE; // min
            minMaxColumns[x][1] = Double.MIN_VALUE; // max

            for (int y = 0; y < height; y++) {
                Vertex v = coordToVertex.get(new VertexCoord(y, x));
                if (v == null) continue;
                if (v.getX() < minMaxColumns[x][0]) minMaxColumns[x][0] = v.getX();
                if (v.getX() > minMaxColumns[x][1]) minMaxColumns[x][1] = v.getX();
            }
        }

        // Separation lines
        lines = new ArrayList<>(height+1);
        lines.set(0, 0d);
        for (int y = 1; y < height; y++) {
            if (minMaxLines[y-1][1] > minMaxLines[y][0]) throw new IllegalStateException("Found overlapping lines at " + (y-1) + " and " + y);
            lines.set(y, (minMaxLines[y-1][1] + minMaxLines[y][0]) / 2); // Midpoint between the max of the previous line and the min of the current line
        }
        lines.set(height, medium.getHeight());

        // Separation columns
        columns = new ArrayList<>(width+1);
        columns.set(0, 0d);
        for (int x = 1; x < width; x++) {
            if (minMaxColumns[x-1][1] > minMaxColumns[x][0]) throw new IllegalStateException("Found overlapping columns at " + (x-1) + " and " + x);
            columns.set(x, (minMaxColumns[x-1][1] + minMaxColumns[x][0]) / 2); // Midpoint between the max of the previous column and the min of the current column
        }
        columns.set(width, medium.getWidth());
    }

    /** Merges lines and columns in order to bring closer vertices that are too far apart canning-wise (deltaX or deltaY >= 3). */
    private void collapseDeltas(){

    }
}
