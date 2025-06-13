package local.simulatedAnnealing.neighborGenerator;

import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.simulatedAnnealing.neighborGenerator.neighborSelector.NeighborSelector;
import local.computingMedia.cannings.vertexCannings.SimpleVertexCanning;
import local.computingMedia.cannings.vertexCannings.VertexCanning;
import local.computingMedia.sLoci.Edge;
import local.computingMedia.sLoci.Vertex;

import java.util.HashMap;


/**
 * Generates a new neighbor for the given candidate vertex canning.
 * The neighbors can be generated in two ways:
 * 1. By repositioning a vertex to an adjacent cell, if there is no vertex in that position.
 * 2. By merging two adjacent lines or columns into a single line or column if that would cause vertices to overlap.
 */
public class NearestNeighborGenerator implements NeighborGenerator<VertexCanning> {
    // A selector for potential neighbors of a vertex canning.
    private final NeighborSelector<VertexCanning> potentialNeighbors;

    private VertexCanning candidate;

    private HashMap<Vertex, VertexCoord> vertexToCoord;
    private HashMap<VertexCoord, Vertex> coordToVertex;

    // A map to store potential neighbors and their distances from the candidate vertex canning.
    private final HashMap<VertexCanning , Double> distances = new HashMap<>();

    // We want to encourage moving to lines and columns that are already filled,
    // and penalise moving to almost empty lines and columns,
    // so that we can eventually remove lines and columns and increase the density of the vertex canning through merging.
    private final HashMap<Integer, Double> lineFillPenalty = new HashMap<>();
    private final HashMap<Integer, Double> columnFillPenalty = new HashMap<>();

    public NearestNeighborGenerator(NeighborSelector<VertexCanning> neighborSelector) {
        this.potentialNeighbors = neighborSelector;
    }

    @Override
    public VertexCanning generate(VertexCanning candidate) {
        potentialNeighbors.clear();
        distances.clear();
        lineFillPenalty.clear();
        columnFillPenalty.clear();
        this.candidate = candidate;

        HashMap<Vertex, VertexCoord> vertexToCoord = candidate.getVertexCanning();
        HashMap<VertexCoord, Vertex> coordToVertex = new HashMap<>();
        for (Vertex vertex : vertexToCoord.keySet()) {
            VertexCoord coord = vertexToCoord.get(vertex);
            coordToVertex.put(coord, vertex);
        }

        this.vertexToCoord = vertexToCoord;
        this.coordToVertex = coordToVertex;

        setFillPenalties();

        addAllRepositioning();
        addAllMergeLines();
        addAllMergeColumns();

        buildPotentialNeighbors();
        return potentialNeighbors.next();
    }

    private void setFillPenalties(){
        for (int y = 0; y < candidate.getHeight(); y++) {
            double density = 0;
            for (int x = 0; x < candidate.getWidth(); x++) {
                VertexCoord coord = new VertexCoord(y, x);
                if (coordToVertex.get(coord) != null) density++;
            }
            density /= candidate.getWidth();
            lineFillPenalty.put(y, density);
        }

        double maxLineDensity = lineFillPenalty.values().stream().max(Double::compare).orElse(.0);
        for (int y = 0; y < candidate.getHeight(); y++) {
            // We want to encourage moving to lines that are already mostly filled, so we make the penalty higher for lower densities.
            lineFillPenalty.compute(y, (k, density) -> 2 - 2 * density / maxLineDensity);
        }

        for (int x = 0; x < candidate.getWidth(); x++) {
            double density = 0;
            for (int y = 0; y < candidate.getHeight(); y++) {
                VertexCoord coord = new VertexCoord(y, x);
                if (coordToVertex.get(coord) != null) density++;
            }
            density /= candidate.getHeight();
            columnFillPenalty.put(x, density);
        }

        double maxColumnDensity = columnFillPenalty.values().stream().max(Double::compare).orElse(.0);
        for (int x = 0; x < candidate.getWidth(); x++) {
            // We want to encourage moving to columns that are already filled, so we make the penalty higher for lower densities.
            columnFillPenalty.compute(x, (k, density) -> 2 - 2 * density / maxColumnDensity);
        }
    }

    /** Adds all repositioning to the potential neighbors. */
    private void addAllRepositioning() {
        for (Vertex vertex : candidate.getMedium()){
            VertexCoord coord = vertexToCoord.get(vertex);

            VertexCoord north = new VertexCoord(coord.Y()-1, coord.X()     );
            VertexCoord south = new VertexCoord(coord.Y()+1, coord.X()     );
            VertexCoord east  = new VertexCoord(coord.Y()     , coord.X()+1);
            VertexCoord west  = new VertexCoord(coord.Y()     , coord.X()-1);

            addRepositionNS(vertex, north);
            addRepositionNS(vertex, south);

            addRepositionEW(vertex, east);
            addRepositionEW(vertex, west);
        }
    }

