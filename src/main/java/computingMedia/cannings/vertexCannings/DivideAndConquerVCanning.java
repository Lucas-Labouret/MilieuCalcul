package computingMedia.cannings.vertexCannings;

import computingMedia.cannings.coords.sCoords.VertexCoord;
import computingMedia.media.Medium;
import computingMedia.sLoci.Vertex;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

public class DivideAndConquerVCanning implements VertexCanning {
    private final HashMap<Vertex, VertexCoord> vertexCanning = new HashMap<>();
    private final Medium medium;
    private final double targetDensity;
    private int width, height;

    @Override public HashMap<Vertex, VertexCoord> getVertexCanning() { return vertexCanning; }
    @Override public Medium getMedium() { return medium; }
    @Override public int getWidth() { return width; }
    @Override public int getHeight() { return height; }

    public DivideAndConquerVCanning(Medium medium, double targetDensity) {
        this.medium = medium;
        this.targetDensity = targetDensity;
    }

    public DivideAndConquerVCanning(Medium medium) {
        this(medium, 0.6);
    }

    @Override
    public void can() {
        double ratio = medium.getHeight() / medium.getWidth();
        int expectedWidth  = (int) Math.ceil(Math.sqrt(medium.size() * ratio));
        int expectedHeight = (int) Math.ceil(Math.sqrt(medium.size() / ratio));

        ArrayList<Vertex> ySorted = new ArrayList<>();
        ArrayList<Vertex> xSorted = new ArrayList<>();
        for (Vertex vertex : medium) {
            ySorted.add(vertex);
            xSorted.add(vertex);
        }
        ySorted.sort(Comparator.comparingDouble(Vertex::getY));
        xSorted.sort(Comparator.comparingDouble(Vertex::getX));

        HashMap<Vertex, Integer> lineMap = new HashMap<>();
        HashMap<Vertex, Integer> colMap = new HashMap<>();
        fillMap(true, expectedHeight, ySorted, 0, ySorted.size(), lineMap, 0, expectedHeight);
        fillMap(false, expectedWidth, xSorted, 0, xSorted.size(), colMap, 0, expectedWidth);
        System.out.println("Y: " + lineMap.size() + " X: " + colMap.size());

        HashSet<Vertex> missingVertices = new HashSet<>();
        HashMap<VertexCoord, Vertex> reversedCanning = new HashMap<>();

        combineMaps(lineMap, colMap, missingVertices, reversedCanning);
        applyTargetDensity(true, reversedCanning);
        applyTargetDensity(false, reversedCanning);
        placeMissingVertices(ySorted, xSorted, lineMap, colMap, missingVertices, reversedCanning);

        for (VertexCoord coord : reversedCanning.keySet())
            vertexCanning.put(reversedCanning.get(coord), coord);
    }

    private void combineMaps(
            HashMap<Vertex, Integer> lineMap, HashMap<Vertex, Integer> colMap,
            HashSet<Vertex> missingVertices,
            HashMap<VertexCoord, Vertex> reversedCanning
    ) {
        height = width = 0;
        for (Vertex vertex : medium) {
            if (!lineMap.containsKey(vertex) || !colMap.containsKey(vertex)) {
                missingVertices.add(vertex);
                continue;
            }
            int line = lineMap.get(vertex);
            int col = colMap.get(vertex);
            if (line < 0 || col < 0) {
                throw new IllegalStateException("Invalid canning coordinates: (" + line + ", " + col + ")");
            }
            VertexCoord coord = new VertexCoord(line, col);
            if (reversedCanning.containsKey(coord)) {
                colMap.remove(vertex);
                lineMap.remove(vertex);
                missingVertices.add(vertex);
                continue;
            }
            reversedCanning.put(coord, vertex);

            if (line > height) height = line;
            if (col > width) width = col;
        }
        height++;
        width++;
    }

