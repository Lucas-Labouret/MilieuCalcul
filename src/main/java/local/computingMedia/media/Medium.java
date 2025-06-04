package local.computingMedia.media;

import local.computingMedia.sLoci.*;
import local.computingMedia.optimization.Delaunay;
import local.computingMedia.optimization.FPO;
import local.misc.linkedList.LinkedList;

import java.util.*;

/**
 * A computing medium is a set of processing elements distributed in some space which are locally connected.<br>
 * This class offers basic functionalities to build and manipulate Delaunay-triangulated media. It is the base class for all other media types
 */
@SuppressWarnings("serial")
public abstract class Medium extends HashSet<Vertex> {
    protected double width = 1;
    protected double height = 1;

    protected ArrayList<Vertex> hardBorder = null;
    protected LinkedList<Vertex> softBorder = null;

    /** Builds a new medium from a set of vertices. It has no border by default, and a width and height of 1. */
    public Medium(Set<Vertex> sv) {
        this.addAll(sv);
    }

    /** Builds a new medium containing the given vertices. */
    public Medium(Vertex... vertices) {
        this.addAll(Arrays.asList(vertices));
    }

    /**
     * Copy constructor for a medium.
     * It produces a deep copy of the medium, meaning that all vertices are cloned.
     */
    public Medium(Medium medium) {
        this.width = medium.width;
        this.height = medium.height;
        this.hardBorder = new ArrayList<>(medium.hardBorder.size());
        this.softBorder = new LinkedList<>();

        HashMap<Vertex, Vertex> originalToClone = new HashMap<>();
        for (Vertex vertex : medium){
            Vertex clone = new Vertex(vertex);
            originalToClone.put(vertex, clone);
            this.add(clone);
        }

        for (Vertex vertex : medium){
            Vertex clone = originalToClone.get(vertex);
            for (Vertex neighbor : vertex.getNeighbors()){
                clone.addNeighbor(originalToClone.get(neighbor));
            }
        }

        for (Vertex vertex : medium.hardBorder) {
            this.hardBorder.add(originalToClone.get(vertex));
        }

        LinkedList<Vertex> tmp = new LinkedList<>();
        for (Vertex vertex : medium.softBorder) {
            Vertex clone = originalToClone.get(vertex);
            tmp.addFirst(clone);
        }
        for (Vertex vertex : tmp) {
            this.softBorder.addFirst(vertex);
        }
    }

    /** Creates a shallow copy of the medium. */
    public abstract Medium copy();

    public Medium(double width, double height, Vertex... vertices) {
        this(vertices);
        this.width = width;
        this.height = height;
    }

    public void setWidth(double width) { this.width = width; }
    public double getWidth() { return width; }
    public void setHeight(double height) { this.height = height; }
    public double getHeight() { return height; }

    public void setHardBorder(ArrayList<Vertex> hardBorder) { this.hardBorder = hardBorder; }
    public ArrayList<Vertex> getHardBorder() { return hardBorder; }
    public void setSoftBorder(LinkedList<Vertex> softBorder) { this.softBorder = softBorder; }
    public LinkedList<Vertex> getSoftBorder() { return softBorder; }

    /** Checks if the given vertex is part of the border of the medium if it exists. */
    public boolean partOfBorder(Vertex vertex) {
        return (hardBorder != null && hardBorder.contains(vertex)) ||
               (softBorder != null && softBorder.contains(vertex));
    }

    /**
     * Checks if the vertex falls inside the medium.
     * This method is used to determine if a vertex is allowed to be added to the medium.
     */
    public abstract boolean isInBorder(Vertex vertex);

    /**
     * Builds the Delaunay triangulation of the medium from its vertices.
     */
    public void delaunayTriangulate(){
        for (Vertex vertex : this) vertex.getNeighbors().clear();
        Delaunay.buildDT(this);
    }

    /**
     * Optimizes the medium using the Farthest Point Optimization (FPO) algorithm.
     * The optimization will continue until the medium is homogeneous enough, or until no further improvements can be made.
     */
    public void optimizeToConvergence(double convergenceTolerance){
        FPO.buildFPO(this, convergenceTolerance);
    }
    /**
     * Optimizes the medium using the Farthest Point Optimization (FPO) algorithm for a fixed number of iterations.
     */
    public void optimizeToSetIterations(int iterations){
        for (int i=0; i<iterations; i++) FPO.fpoIteration(this);
    }

    /** @return the set of edges in the medium, where an edge is defined as a pair of vertices that are neighbors. */
    public HashSet<Edge> getEdges(){
        HashSet<Edge> edges = new HashSet<>();
        for (Vertex vertex : this) for (Vertex neighbor : vertex.getNeighbors())
            edges.add(new Edge(vertex, neighbor));
        return edges;
    }
    /** @return the set of faces in the medium, where a face is defined as a triangle formed by three vertices that neighbors of each other. */
    public HashSet<Face> getFaces(){
        HashSet<Face> faces = new HashSet<>();
        for (Vertex vertex : this){
            faces.addAll(vertex.getSurroundingFaces());
        }
        return faces;
    }