    /** Adds an north/south repositioning of a vertex to the potential neighbors if it is valid. */
    private void addRepositionNS(Vertex vertex, VertexCoord neighborCoord) {
        if (neighborCoord.Y() < 0 || neighborCoord.Y() >= candidate.getHeight()) return;
        if (coordToVertex.get(neighborCoord) != null) return; //We can't reposition to a cell that already has a vertex.

        // Find the left and right ends of the segment that the vertex would be repositioned to, so we can compute the vertical between the vertex to reposition and the segment.
        Vertex leftend = null;
        Vertex rightend = null;

        int endIndex = neighborCoord.X();
        while (endIndex >= 0){
            VertexCoord left = new VertexCoord(neighborCoord.Y(), endIndex);
            leftend = coordToVertex.get(left);
            if (leftend != null) break;
            endIndex--;
        }

        endIndex = neighborCoord.X();
        while (endIndex < candidate.getMedium().getWidth()){
            VertexCoord right = new VertexCoord(neighborCoord.Y(), endIndex);
            rightend = coordToVertex.get(right);
            if (rightend != null) break;
            endIndex++;
        }

        // If both ends are null, the target line is empty, so we don't want to refill it.
        if (leftend == null && rightend == null) return;

        SimpleVertexCanning neighborCanning = new SimpleVertexCanning(
                candidate.getMedium(),
                new HashMap<>(vertexToCoord),
                candidate.getWidth(), candidate.getHeight()
        );
        neighborCanning.getVertexCanning().put(vertex, neighborCoord);

        // If one of the ends is null, we consider the segment to extend infinitely vertically
        if (leftend == null){
            leftend = new Vertex(rightend.getX()-1, rightend.getY());
        }
        if (rightend == null){
            rightend = new Vertex(leftend.getX()+1, leftend.getY());
        }

        double distance = vertex.distanceFrom(new Edge(leftend, rightend)) * lineFillPenalty.get(neighborCoord.Y());
        distances.put(neighborCanning, distance);
    }

    /** Adds a east/west repositioning of a vertex to the potential neighbors if it is valid. */
    private void addRepositionEW(Vertex vertex, VertexCoord neighborCoord) {
        // Comments in the method addRepositionNS() apply here as well, but for the east/west direction.

        if (neighborCoord.X() < 0 || neighborCoord.X() >= candidate.getWidth()) return;
        if (coordToVertex.get(neighborCoord) != null) return;

        Vertex upend = null;
        Vertex lowend = null;

        int endIndex = neighborCoord.Y();
        while (endIndex >= 0) {
            VertexCoord up = new VertexCoord(endIndex, neighborCoord.X());
            upend = coordToVertex.get(up);
            if (upend != null) break;
            endIndex--;
        }

        endIndex = neighborCoord.Y();
        while (endIndex < candidate.getMedium().getWidth()) {
            VertexCoord low = new VertexCoord(endIndex, neighborCoord.X());
            lowend = coordToVertex.get(low);
            if (lowend != null) break;
            endIndex++;
        }

        if (upend == null && lowend == null) return;

        SimpleVertexCanning neighborCanning = new SimpleVertexCanning(
                candidate.getMedium(),
                new HashMap<>(vertexToCoord),
                candidate.getWidth(), candidate.getHeight()
        );
        neighborCanning.getVertexCanning().put(vertex, neighborCoord);

        if (upend == null) {
            upend = new Vertex(lowend.getX(), lowend.getY()+1);
        }
        if (lowend == null) {
            lowend = new Vertex(upend.getX(), upend.getY()-1);
        }

        double distance = vertex.distanceFrom(new Edge(upend, lowend)) * columnFillPenalty.get(neighborCoord.X());
        distances.put(neighborCanning, distance);
    }

    /** Adds all merges of two lines to potential neighbors. */
    private void addAllMergeLines() {
        for (int y = 0; y < candidate.getHeight()-1; y++) {
            int topLineLength = 0;
            int botLineLength = 0;
            double topLineAverageY = 0;
            double botLineAverageY = 0;
            boolean overlap = false;

            // Compute the average y-coordinates of the vertices of each line y and y+1
            // We compute the distance between these averages to determine how far apart the lines are.
            for (int x = 0; x < candidate.getWidth(); x++) {
                VertexCoord topCoord = new VertexCoord(y, x);
                if (coordToVertex.get(topCoord) != null) {
                    topLineLength++;
                    topLineAverageY += coordToVertex.get(topCoord).getY();
                }

                VertexCoord botCoord = new VertexCoord(y+1, x);
                if (coordToVertex.get(botCoord) != null) {
                    botLineLength++;
                    botLineAverageY += coordToVertex.get(botCoord).getY();
                }

                // If both lines have a vertex in the same column, we consider them to overlap.
                // This means we can't merge them, so we skip this pair of lines.
                if (coordToVertex.get(topCoord) != null && coordToVertex.get(botCoord) != null) {
                    overlap = true;
                    break;
                }
            }

            if (overlap) continue;

            // If one of the lines is empty, we can merge and consider the distance to be 0.
            if (topLineLength == 0 || botLineLength == 0) {
                addMergeLines(y, 0);
                continue;
            }

            topLineAverageY /= topLineLength;
            botLineAverageY /= botLineLength;
            double distance = Math.abs(topLineAverageY - botLineAverageY);
            addMergeLines(y, distance);
        }
    }

