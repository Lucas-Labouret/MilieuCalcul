package local.computingMedia.cannings.vertexCannings;

import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.media.Medium;
import local.computingMedia.sLoci.Vertex;

import java.util.HashMap;

/**
 * A container for a prebuilt vertex canning.
 */
public class SimpleVertexCanning implements VertexCanning {
    private final Medium medium;
    private final HashMap<Vertex, VertexCoord> vertexCanning;
    private final int width;
    private final int height;

    public SimpleVertexCanning(Medium medium, HashMap<Vertex, VertexCoord> vertexCanning, int width, int height) {
        this.medium = medium;
        this.vertexCanning = vertexCanning;
        this.width = width;
        this.height = height;
    }

    public SimpleVertexCanning(VertexCanning vertexCanning) {
        this.medium = vertexCanning.getMedium();
        this.vertexCanning = vertexCanning.getVertexCanning();
        this.width = vertexCanning.getWidth();
        this.height = vertexCanning.getHeight();
    }

    @Override public Medium getMedium() { return medium; }

    @Override public int getWidth() { return width; }
    @Override public int getHeight() { return height; }

    @Override public void can() {}

    @Override public HashMap<Vertex, VertexCoord> getVertexCanning() { return vertexCanning; }
}
