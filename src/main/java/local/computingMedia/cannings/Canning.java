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

/**
 * A canning is a set of mappings from the loci of a medium to their respective coordinates.
 * These coordinates are<br>
 *     - 2D for vertices<br>
 *     - 3D for edges, faces, Ve's and Vf's<br>
 *     - 4D for Ev's, Fv's, Ef's and Fe's<br>
 * <p>
 * It also provides communication mappings between Ve's and Ev's, Vf's and Fv's, Ef's and Fe's,
 * as well as access to the locus of the underlying medium.
 * </p
 */
public interface Canning extends VertexCanning {
    /** @return the map from the edges to their coordinates */
    HashMap<Edge, EdgeCoord> getEdgeCanning();
    /** @return the map from the faces to their coordinates */
    HashMap<Face, FaceCoord> getFaceCanning();
    /** @return the map from the Ef's to their coordinates */
    HashMap<Ef, EfCoord> getEfCanning();
    /** @return the map from the Ev's to their coordinates */
    HashMap<Ev, EvCoord> getEvCanning();
    /** @return the map from the Fe's to their coordinates */
    HashMap<Fe, FeCoord> getFeCanning();
    /** @return the map from the Fv's to their coordinates */
    HashMap<Fv, FvCoord> getFvCanning();
    /** @return the map from the Ve's to their coordinates */
    HashMap<Ve, VeCoord> getVeCanning();
    /** @return the map from the Vf's to their coordinates */
    HashMap<Vf, VfCoord> getVfCanning();

    /** @return the map from Ve's to their facing Ev */
    HashMap<Ve, Ev> getVeEvCommunication();
    /** @return the map from Ev's to their facing Ve */
    HashMap<Ev, Ve> getEvVeCommunication();
    /** @return the map from Vf's to their facing Fv */
    HashMap<Vf, Fv> getVfFvCommunication();
    /** @return the map from Fv's to their facing Vf */
    HashMap<Fv, Vf> getFvVfCommunication();
    /** @return the map from Ef's to their facing Fe */
    HashMap<Ef, Fe> getEfFeCommunication();
    /** @return the map from Fe's to their facing Ef */
    HashMap<Fe, Ef> getFeEfCommunication();

    /** @return the set of vertices of the medium */
    HashSet<Vertex> getVertices();
    /** @return the set of edges of the medium */
    HashSet<Edge> getEdges();
    /** @return the set of faces of the medium */
    HashSet<Face> getFaces();

    /** @return the set of Ef's of the medium */
    HashSet<Ef> getEf();
    /** @return the set of Fe's of the medium */
    HashSet<Fe> getFe();
    /** @return the set of Ev's of the medium */
    HashSet<Ev> getEv();
    /** @return the set of Fv's of the medium */
    HashSet<Ve> getVe();
    /** @return the set of Fv's of the medium */
    HashSet<Fv> getFv();
    /** @return the set of Vf's of the medium */
    HashSet<Vf> getVf();

}
