package local.computingMedia.cannings.vertexCannings;

import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.sLoci.Vertex;
import local.computingMedia.media.Medium;

import java.util.HashMap;

@Deprecated //Gave the same coord to multiple vertices
public class TopLeftDistanceVCanning implements VertexCanning {
    private HashMap<Vertex, VertexCoord> vertexCanning = null;
    private Medium medium;

    @Override public int getWidth() { return -1; }
    @Override public int getHeight() { return -1; }

    public void setMedium(Medium medium) { this.medium = medium; }
    public HashMap<Vertex, VertexCoord> getVertexCanning() { return this.vertexCanning; }

    @Override
    public void can() {
        DistanceToSideGetter distanceToSideGetter = new DistanceToSideGetter();
        HashMap<Vertex, Integer> topCoords = distanceToSideGetter.getDistanceToTop(medium);
        HashMap<Vertex, Integer> leftCoords = distanceToSideGetter.getDistanceToLeft(medium);

        HashMap<Vertex, VertexCoord> result = new HashMap<>();
        for (Vertex vertex : topCoords.keySet()) {
            result.put(vertex, new VertexCoord(topCoords.get(vertex), leftCoords.get(vertex)));
        }
        vertexCanning = result;
    }
}
