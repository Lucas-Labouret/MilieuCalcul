package local.furthestpointoptimization.model.miseEnBoite;

import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;

import java.util.ArrayList;
import java.util.HashMap;

public class TopDistanceXSortedLinesMeb implements MiseEnBoite {
    @Override
    public HashMap<Vertex, Coord> miseEnBoite(VertexSet vertexSet) {
        DistanceToSideGetter distanceToSideGetter = new DistanceToSideGetter();
        HashMap<Vertex, Integer> topCoords = distanceToSideGetter.getDistanceToTop(vertexSet);

        HashMap<Integer, ArrayList<Vertex>> lines = new HashMap<>();
        for (Vertex vertex : topCoords.keySet()) {
            int top = topCoords.get(vertex);
            if (lines.get(top) == null) lines.put(top, new ArrayList<>());
            lines.get(top).add(vertex);
        }
        for (ArrayList<Vertex> line : lines.values()) {
            line.sort((v1, v2) -> {
                if (v1.getX() < v2.getX()) return -1;
                if (v1.getX() > v2.getX()) return 1;
                return Double.compare(v1.getY(), v2.getY());
            });
        }

        HashMap<Vertex, Coord> result = new HashMap<>();
        for (int y : lines.keySet()) {
            ArrayList<Vertex> line = lines.get(y);
            for (int x = 0; x < line.size(); x++) {
                result.put(line.get(x), new Coord(y, x));
            }
        }
        return result;
    }
}
