package local.computingMedia.cannings.vertexCannings;

import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.sLoci.Vertex;
import local.computingMedia.media.Medium;

import java.util.HashMap;

/**
 * This vertex canning implementation calculates the distance of each vertex to the top and left sides of the medium
 * and uses these distances as the coordinates for canning.
 * <p>
 * It is deprecated because it sometimes assigns the same coordinates to multiple vertices.
 * </p>
 */
@Deprecated
public class TopLeftDistanceVCanning implements VertexCanning {
    private HashMap<Vertex, VertexCoord> vertexCanning = null;
    private Medium medium;

    @Override public int getWidth() { return -1; }
    @Override public int getHeight() { return -1; }
    @Override public double getDensity() { return -1; }

    @Override public void setMedium(Medium medium) { this.medium = medium; }
    @Override public HashMap<Vertex, VertexCoord> getVertexCanning() { return this.vertexCanning; }

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