    private void applyTargetDensity(boolean lines, HashMap<VertexCoord, Vertex> reversedCanning) {
        ArrayList<Double> separations = new ArrayList<>();
        separations.add(0.);
        for (int i = 0; i < (lines? height : width) - 1; i++) {
            int lowCount = 0;
            double lowSep = 0;
            int highCount = 0;
            double highSep = 0;
            for (int j = 0; j < (lines? width : height); j++) {
                VertexCoord lowCoord = lines? new VertexCoord(i, j) : new VertexCoord(j, i);
                VertexCoord highCoord = lines? new VertexCoord(i + 1, j) : new VertexCoord(j, i + 1);

                Vertex lowVertex = reversedCanning.get(lowCoord);
                Vertex highVertex = reversedCanning.get(highCoord);

                if (lowVertex != null) {
                    lowSep += lines ? lowVertex.getY() : lowVertex.getX();
                    lowCount++;
                }
                if (highVertex != null) {
                    highSep += lines ? highVertex.getY() : highVertex.getX();
                    highCount++;
                }
            }
            if (lowCount > 0) lowSep /= lowCount;
            if (highCount > 0) highSep /= highCount;
            if (lowCount == 0 && highCount == 0) throw new IllegalStateException("No vertices found for line/column " + i);
            double separation = (highSep + lowSep) / 2;
            separations.add(separation);
        }
        separations.add(lines ? medium.getHeight() : medium.getWidth());

        for (int i = 0; i < separations.size() - 1; i++) {
            double lowSep = separations.get(i);
            double highSep = separations.get(i + 1);
            System.out.println(i + " " + (highSep - lowSep));
        }
        System.out.println("---");

        for (int i = (lines? height : width) - 1; i >= 0; i--) for (int j = 0; j < (lines ? width : height); j++) {
            VertexCoord coord = lines? new VertexCoord(i, j) : new VertexCoord(j, i);
            Vertex vertex = reversedCanning.get(coord);
            if (vertex == null) continue;

            double lowSep = separations.get(i);
            double highSep = separations.get(i + 1);
            double relativePosition = ((lines? vertex.getY() : vertex.getX()) - lowSep) / (highSep - lowSep);

            if (relativePosition < targetDensity) continue;

            Vertex currentVertex = vertex;
            VertexCoord nextCoord;
            Vertex nextVertex;
            for (int k = i; k < (lines ? height : width) - 1; k++) {
                nextCoord = lines ? new VertexCoord(k+1, j) : new VertexCoord(j, k+1);
                nextVertex = reversedCanning.get(nextCoord);

                reversedCanning.remove(coord);
                reversedCanning.put(nextCoord, currentVertex);

                currentVertex = nextVertex;
            }
            if (lines && currentVertex != null) {
                nextCoord = new VertexCoord(height, j);
                reversedCanning.put(nextCoord, currentVertex);
                height++;
            } else if (!lines && currentVertex != null) {
                nextCoord = new VertexCoord(j, width);
                reversedCanning.put(nextCoord, currentVertex);
                width++;
            }
        }
    }

    private void placeMissingVertices(
            ArrayList<Vertex> ySorted, ArrayList<Vertex> xSorted,
            HashMap<Vertex, Integer> lineMap, HashMap<Vertex, Integer> colMap,
            HashSet<Vertex> missingVertices, HashMap<VertexCoord, Vertex> reversedCanning
    ) {
        HashSet<VertexCoord> holes = new HashSet<>();
        for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
            VertexCoord coord = new VertexCoord(y, x);
            if (!reversedCanning.containsKey(coord)) holes.add(coord);
        }

