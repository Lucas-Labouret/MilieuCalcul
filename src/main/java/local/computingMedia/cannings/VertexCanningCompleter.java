package local.computingMedia.cannings;

import local.computingMedia.cannings.coords.sCoords.*;
import local.computingMedia.cannings.coords.tCoords.*;
import local.computingMedia.cannings.vertexCannings.VertexCanning;
import local.computingMedia.media.Medium;
import local.computingMedia.sLoci.*;
import local.computingMedia.tLoci.*;

import java.util.*;

/**
 * Create a complete canning from a vertex canning.
 */
public class VertexCanningCompleter implements Canning {
    private Medium medium;
    private final VertexCanning vertexCanner;

    private HashMap<Vertex, VertexCoord> vertexCanning;
    private HashMap<Edge, EdgeCoord> edgeCanning;
    private HashMap<Face, FaceCoord> faceCanning;

    private HashMap<Vertex, HashSet<Edge>> vertexToManagedEdges; // Maps each vertex to the edges that belong to it
    private HashMap<Vertex, HashSet<Face>> vertexToManagedFaces; // Maps each vertex to the faces that belong to it

    private HashMap<Ef, EfCoord> efCanning;
    private HashMap<Ev, EvCoord> evCanning;
    private HashMap<Fe, FeCoord> feCanning;
    private HashMap<Fv, FvCoord> fvCanning;
    private HashMap<Ve, VeCoord> veCanning;
    private HashMap<Vf, VfCoord> vfCanning;

    private HashMap<Ef, Fe> facingEfFe;
    private HashMap<Fe, Ef> facingFeEf;
    private HashMap<Ev, Ve> facingEvVe;
    private HashMap<Ve, Ev> facingVeEv;
    private HashMap<Fv, Vf> facingFvVf;
    private HashMap<Vf, Fv> facingVfFv;

    @Override public int getWidth() { return vertexCanner.getWidth(); }
    @Override public int getHeight() { return vertexCanner.getHeight(); }
    @Override public double getDensity() { return vertexCanner.getDensity(); }

    @Override public HashMap<Vertex, VertexCoord> getVertexCanning() { return vertexCanning; }
    @Override public HashMap<Edge  , EdgeCoord  > getEdgeCanning()   { return edgeCanning;   }
    @Override public HashMap<Face  , FaceCoord  > getFaceCanning()   { return faceCanning;   }

    @Override public HashMap<Ef, EfCoord> getEfCanning() { return efCanning; }
    @Override public HashMap<Fe, FeCoord> getFeCanning() { return feCanning; }
    @Override public HashMap<Ev, EvCoord> getEvCanning() { return evCanning; }
    @Override public HashMap<Ve, VeCoord> getVeCanning() { return veCanning; }
    @Override public HashMap<Fv, FvCoord> getFvCanning() { return fvCanning; }
    @Override public HashMap<Vf, VfCoord> getVfCanning() { return vfCanning; }

    @Override public HashMap<Ef, Fe> getEfFeCommunication() { return facingEfFe; }
    @Override public HashMap<Fe, Ef> getFeEfCommunication() { return facingFeEf; }
    @Override public HashMap<Ev, Ve> getEvVeCommunication() { return facingEvVe; }
    @Override public HashMap<Ve, Ev> getVeEvCommunication() { return facingVeEv; }
    @Override public HashMap<Fv, Vf> getFvVfCommunication() { return facingFvVf; }
    @Override public HashMap<Vf, Fv> getVfFvCommunication() { return facingVfFv; }

    @Override public HashSet<Vertex> getVertices() { return new HashSet<>(vertexCanning.keySet()); }
    @Override public HashSet<Edge>   getEdges()    { return new HashSet<>(edgeCanning.keySet());   }
    @Override public HashSet<Face>   getFaces()    { return new HashSet<>(faceCanning.keySet());   }

    @Override public HashSet<Ef> getEf() { return new HashSet<>(efCanning.keySet()); }
    @Override public HashSet<Fe> getFe() { return new HashSet<>(feCanning.keySet()); }
    @Override public HashSet<Ev> getEv() { return new HashSet<>(evCanning.keySet()); }
    @Override public HashSet<Ve> getVe() { return new HashSet<>(veCanning.keySet()); }
    @Override public HashSet<Fv> getFv() { return new HashSet<>(fvCanning.keySet()); }
    @Override public HashSet<Vf> getVf() { return new HashSet<>(vfCanning.keySet()); }

    @Override public void setMedium(Medium medium) {
        this.medium = medium;
        vertexCanner.setMedium(medium);
    }


