package local.computingMedia.canning;

import local.computingMedia.geometry.Vertex;
import local.computingMedia.media.Medium;

import java.util.HashMap;

public interface VertexCanning {
    void setMedium(Medium medium);
    void can();

    HashMap<Vertex, Coord> getVertexCanning();
}
