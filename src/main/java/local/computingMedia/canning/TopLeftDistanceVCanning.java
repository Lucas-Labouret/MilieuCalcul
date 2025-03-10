package local.computingMedia.canning;

import local.computingMedia.geometry.Vertex;
import local.computingMedia.media.Medium;

import java.util.HashMap;

@Deprecated //Gave the same coord to multiple vertices
public class TopLeftDistanceVCanning implements VertexCanning {
    private HashMap<Vertex, Coord> vertexCanning = null;
    private Medium medium;

    public void setMedium(Medium medium) { this.medium = medium; }
    public HashMap<Vertex, Coord> getVertexCanning() { return this.vertexCanning; }

    @Override
    public void can() {
        DistanceToSideGetter distanceToSideGetter = new DistanceToSideGetter();
        HashMap<Vertex, Integer> topCoords = distanceToSideGetter.getDistanceToTop(medium);
        HashMap<Vertex, Integer> leftCoords = distanceToSideGetter.getDistanceToLeft(medium);

        HashMap<Vertex, Coord> result = new HashMap<>();
        for (Vertex vertex : topCoords.keySet()) {
            result.put(vertex, new Coord(topCoords.get(vertex), leftCoords.get(vertex)));
        }
        vertexCanning = result;
    }
}
