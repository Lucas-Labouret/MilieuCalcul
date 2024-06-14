package local.furthestpointoptimization.model.miseEnBoite;

import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;

import java.util.HashMap;
import java.util.Set;

public class DistanceToSideGetter {
    private final HashMap<Vertex, Integer> coords = new HashMap<>();

    private enum SIDE {
        TOP, LEFT, RIGHT, BOTTOM
    }

    private Vertex getStart(Set<Vertex> vs, SIDE side){
        for (Vertex vertex : vs) {
            for (Vertex neighbor : vertex.getNeighbors()) {
                if (side == SIDE.TOP && neighbor.isTopBorder()) return vertex;
                if (side == SIDE.LEFT && neighbor.isLeftBorder()) return vertex;
                if (side == SIDE.RIGHT && neighbor.isRightBorder()) return vertex;
                if (side == SIDE.BOTTOM && neighbor.isBottomBorder()) return vertex;
            }
        }
        return null;
    }

    private boolean nearBorder(Vertex vertex, SIDE side){
        for (Vertex neighbor : vertex.getNeighbors()) {
            if (side == SIDE.TOP && neighbor.isTopBorder()) return true;
            if (side == SIDE.LEFT && neighbor.isLeftBorder()) return true;
            if (side == SIDE.RIGHT && neighbor.isRightBorder()) return true;
            if (side == SIDE.BOTTOM && neighbor.isBottomBorder()) return true;
        }
        return false;
    }

    private void propagate(Vertex vertex, SIDE side){
        if (vertex.isBorder()) return;

        if (nearBorder(vertex, side)) coords.put(vertex, 0);
        for (Vertex neighbor : vertex.getNeighbors()) {
            if (neighbor.isBorder()) continue;
            if (coords.get(neighbor) == null) {
                coords.put(neighbor, coords.get(vertex) + 1);
                propagate(neighbor, side);
            } else if (coords.get(neighbor) > coords.get(vertex) + 1) {
                coords.put(neighbor, coords.get(vertex) + 1);
                propagate(neighbor, side);
            }
        }
    }

    private HashMap<Vertex, Integer> getDistanceToSide(VertexSet vs, SIDE side) {
        coords.clear();

        for (Vertex vertex : vs)
            if (!vertex.isBorder()) coords.put(vertex, null);

        Vertex start = getStart(coords.keySet(), side);
        coords.put(start, 0);

        if (start == null) throw new IllegalArgumentException("Invalid border configuration");
        propagate(start, side);

        return coords;
    }

    public HashMap<Vertex, Integer> getDistanceToTop(VertexSet vs) {
        return getDistanceToSide(vs, SIDE.TOP);
    }
    public HashMap<Vertex, Integer> getDistanceToLeft(VertexSet vs) {
        return getDistanceToSide(vs, SIDE.LEFT);
    }
    public HashMap<Vertex, Integer> getDistanceToRight(VertexSet vs) {
        return getDistanceToSide(vs, SIDE.RIGHT);
    }
    public HashMap<Vertex, Integer> getDistanceToBottom(VertexSet vs) {
        return getDistanceToSide(vs, SIDE.BOTTOM);
    }
}