    public VertexCanningCompleter(VertexCanning vertexCanning) {
        this.vertexCanner = vertexCanning;
    }

    /**
     * Initializes the environment for the canning.
     * This method sets up the vertex canning, fills managed edges and faces, and initializes the various canning maps.
     */
    private void initEnv() {
        vertexCanner.can();
        vertexCanning = vertexCanner.getVertexCanning();

        fillManagedEdges();
        fillManagedFaces();

        edgeCanning = new HashMap<>();
        faceCanning = new HashMap<>();

        efCanning = new HashMap<>();
        evCanning = new HashMap<>();
        feCanning = new HashMap<>();
        fvCanning = new HashMap<>();
        veCanning = new HashMap<>();
        vfCanning = new HashMap<>();

        facingEfFe = new HashMap<>();
        facingFeEf = new HashMap<>();
        facingEvVe = new HashMap<>();
        facingVeEv = new HashMap<>();
        facingFvVf = new HashMap<>();
        facingVfFv = new HashMap<>();
    }

    /**
     * Fills the managed edges for each vertex in the medium.
     * <p>
     * An edge is considered managed by a vertex if it is directed from the vertex to a neighbor with a higher coordinate.<br>
     * This is equivalent to attributing to each vertex the edges that are :<br>
     * 1. Connected to it and a vertex below it<br>
     * 2. Connected to it and a neighbor at the same level, on its right.
     * </p>
     */
    private void fillManagedEdges() {
        vertexToManagedEdges = new HashMap<>();

        for (Vertex vertex: medium) {
            vertexToManagedEdges.put(vertex, new HashSet<>());
            for (Vertex neighbor: vertex.getNeighbors()) {
                if (vertexCanning.get(vertex).compareTo(vertexCanning.get(neighbor)) < 0) {
                    vertexToManagedEdges.get(vertex).add(new Edge(vertex, neighbor));
                }
            }
        }
    }
    /**
     * Fills the managed faces for each vertex in the medium.
     * <p>
     * Face attribution follows mostly the same principle as edge attribution.
     * </p>
     */
    private void fillManagedFaces() {
        vertexToManagedFaces = new HashMap<>();
        for (Vertex vertex: medium) {
            vertexToManagedFaces.put(vertex, new HashSet<>());
            for (Face face: vertex.getSurroundingFaces()) {
                ArrayList<Vertex> faceVertices = new ArrayList<>(face.getVertices());
                faceVertices.sort((v1, v2) -> vertexCanning.get(v1).compareTo(vertexCanning.get(v2)));
                Vertex minVertex = faceVertices.getFirst();

                if (minVertex.equals(vertex)) {
                    vertexToManagedFaces.get(vertex).add(face);
                }
            }
        }
    }

