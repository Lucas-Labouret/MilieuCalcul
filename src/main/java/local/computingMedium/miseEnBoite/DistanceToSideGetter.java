package local.computingMedium.miseEnBoite;

import local.computingMedium.Vertex;
import local.computingMedium.media.Medium;

import java.util.HashMap;
import java.util.Set;

public class DistanceToSideGetter {
    private final HashMap<Vertex, Integer> coords = new HashMap<>();

    private enum SIDE {
        TOP, LEFT, RIGHT, BOTTOM
    }

    private Vertex getStart(Set<Vertex> vertices, SIDE side){
        for (Vertex vertex : vertices) {
            //System.out.println("isBorder: " + vertex.isBorder() + " side: " + side + " isTopBorder: " + vertex.isTopBorder() + " isLeftBorder: " + vertex.isLeftBorder() + " isRightBorder: " + vertex.isRightBorder() + " isBottomBorder: " + vertex.isBottomBorder());
            if (side == SIDE.TOP && vertex.isTopBorder()) return vertex;
            if (side == SIDE.LEFT && vertex.isLeftBorder()) return vertex;
            if (side == SIDE.RIGHT && vertex.isRightBorder()) return vertex;
            if (side == SIDE.BOTTOM && vertex.isBottomBorder()) return vertex;
        }
        return null;
    }

    private boolean nearBorder(Vertex vertex, SIDE side){
        //System.out.println("isBorder: " + vertex.isBorder() + " side: " + side + " isTopBorder: " + vertex.isTopBorder() + " isLeftBorder: " + vertex.isLeftBorder() + " isRightBorder: " + vertex.isRightBorder() + " isBottomBorder: " + vertex.isBottomBorder());
        if (side == SIDE.TOP && vertex.isTopBorder()) return true;
        if (side == SIDE.LEFT && vertex.isLeftBorder()) return true;
        if (side == SIDE.RIGHT && vertex.isRightBorder()) return true;
        return side == SIDE.BOTTOM && vertex.isBottomBorder();
    }

    private void propagate(Vertex vertex, SIDE side){
        if (nearBorder(vertex, side)) coords.put(vertex, 0);
        for (Vertex neighbor : vertex.getNeighbors()) {
            if (coords.get(neighbor) == null) {
                coords.put(neighbor, coords.get(vertex) + 1);
                propagate(neighbor, side);
            } else if (coords.get(neighbor) > coords.get(vertex) + 1) {
                coords.put(neighbor, coords.get(vertex) + 1);
                propagate(neighbor, side);
            }
        }
    }

    private HashMap<Vertex, Integer> getDistanceToSide(Medium medium, SIDE side) {
        coords.clear();

        for (Vertex vertex : medium)
            coords.put(vertex, null);

        Vertex start = getStart(coords.keySet(), side);
        coords.put(start, 0);

        if (start == null) throw new IllegalArgumentException("Invalid border configuration");
        propagate(start, side);

        return coords;
    }

    public HashMap<Vertex, Integer> getDistanceToTop(Medium medium) {
        return getDistanceToSide(medium, SIDE.TOP);
    }
    public HashMap<Vertex, Integer> getDistanceToLeft(Medium medium) {
        return getDistanceToSide(medium, SIDE.LEFT);
    }
    public HashMap<Vertex, Integer> getDistanceToRight(Medium medium) {
        return getDistanceToSide(medium, SIDE.RIGHT);
    }
    public HashMap<Vertex, Integer> getDistanceToBottom(Medium medium) {
        return getDistanceToSide(medium, SIDE.BOTTOM);
    }
}
