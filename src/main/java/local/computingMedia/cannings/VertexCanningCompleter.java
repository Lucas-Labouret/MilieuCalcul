package local.computingMedia.cannings;

import local.computingMedia.cannings.Coords.sCoords.*;
import local.computingMedia.cannings.Coords.tCoords.*;
import local.computingMedia.media.Medium;
import local.computingMedia.sLoci.*;
import local.computingMedia.tLoci.*;

import java.util.HashMap;

public class VertexCanningCompleter<T extends VertexCanning> implements Canning {
    private Medium medium;
    private final T vertexCanner;

    private HashMap<Vertex, VertexCoord> vertexCanning;
    private HashMap<Edge, EdgeCoord> edgeCanning;
    private HashMap<Face, FaceCoord> faceCanning;

    private HashMap<Ef, EfCoord> efCanning;
    private HashMap<Ev, EvCoord> evCanning;
    private HashMap<Fe, FeCoord> feCanning;
    private HashMap<Fv, FvCoord> fvCanning;
    private HashMap<Ve, VeCoord> veCanning;
    private HashMap<Vf, VfCoord> vfCanning;

    public VertexCanningCompleter(T vertexCanning) {
        this.vertexCanner = vertexCanning;
    }

    @Override
    public void can() {
        vertexCanner.can();
        vertexCanning = vertexCanner.getVertexCanning();
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
        vertexCanner.setMedium(medium);
    }

    public HashMap<Vertex, VertexCoord> getVertexCanning() { return vertexCanning; }
    public HashMap<Edge, EdgeCoord> getEdgeCanning() { return edgeCanning; }
    public HashMap<Face, FaceCoord> getFaceCanning() { return faceCanning; }

    public HashMap<Ef, EfCoord> getEfCanning() { return efCanning; }
    public HashMap<Ev, EvCoord> getEvCanning() { return evCanning; }
    public HashMap<Fe, FeCoord> getFeCanning() { return feCanning; }
    public HashMap<Fv, FvCoord> getFvCanning() { return fvCanning; }
    public HashMap<Ve, VeCoord> getVeCanning() { return veCanning; }
    public HashMap<Vf, VfCoord> getVfCanning() { return vfCanning; }
}