        for (Vertex vertex : missingVertices) {
            int yIndex = ySorted.indexOf(vertex);
            int xIndex = xSorted.indexOf(vertex);

            if (yIndex == -1 || xIndex == -1) {
                throw new IllegalStateException("Vertex " + vertex + " not found in sorted lists.");
            }

            int lowBound = yIndex - 1;
            int highBound = yIndex + 1;
            int leftBound = xIndex - 1;
            int rightBound = xIndex + 1;

            while (lowBound > 0 && !lineMap.containsKey(ySorted.get(lowBound))) lowBound--;
            while (highBound < ySorted.size() - 2 && !lineMap.containsKey(ySorted.get(highBound))) highBound++;
            while (leftBound > 0 && !colMap.containsKey(xSorted.get(leftBound))) leftBound--;
            while (rightBound < xSorted.size() - 2 && !colMap.containsKey(xSorted.get(rightBound))) rightBound++;

            int lowLine = lineMap.get(ySorted.get(lowBound));
            int highLine = lineMap.get(ySorted.get(highBound));
            int leftCol = colMap.get(xSorted.get(leftBound));
            int rightCol = colMap.get(xSorted.get(rightBound));

            int line;
            if (lowLine == highLine) {
                line = lowLine;
            } else {
                double lowDist = vertex.getY() - ySorted.get(lowLine).getY();
                double highDist = ySorted.get(highLine).getY() - vertex.getY();
                line = (lowDist < highDist) ? lowLine : highLine;
            }

            int col;
            if (leftCol == rightCol) {
                col = leftCol;
            } else {
                double leftDist = vertex.getX() - xSorted.get(leftCol).getX();
                double rightDist = xSorted.get(rightCol).getX() - vertex.getX();
                col = (leftDist < rightDist) ? leftCol : rightCol;
            }

            VertexCoord coord = new VertexCoord(line, col);

            double closestHoleDist = Double.MAX_VALUE;
            VertexCoord closestHole = null;
            for (VertexCoord hole : holes) {
                double dist = Math.sqrt((hole.X() - coord.X()) * (hole.X() - coord.X()) +
                        (hole.Y() - coord.Y()) * (hole.Y() - coord.Y()));
                if (dist < closestHoleDist) {
                    closestHoleDist = dist;
                    closestHole = hole;
                }
            }
            if (closestHole != null) {
                vertexCanning.put(vertex, closestHole);
                holes.remove(closestHole);
            } else {
                throw new IllegalStateException("No available hole for vertex " + vertex + " at (" + line + ", " + col + ")");
            }
        }
    }

    /**
     * Associate most vertices in the sorted list with a line or column index in the map.
     * @param lines if true, fills for lines (y-coordinates), if false, fills for columns (x-coordinates).
     * @param expectedSize the expected amount of vertices per lines/columns.
     * @param sorted the sorted list of vertices based on y or x coordinates.
     * @param sortedStart the start index in the sorted list.
     * @param sortedEnd the end index in the sorted list.
     * @param map the map to fill with vertices and their corresponding indices.
     * @param mapStart the start index in the map.
     * @param mapEnd the end index in the map.
     */
    private void fillMap(
            boolean lines, int expectedSize,
            ArrayList<Vertex> sorted, int sortedStart, int sortedEnd,
            HashMap<Vertex, Integer> map, int mapStart, int mapEnd
    ) {
        int size = mapEnd - mapStart;
        int mid = (mapStart + mapEnd)/2;

        // If the number of vertices is even, we can split the range evenly and recursively fill each half
        if (size%2 == 0) {
            fillMap(lines, expectedSize,
                    sorted, sortedStart, (sortedStart + sortedEnd)/2 - 1,
                    map, mapStart, mid);
            fillMap(lines, expectedSize,
                    sorted, (sortedStart + sortedEnd)/2, sortedEnd,
                    map, mid, mapEnd);
        // If the number of vertices is odd, we fill the middle line/column and then recursively fill the first and last halves
        } else {
            int lowerBound, upperBound;
            if ((sortedEnd - sortedStart)%2 == 0){
                lowerBound = (sortedStart + sortedEnd) / 2 - 1;
                upperBound = (sortedStart + sortedEnd) / 2;
            } else {
                 upperBound = lowerBound = (sortedStart + sortedEnd) / 2;
            }

            double medianPoint;
            if (lines) {
                medianPoint = (sorted.get(lowerBound).getY() + sorted.get(upperBound).getY()) / 2;
            } else {
                medianPoint = (sorted.get(lowerBound).getX() + sorted.get(upperBound).getX()) / 2;
            }

            for (int ignored = 0; ignored < expectedSize; ignored++) {
                double lowerDist = -1;
                if (lowerBound >= 0)
                    lowerDist = lines? medianPoint - sorted.get(lowerBound).getY() :
                                       medianPoint - sorted.get(lowerBound).getX();

                double upperDist = -1;
                if (upperBound < sorted.size())
                    upperDist = lines? sorted.get(upperBound).getY() - medianPoint :
                                       sorted.get(upperBound).getX() - medianPoint;

                if (lowerDist != -1 && lowerDist < upperDist) {
                    System.out.println("lowerDist = " + lowerDist);
                    map.put(sorted.get(lowerBound), mid);
                    lowerBound--;
                } else if (upperDist != -1) {
                    System.out.println("upperDist = " + upperDist);
                    map.put(sorted.get(upperBound), mid);
                    upperBound++;
                } else {
                    System.out.println("No more vertices to assign in this range");
                    return; // no more vertices to assign in this range
                }
            }

            if (size == 1) return; // base recursion case

            fillMap(lines, expectedSize,
                    sorted, sortedStart, lowerBound+1,
                    map, mapStart, mid);
            fillMap(lines, expectedSize,
                    sorted, upperBound, sortedEnd,
                    map, mid+1, mapEnd);
        }
    }
}