    /** @return the distance between x and y. */
    public double getDist(Vertex x, Vertex y){
        return Edge.length(x, y);
    }
    /** @return the distance between x and its closest neighbor, or Double.POSITIVE_INFINITY if x has no neighbor. */
    public double getLocalMinDist(Vertex x){
        double minDistance = Double.POSITIVE_INFINITY;
        for (Vertex y : this)
            if (x != y){
                double distance = getDist(x, y);
                if (distance < minDistance)
                    minDistance = distance;
            }
        return minDistance;
    }
    /** @return the average of the local minimum distances of all vertices in the medium. */
    public double getAverageMinDist(){
        double minDistance = 0;
        for (Vertex x : this){
            minDistance += getLocalMinDist(x);
        }
        return minDistance/this.size();
    }
    /** @return the minimum of the local minimum distances of all vertices in the medium. */
    public double getGlobalMinDist(){
        double minDistance = Double.POSITIVE_INFINITY;
        for (Vertex x : this){
            double distance = getLocalMinDist(x);
            if (distance < minDistance) minDistance = distance;
        }
        return minDistance;
    }
    /** @return the maximum of the local minimum distances of all vertices in the medium. */
    public double getMaxMinDist(){
        double maxDistance = 0;
        for (Vertex x : this){
            double distance = getLocalMinDist(x);
            if (distance > maxDistance) maxDistance = distance;
        }
        return maxDistance;
    }
    /** @return the distance between x and its furthest neighbor, or 0 if x has no neighbor. */
    public double getLocalNeighborhoodMaxDist(Vertex x){
        double maxDistance = 0;
        for (Vertex y : x.getNeighbors()){
            double distance = getDist(x, y);
            if (distance > maxDistance)
                maxDistance = distance;
        }
        return maxDistance;
    }
    /** @return the average of the local maximum distances of all vertices in the medium. */
    public double getAverageNeighborhoodMaxDist(){
        double maxDistance = 0;
        for (Vertex x : this){
            maxDistance += getLocalNeighborhoodMaxDist(x);
        }
        return maxDistance/this.size();
    }
    /** @return the minimum of the local maximum distances of all vertices in the medium. */
    public double getMaxNeighborhoodMaxDist(){
        double maxDistance = 0;
        for (Vertex x : this){
            double distance = getLocalNeighborhoodMaxDist(x);
            if (distance > maxDistance) maxDistance = distance;
        }
        return maxDistance;
    }

    /** @return the smallest X coordinate of all vertices in the medium. */
    public double getMinX(){
        double minX = Double.POSITIVE_INFINITY;
        for (Vertex vertex : this){
            if (vertex.getX() < minX) minX = vertex.getX();
        }
        return minX;
    }
    /** @return the largest X coordinate of all vertices in the medium. */
    public double getMaxX(){
        double maxX = Double.NEGATIVE_INFINITY;
        for (Vertex vertex : this){
            if (vertex.getX() > maxX) maxX = vertex.getX();
        }
        return maxX;
    }
    /** @return the smallest Y coordinate of all vertices in the medium. */
    public double getMinY(){
        double minY = Double.POSITIVE_INFINITY;
        for (Vertex vertex : this){
            if (vertex.getY() < minY) minY = vertex.getY();
        }
        return minY;
    }
    /** @return the largest Y coordinate of all vertices in the medium. */
    public double getMaxY(){
        double maxY = Double.NEGATIVE_INFINITY;
        for (Vertex vertex : this){
            if (vertex.getY() > maxY) maxY = vertex.getY();
        }
        return maxY;
    }

    /** @return the maximum number of neighbors of any vertex in the medium. */
    public int getMaxNeighborsCount() {
        int maxN = 0;
        for (Vertex vertex : this){
            int count = vertex.getNeighbors().size();
            if (count > maxN) maxN = count;
        }
        return maxN;
    }
    /** @return the minimum number of neighbors of any vertex in the medium. */
    public int getMinNeighborsCount() {
        int minN = Integer.MAX_VALUE;
        for (Vertex vertex : this){
            int count = vertex.getNeighbors().size();
            if (count < minN) minN = count;
        }
        return minN;
    }
    /** @return the maximum number of neighbors of any vertex that is not part of the border of the medium. */
    public int getInsideMinNeighborsCount() {
        int minN = Integer.MAX_VALUE;
        for (Vertex vertex : this){
            if (partOfBorder(vertex)) continue;
            int count = vertex.getNeighbors().size();
            if (count < minN) minN = count;
        }
        return minN;
    }

    /**
     * Comparator that sorts vertices in a clockwise order around a given center vertex.
     */
    public static class ClockWise implements Comparator<Vertex> {
        private final Vertex center;
        public ClockWise(Vertex center) {
            this.center = center;
        }

        @Override
        public int compare(Vertex v1, Vertex v2) {
            double det = (v1.getX() - center.getX()) * (v2.getY() - center.getY()) - (v2.getX() - center.getX()) * (v1.getY() - center.getY());
            if (det < 0.0)
                return -1;
            else if (det > 0.0)
                return 1;
            else {
                return Double.compare(v1.getX(), v2.getX());
            }
        }
    }
}