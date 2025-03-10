package local.computingMedia.cannings;

import local.computingMedia.cannings.Coords.sCoords.VertexCoord;
import local.computingMedia.sLoci.Vertex;
import local.computingMedia.media.Medium;

import java.util.ArrayList;
import java.util.HashMap;

public class TopDistanceXSortedLinesVCanning implements VertexCanning {
    private HashMap<Vertex, VertexCoord> vertexCanning = null;
    private Medium medium;

    public void setMedium(Medium medium) { this.medium = medium; }
    public HashMap<Vertex, VertexCoord> getVertexCanning() { return this.vertexCanning; }

    @Override
    public void can() {
        DistanceToSideGetter distanceToSideGetter = new DistanceToSideGetter();
        HashMap<Vertex, Integer> topCoords = distanceToSideGetter.getDistanceToTop(medium);

        HashMap<Integer, ArrayList<Vertex>> lines = new HashMap<>();
        for (Vertex vertex : topCoords.keySet()) {
            int top = topCoords.get(vertex);
            lines.computeIfAbsent(top, k -> new ArrayList<>());
            lines.get(top).add(vertex);
        }
        for (ArrayList<Vertex> line : lines.values()) {
            line.sort((v1, v2) -> {
                if (v1.getX() < v2.getX()) return -1;
                if (v1.getX() > v2.getX()) return 1;
                return Double.compare(v1.getY(), v2.getY());
            });
        }

        HashMap<Vertex, VertexCoord> result = new HashMap<>();
        for (int y : lines.keySet()) {
            ArrayList<Vertex> line = lines.get(y);
            for (int x = 0; x < line.size(); x++) {
                result.put(line.get(x), new VertexCoord(y, x));
            }
        }
        vertexCanning = result;
    }
}
