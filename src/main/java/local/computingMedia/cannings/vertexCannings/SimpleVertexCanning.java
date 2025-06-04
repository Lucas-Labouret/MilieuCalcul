package local.computingMedia.cannings.vertexCannings;

import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.media.Medium;
import local.computingMedia.sLoci.Vertex;

import java.util.HashMap;

/**
 * A container for a prebuilt vertex canning.
 */
public class SimpleVertexCanning implements VertexCanning {
    private final HashMap<Vertex, VertexCoord> vertexCanning;
    private final int width;
    private final int height;

    public SimpleVertexCanning(HashMap<Vertex, VertexCoord> vertexCanning, int width, int height) {
        this.vertexCanning = vertexCanning;
        this.width = width;
        this.height = height;
    }

    public SimpleVertexCanning(VertexCanning vertexCanning) {
        this.vertexCanning = vertexCanning.getVertexCanning();
        this.width = vertexCanning.getWidth();
        this.height = vertexCanning.getHeight();
    }

    @Override public void setMedium(Medium medium) {}
    @Override public void can() {}

    @Override public HashMap<Vertex, VertexCoord> getVertexCanning() { return vertexCanning;}

    @Override public int getWidth() { return width; }
    @Override public int getHeight() { return height; }
}
