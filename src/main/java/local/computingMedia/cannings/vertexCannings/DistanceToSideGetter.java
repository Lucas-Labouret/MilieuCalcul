package local.computingMedia.cannings.vertexCannings;

import local.computingMedia.sLoci.Vertex;
import local.computingMedia.media.Medium;

import java.util.HashMap;
import java.util.Set;

/**
 * Utility class to compute the distance of each vertex in a medium to a given border side.
 */
public class DistanceToSideGetter {
    private enum SIDE {
        TOP, LEFT, RIGHT, BOTTOM
    }

    /** Finds a vertex to start the propagation from, based on the specified side */
    private Vertex getStart(Set<Vertex> vertices, SIDE side){
        for (Vertex vertex : vertices) {
            if (side == SIDE.TOP && vertex.isTopBorder()) return vertex;
            if (side == SIDE.LEFT && vertex.isLeftBorder()) return vertex;
            if (side == SIDE.RIGHT && vertex.isRightBorder()) return vertex;
            if (side == SIDE.BOTTOM && vertex.isBottomBorder()) return vertex;
        }
        return null;
    }

    /** Checks if the specified vertex is near the specified border specified side */
    private boolean nearBorder(Vertex vertex, SIDE side){
        if (side == SIDE.TOP && vertex.isTopBorder()) return true;
        if (side == SIDE.LEFT && vertex.isLeftBorder()) return true;
        if (side == SIDE.RIGHT && vertex.isRightBorder()) return true;
        return side == SIDE.BOTTOM && vertex.isBottomBorder();
    }

    /**
     * Recursively propagates the distance from the starting vertex to its neighbors.
     * @param vertex the current vertex being processed
     * @param side the side to which the distance is calculated (TOP, LEFT, RIGHT, BOTTOM)
     */
    private void propagate(HashMap<Vertex, Integer> coords, Vertex vertex, SIDE side){
        if (nearBorder(vertex, side)) coords.put(vertex, 0);
        for (Vertex neighbor : vertex.getNeighbors()) {
            if (coords.get(neighbor) == null) {
                coords.put(neighbor, coords.get(vertex) + 1);
                propagate(coords, neighbor, side);
            } else if (coords.get(neighbor) > coords.get(vertex) + 1) {
                coords.put(neighbor, coords.get(vertex) + 1);
                propagate(coords, neighbor, side);
            }
        }
    }

    /**
     * Calculates the distance of each vertex in the medium to the specified side.
     * @param medium the medium containing the vertices
     * @param side the side to which the distance is calculated (TOP, LEFT, RIGHT, BOTTOM)
     * @return a map where keys are vertices and values are their distances to the specified side
     */
    private HashMap<Vertex, Integer> getDistanceToSide(Medium medium, SIDE side) {
        HashMap<Vertex, Integer> coords = new HashMap<>();

        Vertex start = getStart(medium, side);
        coords.put(start, 0);

        if (start == null) throw new IllegalArgumentException("Invalid border configuration");
        propagate(coords, start, side);

        return coords;
    }

    /** @return the distance of each vertex to the top border */
    public HashMap<Vertex, Integer> getDistanceToTop(Medium medium) { return getDistanceToSide(medium, SIDE.TOP); }
    /** @return the distance of each vertex to the left border */
    public HashMap<Vertex, Integer> getDistanceToLeft(Medium medium) { return getDistanceToSide(medium, SIDE.LEFT); }
    /** @return the distance of each vertex to the right border */
    public HashMap<Vertex, Integer> getDistanceToRight(Medium medium) { return getDistanceToSide(medium, SIDE.RIGHT); }
    /** @return the distance of each vertex to the bottom border */
    public HashMap<Vertex, Integer> getDistanceToBottom(Medium medium) { return getDistanceToSide(medium, SIDE.BOTTOM); }
}