    /** Adds all merges of two columns to potential neighbors. */
    private void addAllMergeColumns() {
        for (int x = 0; x < candidate.getWidth()-1; x++) {
            int leftColLength = 0;
            int rightColLength = 0;
            double leftColAverageX = 0;
            double rightColAverageX = 0;
            boolean overlap = false;

            for (int y = 0; y < candidate.getHeight(); y++) {
                VertexCoord leftCoord = new VertexCoord(y, x);
                if (coordToVertex.get(leftCoord) != null) {
                    leftColLength++;
                    leftColAverageX += coordToVertex.get(leftCoord).getX();
                }

                VertexCoord rightCoord = new VertexCoord(y, x+1);
                if (coordToVertex.get(rightCoord) != null) {
                    rightColLength++;
                    rightColAverageX += coordToVertex.get(rightCoord).getX();
                }

                if (coordToVertex.get(leftCoord) != null && coordToVertex.get(rightCoord) != null) {
                    overlap = true;
                    break;
                }
            }

            if (overlap) continue; // If there is an overlap, skip this pair of columns.

            // If one of the columns is empty, we can merge it and consider its distance to its neighbor to be 0.
            if (leftColLength == 0 || rightColLength == 0) {
                addMergeColumns(x, 0);
                continue;
            }

            leftColAverageX /= leftColLength;
            rightColAverageX /= rightColLength;
            double distance = Math.abs(leftColAverageX - rightColAverageX);
            addMergeColumns(x, distance);
        }
    }

    /** Merges the lines y and y+1 and adds the resulting vertex canning to potential neighbors. */
    private void addMergeLines(int y, double distance) {
        SimpleVertexCanning neighborCanning = new SimpleVertexCanning(
                candidate.getMedium(), new HashMap<>(), candidate.getWidth(), candidate.getHeight()-1
        );
        for (Vertex vertex : vertexToCoord.keySet()) {
            VertexCoord coord = vertexToCoord.get(vertex);

            // Shift up all vertices in the line y+1 and after.
            // This will merge the two lines y and y+1, and fill the empty line left by the merge.
            if (coord.Y() <= y) neighborCanning.getVertexCanning().put(vertex, coord);
            else neighborCanning.getVertexCanning().put(vertex, new VertexCoord(coord.Y()-1, coord.X()));
        }
        distances.put(neighborCanning, distance/10);
    }

    /** Merges the columns x and x+1, and adds the resulting vertex canning to potential neighbors. */
    private void addMergeColumns(int x, double distance) {
        SimpleVertexCanning neighborCanning = new SimpleVertexCanning(
                candidate.getMedium(), new HashMap<>(), candidate.getWidth()-1, candidate.getHeight()
        );
        for (Vertex vertex : vertexToCoord.keySet()) {
            VertexCoord coord = vertexToCoord.get(vertex);

            // Shift up all vertices in the line y+1 and after.
            // This will merge the two lines y and y+1, and fill the empty line left by the merge.
            if (coord.X() <= x) neighborCanning.getVertexCanning().put(vertex, coord);
            else neighborCanning.getVertexCanning().put(vertex, new VertexCoord(coord.Y(), coord.X()-1));
        }
        distances.put(neighborCanning, distance/10);
    }

    /** Defines a convex function defined on [0, 1] -> [0, 1] */
    private interface ConvexFunction {
        double apply(double weight);
    }
    /**
     * Makes the difference between high and low weights more extreme.
     * This is useful to make the selection of neighbors more biased towards closer ones.
     */
    private double makeConvexer(ConvexFunction f, double weight, int iter) {
        for (int i = 0; i < iter; i++) weight = f.apply(weight);
        return weight;
    }

    /** Builds the potential neighbors based on the distances calculated. */
    private void buildPotentialNeighbors() {
        double maxDistance = distances.values().stream().max(Double::compare).orElse(1d);
        if (maxDistance == 0) return;
        for (VertexCanning neighbor : distances.keySet()) {
            double distance = distances.get(neighbor);
            double weight = makeConvexer(x -> x*x, 1 - distance/maxDistance, 3);
            potentialNeighbors.add(weight, neighbor);
        }
    }
}
