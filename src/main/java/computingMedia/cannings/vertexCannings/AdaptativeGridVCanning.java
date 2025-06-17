package computingMedia.cannings.vertexCannings;

import computingMedia.cannings.coords.sCoords.VertexCoord;
import computingMedia.media.Medium;
import computingMedia.sLoci.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

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

    // Maximum allowed virtual epsilon for vertex positions
    // Should be strictly less than half the smallest dimension of any grid cell.
    private static final double MAX_EPSILON = 1e-4;

    // Y-coordinates of the horizontal separation lines
    private ArrayList<Double> lineSeparations;

    // X-coordinates of the vertical separation columns
    private ArrayList<Double> columnSeparations;

    // The virtual changes of position for each vertex, allowing for slight inaccuracies.
    // The values are stored as an array of two doubles: [epsilonY, epsilonX].
    private HashMap<Vertex, double[]> virtualEpsilons;

    @Override public Medium getMedium() { return medium; }
    @Override public int getWidth() { return width; }
    @Override public int getHeight() { return height; }

    @Override public HashMap<Vertex, VertexCoord> getVertexCanning() { return vertexCanning; }

    public AdaptativeGridVCanning(VertexCanning base) {
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

        int currentLineBound = 0; // Tracks how many empty lines we have passed
        for (int y = 0; y < height; y++) {
            if (currentLineBound < emptyLines.size() && emptyLines.get(currentLineBound) == y) currentLineBound++;

            int currentColumnBound = 0; // Tracks how many empty columns we have passed
            for (int x = 0; x < width; x++) {
                if (currentColumnBound < emptyColumns.size() && emptyColumns.get(currentColumnBound) == x) currentColumnBound++;

                Vertex v = coordToVertex.get(new VertexCoord(y, x));
                if (v == null) continue;
                reducedCanning.put(v, new VertexCoord(
                        y - currentLineBound, // We move the vertex up by the number of empty lines above it
                        x - currentColumnBound) // We move the vertex left by the number of empty columns to its left
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
        lineSeparations = new ArrayList<>(Collections.nCopies(height+1, 0d));
        for (int y = 1; y < height; y++) {
            if (minMaxLines[y-1][1] > minMaxLines[y][0]) throw new IllegalStateException("Found overlapping lines at " + (y-1) + " and " + y);
            lineSeparations.set(y, (minMaxLines[y-1][1] + minMaxLines[y][0]) / 2); // Midpoint between the max of the previous line and the min of the current line
        }
        lineSeparations.set(height, medium.getHeight());

        // Separation columns
        columnSeparations = new ArrayList<>(Collections.nCopies(width+1, 0d));
        for (int x = 1; x < width; x++) {
            if (minMaxColumns[x-1][1] > minMaxColumns[x][0]) throw new IllegalStateException("Found overlapping columns at " + (x-1) + " and " + x);
            columnSeparations.set(x, (minMaxColumns[x-1][1] + minMaxColumns[x][0]) / 2); // Midpoint between the max of the previous column and the min of the current column
        }
        columnSeparations.set(width, medium.getWidth());
    }

    /** Merges lines and columns in order to bring closer vertices that are too far apart canning-wise (deltaX or deltaY >= 3). */
    private void collapseDeltas(){
        double maxVerticalEpsilon = MAX_EPSILON * medium.getHeight() / height;
        double maxHorizontalEpsilon = MAX_EPSILON * medium.getWidth() / width;

        nonMergeCollapse(maxVerticalEpsilon, maxHorizontalEpsilon);
    }

    private void nonMergeCollapse(double maxVerticalEpsilon, double maxHorizontalEpsilon) {
        boolean changed = false;
        do for (Vertex v: medium) {
            VertexCoord vCoord = vertexCanning.get(v);
            for (Vertex n: v.getNeighbors()){
                VertexCoord nCoord = vertexCanning.get(n);

                //if v is 3 cells left of n
                //note : the cases where v is left of n and n is right of v are handled separately despite being symmetric,
                //as we move a different vertex in each case
                if (nCoord.X() - vCoord.X() ==  3){
                    double virtualX = v.getX() + virtualEpsilons.get(v)[1];
                    double dist = columnSeparations.get(vCoord.X()+1) - virtualX;
                    //if the distance between between v and the next separation column too large, continue
                    if (dist > maxHorizontalEpsilon) continue;
                    //if the virtual position of v would become too far from its real position, continue
                    if (virtualX + 2*dist > maxHorizontalEpsilon) continue;

                    VertexCoord newCoord = new VertexCoord(vCoord.Y(), vCoord.X() + 1);
                    boolean moreProblems = false;
                    for (Vertex n2: v.getNeighbors()){
                        VertexCoord n2Coord = vertexCanning.get(n2);
                        if (Math.abs(n2Coord.X() - vCoord.X()) <= 2 && Math.abs(n2Coord.X() - newCoord.X()) >= 3) {
                            moreProblems = true; // n2 is already at the new position
                            break;
                        }
                    }
                    // We cannot move v, as it would create new problems
                    if (moreProblems) continue;

                    virtualEpsilons.put(v, new double[]{virtualEpsilons.get(v)[0], virtualX + 2*dist});
                    vertexCanning.put(v, newCoord);
                    changed = true;
                    break;
                }

                //if v is 3 cells right of n
                if (vCoord.X() - nCoord.X() ==  3){
                    double virtualX = v.getX() + virtualEpsilons.get(v)[1];
                    double dist = virtualX - columnSeparations.get(vCoord.X());
                    //if the distance between between v and the next separation column too large, continue
                    if (dist > maxHorizontalEpsilon) continue;
                    //if the virtual position of v would become too far from its real position, continue
                    if (virtualX - 2*dist < -maxHorizontalEpsilon) continue;

                    VertexCoord newCoord = new VertexCoord(vCoord.Y(), vCoord.X() - 1);
                    boolean moreProblems = false;
                    for (Vertex n2: v.getNeighbors()){
                        VertexCoord n2Coord = vertexCanning.get(n2);
                        if (Math.abs(n2Coord.X() - vCoord.X()) <= 2 && Math.abs(n2Coord.X() - newCoord.X()) >= 3) {
                            moreProblems = true; // n2 is already at the new position
                            break;
                        }
                    }
                    // We cannot move v, as it would create new problems
                    if (moreProblems) continue;

                    virtualEpsilons.put(v, new double[]{virtualEpsilons.get(v)[0], virtualX - 2*dist});
                    vertexCanning.put(v, newCoord);
                    changed = true;
                    break;
                }

                //if v is 3 cells above n
                if (nCoord.Y() - vCoord.Y() ==  3){
                    double virtualY = v.getY() + virtualEpsilons.get(v)[0];
                    double dist = lineSeparations.get(vCoord.Y()+1) - virtualY;
                    //if the distance between between v and the next separation line too large, continue
                    if (dist > maxVerticalEpsilon) continue;
                    //if the virtual position of v would become too far from its real position, continue
                    if (virtualY + 2*dist > maxVerticalEpsilon) continue;

                    VertexCoord newCoord = new VertexCoord(vCoord.Y() + 1, vCoord.X());
                    boolean moreProblems = false;
                    for (Vertex n2: v.getNeighbors()){
                        VertexCoord n2Coord = vertexCanning.get(n2);
                        if (Math.abs(n2Coord.Y() - vCoord.Y()) <= 2 && Math.abs(n2Coord.Y() - newCoord.Y()) >= 3) {
                            moreProblems = true; // n2 is already at the new position
                            break;
                        }
                    }
                    // We cannot move v, as it would create new problems
                    if (moreProblems) continue;

                    virtualEpsilons.put(v, new double[]{virtualY + 2*dist, virtualEpsilons.get(v)[1]});
                    vertexCanning.put(v, newCoord);
                    changed = true;
                    break;
                }

                //if v is 3 cells below n
                if (vCoord.Y() - nCoord.Y() ==  3){
                    double virtualY = v.getY() + virtualEpsilons.get(v)[0];
                    double dist = virtualY - lineSeparations.get(vCoord.Y());
                    //if the distance between between v and the next separation line too large, continue
                    if (dist > maxVerticalEpsilon) continue;
                    //if the virtual position of v would become too far from its real position, continue
                    if (virtualY - 2*dist < -maxVerticalEpsilon) continue;

                    VertexCoord newCoord = new VertexCoord(vCoord.Y() - 1, vCoord.X());
                    boolean moreProblems = false;
                    for (Vertex n2: v.getNeighbors()){
                        VertexCoord n2Coord = vertexCanning.get(n2);
                        if (Math.abs(n2Coord.Y() - vCoord.Y()) <= 2 && Math.abs(n2Coord.Y() - newCoord.Y()) >= 3) {
                            moreProblems = true; // n2 is already at the new position
                            break;
                        }
                    }
                    // We cannot move v, as it would create new problems
                    if (moreProblems) continue;

                    virtualEpsilons.put(v, new double[]{virtualY - 2*dist, virtualEpsilons.get(v)[1]});
                    vertexCanning.put(v, newCoord);
                    changed = true;
                    break;
                }
            }
        } while (changed);
    }

    /**
     * Merges n lines of the grid into n-1.
     * @return a boolean indicating whether the merge was successful.
     */
    private boolean mergeLines(int upperLine, int lowerLine) {
        if (upperLine < 0 || lowerLine < 0 || upperLine >= height || lowerLine >= height) {
            throw new IllegalArgumentException("Invalid line indices: " + upperLine + ", " + lowerLine);
        }
        if (lowerLine - upperLine < 1) {
            throw new IllegalArgumentException("There must at least two lines to merge");
        }

        int deltaY = lowerLine - upperLine;
        double upperSeparation = lineSeparations.get(upperLine);
        double lowerSeparation = lineSeparations.get(lowerLine+1);
        double[] newSeparations = new double[deltaY - 1];

        double totalGap = lowerSeparation - upperSeparation;
        for (int i = 0; i < deltaY - 1; i++) {
            newSeparations[i] = upperSeparation + (i + 1) * totalGap / deltaY;
        }

        HashSet<Vertex> affectedVertices = new HashSet<>();
        for (Vertex v : medium) {
            double vY = v.getY() + virtualEpsilons.get(v)[0];
            if (vY >= upperSeparation && vY <= lowerSeparation) affectedVertices.add(v);
        }

        HashMap<Vertex, VertexCoord> newCanning = new HashMap<>(vertexCanning);
        for (Vertex v : affectedVertices) newCanning.remove(v);

        for (Vertex v : affectedVertices) {
            double vY = v.getY() + virtualEpsilons.get(v)[0];
            int newYIndex = 0;
            while (newYIndex < newSeparations.length && vY > newSeparations[newYIndex]) newYIndex++;

        }

        lineSeparations.removeLast();
        return false;
    }
}
