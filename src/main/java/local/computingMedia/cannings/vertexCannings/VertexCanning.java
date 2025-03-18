package local.computingMedia.cannings.vertexCannings;

import local.computingMedia.cannings.coords.sCoords.VertexCoord;
import local.computingMedia.sLoci.Vertex;
import local.computingMedia.media.Medium;

import java.util.HashMap;

public interface VertexCanning {
    void setMedium(Medium medium);
    void can();

    HashMap<Vertex, VertexCoord> getVertexCanning();

    int getWidth();
    int getHeight();
}
