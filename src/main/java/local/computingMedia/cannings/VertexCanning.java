package local.computingMedia.cannings;

import local.computingMedia.cannings.Coords.sCoords.VertexCoord;
import local.computingMedia.sLoci.Vertex;
import local.computingMedia.media.Medium;

import java.util.HashMap;

public interface VertexCanning {
    void setMedium(Medium medium);
    void can();

    HashMap<Vertex, VertexCoord> getVertexCanning();
}
