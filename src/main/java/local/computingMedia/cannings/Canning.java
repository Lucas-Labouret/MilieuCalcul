package local.computingMedia.cannings;

import local.computingMedia.cannings.coords.sCoords.EdgeCoord;
import local.computingMedia.cannings.coords.sCoords.FaceCoord;
import local.computingMedia.cannings.coords.tCoords.*;
import local.computingMedia.cannings.vertexCannings.VertexCanning;
import local.computingMedia.sLoci.Edge;
import local.computingMedia.sLoci.Face;
import local.computingMedia.sLoci.Vertex;
import local.computingMedia.tLoci.*;

import java.util.HashMap;
import java.util.HashSet;

public interface Canning extends VertexCanning {
    HashMap<Edge, EdgeCoord> getEdgeCanning();
    HashMap<Face, FaceCoord> getFaceCanning();
    HashMap<Ef, EfCoord> getEfCanning();
    HashMap<Ev, EvCoord> getEvCanning();
    HashMap<Fe, FeCoord> getFeCanning();
    HashMap<Fv, FvCoord> getFvCanning();
    HashMap<Ve, VeCoord> getVeCanning();
    HashMap<Vf, VfCoord> getVfCanning();

    HashMap<Ve, Ev> getVeEvCommunication();
    HashMap<Ev, Ve> getEvVeCommunication();
    HashMap<Vf, Fv> getVfFvCommunication();
    HashMap<Fv, Vf> getFvVfCommunication();
    HashMap<Ef, Fe> getEfFeCommunication();
    HashMap<Fe, Ef> getFeEfCommunication();

    HashSet<Vertex> getVertices();
    HashSet<Edge> getEdges();
    HashSet<Face> getFaces();

    HashSet<Ef> getEf();
    HashSet<Fe> getFe();
    HashSet<Ev> getEv();
    HashSet<Ve> getVe();
    HashSet<Fv> getFv();
    HashSet<Vf> getVf();
}