    @Override
    public void can() {
        initEnv();
        sCanning();
        tCanning();
    }
    /** Cans the edges and faces of the medium relative to the vertices. */
    private void sCanning(){
        for (Vertex vertex: medium) {
            VertexCoord vertexCoord = vertexCanning.get(vertex);
            HashSet<Edge> managedEdges = vertexToManagedEdges.get(vertex);
            HashSet<Face> managedFaces = vertexToManagedFaces.get(vertex);

            ArrayList<Edge> sortedEdges = new ArrayList<>(managedEdges);
            sortedEdges.sort((e1, e2) -> Edge.sortCWWithReferencePoint(vertex, e1, e2));
            int firstEdgeIndex = 0;
            for (int i = 0; i < sortedEdges.size(); i++) {
                Edge edge = sortedEdges.get(i);
                VertexCoord neighborCoord = vertexCanning.get(edge.getNeighbor(vertex));
                if (vertexCoord.Y() == neighborCoord.Y() && vertexCoord.X() < neighborCoord.X()) {
                    firstEdgeIndex = i;
                    break;
                }
            }
            for (int _i = 0; _i < sortedEdges.size(); _i++) {
                int i = (firstEdgeIndex + _i) % sortedEdges.size();
                Edge edge = sortedEdges.get(i);
                edgeCanning.put(edge, new EdgeCoord(_i, vertexCoord));
            }

            ArrayList<Face> sortedFaces = new ArrayList<>(managedFaces);
            sortedFaces.sort((f1, f2) -> Face.sortCWWithReferencePoint(vertex, f1, f2));
            int firstFaceIndex = 0;
            for (int i = 0; i < sortedFaces.size(); i++) {
                for (Vertex faceVertex: sortedFaces.get(i).getVertices()) {
                    VertexCoord neighborCoord = vertexCanning.get(faceVertex);
                    if (vertexCoord.Y() == neighborCoord.Y() && vertexCoord.X() < neighborCoord.X()) {
                        firstFaceIndex = i;
                        break;
                    }
                }
            }
            for (int _i = 0; _i < sortedFaces.size(); _i++) {
                int i = (firstFaceIndex + _i) % sortedFaces.size();
                Face face = sortedFaces.get(i);
                faceCanning.put(face, new FaceCoord(_i, vertexCoord));
            }
        }
    }
    /** Cans the transfer loci of the medium relative to their primary sLoci */
    private void tCanning(){
        for (Vertex vertex: medium) {
            VertexCoord vertexCoord = vertexCanning.get(vertex);

            ArrayList<Vertex> neighbors = new ArrayList<>(vertex.getNeighbors());
            neighbors.sort((n1, n2) -> Edge.sortCWWithReferencePoint(vertex, new Edge(vertex, n1), new Edge(vertex, n2)));
            int firstNeighborIndex = 0;
            for (int i = 0; i < neighbors.size(); i++) {
                Vertex neighbor = neighbors.get(i);
                EdgeCoord edgeCoord = edgeCanning.get(new Edge(vertex, neighbor));
                if (vertexCanning.get(vertex).equals(edgeCoord.vertex())) {
                    firstNeighborIndex = i;
                    break;
                }
            }

            for(int _i = 0; _i < neighbors.size(); _i++) {
                int i = (firstNeighborIndex + _i) % neighbors.size();
                int j = (firstNeighborIndex + _i + 1) % neighbors.size();

                Vertex neighbor1 = neighbors.get(i);
                Vertex neighbor2 = neighbors.get(j);

                Edge edge = new Edge(vertex, neighbor1);
                int sideEdge = 0;
                for (Edge candidate: vertexToManagedEdges.get(vertex))    if (candidate.equals(edge)) edge = candidate;
                for (Edge candidate: vertexToManagedEdges.get(neighbor1)) if (candidate.equals(edge)) {
                    sideEdge = 1;
                    edge = candidate;
                }
                EdgeCoord edgeCoord = edgeCanning.get(edge);

                Ve ve = new Ve(vertex, edge);
                VeCoord veCoord = new VeCoord(_i, vertexCoord);
                veCanning.put(ve, veCoord);

                Ev ev = new Ev(edge, vertex);
                EvCoord evCoord = new EvCoord(sideEdge, edgeCoord);
                evCanning.put(ev, evCoord);

                facingEvVe.put(ev, ve);
                facingVeEv.put(ve, ev);

                Face oldFace = new Face(vertex, neighbor1, neighbor2);
                Face face = null;
                int sideFace = 0;
                for (Face candidate: vertexToManagedFaces.get(vertex))    if (candidate.equals(oldFace)) face = candidate;
                for (Face candidate: vertexToManagedFaces.get(neighbor1)) if (candidate.equals(oldFace)) {
                    sideFace = 2;
                    face = candidate;
                }
                for (Face candidate: vertexToManagedFaces.get(neighbor2)) if (candidate.equals(oldFace)) {
                    sideFace = 1;
                    face = candidate;
                }
                if (face == null) {
                    continue;
                }
                FaceCoord faceCoord = faceCanning.get(face);

                Vf vf = new Vf(vertex, face);
                VfCoord vfCoord = new VfCoord(_i, vertexCoord);
                vfCanning.put(vf, vfCoord);

                Fv fv = new Fv(face, vertex);
                FvCoord fvCoord = new FvCoord(sideFace, faceCoord);
                fvCanning.put(fv, fvCoord);

                facingFvVf.put(fv, vf);
                facingVfFv.put(vf, fv);

                Fe fe = new Fe(face, edge);
                FeCoord feCoord = new FeCoord(sideFace, faceCoord);
                feCanning.put(fe, feCoord);

                Ef ef = new Ef(edge, face);
                EfCoord efCoord = new EfCoord(sideEdge, edgeCoord);
                efCanning.put(ef, efCoord);

                facingEfFe.put(ef, fe);
                facingFeEf.put(fe, ef);
            }
        }
    }

    public String toString(){
        int i=0;
        for (Vertex vertex: medium) {
            vertex.setId(String.valueOf(i++));
        }
        return "Ef canning : " + efCanning + "\n" +
               "Ev canning : " + evCanning + "\n" +
               "Fe canning : " + feCanning + "\n" +
               "Fv canning : " + fvCanning + "\n" +
               "Ve canning : " + veCanning + "\n" +
               "Vf canning : " + vfCanning + "\n";
    }
}
