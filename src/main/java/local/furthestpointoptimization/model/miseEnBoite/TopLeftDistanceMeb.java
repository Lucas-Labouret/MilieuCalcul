package local.furthestpointoptimization.model.miseEnBoite;

import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;

import java.util.HashMap;
import java.util.Set;

public class TopLeftDistanceMeb implements MiseEnBoite {
    private HashMap<Vertex, Integer> yCoord = new HashMap<>();
    private HashMap<Vertex, Integer> xCoord = new HashMap<>();

    private enum SIDE {
        TOP, LEFT
    }

    private Vertex getStart(Set<Vertex> vs, SIDE side){
        for (Vertex vertex : vs) {
            for (Vertex neighbor : vertex.getNeighbors()) {
                if (side == SIDE.TOP && neighbor.isTopBorder()) return vertex;
                if (side == SIDE.LEFT && neighbor.isLeftBorder()) return vertex;
            }
        }
        return null;
    }

    private boolean nearBorder(Vertex vertex, SIDE side){
        for (Vertex neighbor : vertex.getNeighbors()) {
            if (side == SIDE.TOP && neighbor.isTopBorder()) return true;
            if (side == SIDE.LEFT && neighbor.isLeftBorder()) return true;
        }
        return false;
    }

    private void propagate(Vertex vertex, SIDE side){
        if (vertex.isBorder()) return;

        HashMap<Vertex, Integer> coords = (side == SIDE.TOP ? yCoord : xCoord);
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

    private void normalize(int width, int height){
        int maxY = 0;
        int maxX = 0;
        for (Vertex vertex : yCoord.keySet()) {
            if (yCoord.get(vertex) > maxY) maxY = yCoord.get(vertex);
            if (xCoord.get(vertex) > maxX) maxX = xCoord.get(vertex);
        }

        for (Vertex vertex : yCoord.keySet()) {
            int newY = (int) Math.round(((double) yCoord.get(vertex) / maxY) * height);
            int newX = (int) Math.round(((double) xCoord.get(vertex) / maxX) * width);
            yCoord.put(vertex, newY);
            xCoord.put(vertex, newX);
        }
    }

    @Override
    public HashMap<Vertex, Coord> miseEnBoite(VertexSet vs) {
        for (Vertex vertex : vs) {
            if (vertex.isBorder()) continue;
            yCoord.put(vertex, null);
            xCoord.put(vertex, null);
        }

        Vertex yStart = getStart(yCoord.keySet(), SIDE.TOP);
        yCoord.put(yStart, 0);

        Vertex xStart = getStart(xCoord.keySet(), SIDE.LEFT);
        xCoord.put(xStart, 0);

        if (yStart == null || xStart == null) throw new IllegalArgumentException("Invalid border configuration");

        propagate(yStart, SIDE.TOP);
        propagate(xStart, SIDE.LEFT);

        //normalize(width, height);

        HashMap<Vertex, Coord> result = new HashMap<>();
        for (Vertex vertex : yCoord.keySet()) {
            result.put(vertex, new Coord(yCoord.get(vertex), xCoord.get(vertex)));
        }
        return result;
    }
}
