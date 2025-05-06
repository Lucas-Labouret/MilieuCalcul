package local.computingMedia.media;

import local.computingMedia.sLoci.*;
import local.computingMedia.optimization.Delaunay;
import local.computingMedia.optimization.FPO;
import local.misc.linkedList.LinkedList;

import java.util.*;

@SuppressWarnings("serial")
public abstract class Medium extends HashSet<Vertex> {
    protected double width = 1;
    protected double height = 1;

    protected ArrayList<Vertex> hardBorder = null;
    protected LinkedList<Vertex> softBorder = null;

    public Medium(Set<Vertex> sv) {
        this.addAll(sv);
    }

    public Medium(Medium medium) {
        this.width = medium.width;
        this.height = medium.height;
        this.hardBorder = medium.hardBorder;
        this.softBorder = medium.softBorder;

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
    }

    public abstract Medium copy();

    public Medium(Vertex... vertices) {
        this.addAll(Arrays.asList(vertices));
    }

    public void setWidth(double width) { this.width = width; }
    public double getWidth() { return width; }
    public void setHeight(double height) { this.height = height; }
    public double getHeight() { return height; }

    public void setHardBorder(ArrayList<Vertex> hardBorder) { this.hardBorder = hardBorder; }
    public ArrayList<Vertex> getHardBorder() { return hardBorder; }
    public void setSoftBorder(LinkedList<Vertex> softBorder) { this.softBorder = softBorder; }
    public LinkedList<Vertex> getSoftBorder() { return softBorder; }
    public boolean partOfBorder(Vertex vertex) {
        return (hardBorder != null && hardBorder.contains(vertex)) ||
               (softBorder != null && softBorder.contains(vertex));
    }

    public boolean isInBorder(Vertex vertex) { return false; }

    public void delaunayTriangulate(){
        for (Vertex vertex : this) vertex.getNeighbors().clear();
        Delaunay.buildDT(this);
    }

    public void optimizeToConvergence(double convergenceTolerance){
        FPO.buildFPO(this, convergenceTolerance);
    }
    public void optimizeToSetIterations(int iterations){
        for (int i=0; i<iterations; i++) FPO.fpoIteration(this);
    }

    public HashSet<Edge> getEdges(){
        HashSet<Edge> edges = new HashSet<>();
        for (Vertex vertex : this) for (Vertex neighbor : vertex.getNeighbors())
            edges.add(new Edge(vertex, neighbor));
        return edges;
    }
    public HashSet<Face> getFaces(){
        HashSet<Face> faces = new HashSet<>();
        for (Vertex vertex : this){
            faces.addAll(vertex.getSurroundingFaces());
        }
        return faces;
    }

    public double getDist(Vertex x, Vertex y){
        return Edge.length(x, y);
    }
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
    public double getAverageMinDist(){
        double minDistance = 0;
        for (Vertex x : this){
            minDistance += getLocalMinDist(x);
        }
        return minDistance/this.size();
    }
    public double getGlobalMinDist(){
        double minDistance = Double.POSITIVE_INFINITY;
        for (Vertex x : this){
            double distance = getLocalMinDist(x);
            if (distance < minDistance) minDistance = distance;
        }
        return minDistance;
    }
    public double getMaxMinDist(){
        double maxDistance = 0;
        for (Vertex x : this){
            double distance = getLocalMinDist(x);
            if (distance > maxDistance) maxDistance = distance;
        }
        return maxDistance;
    }
    public double getLocalNeighborhoodMaxDist(Vertex x){
        double maxDistance = 0;
        for (Vertex y : x.getNeighbors()){
            double distance = getDist(x, y);
            if (distance > maxDistance)
                maxDistance = distance;
        }
        return maxDistance;
    }
    public double getAverageNeighborhoodMaxDist(){
        double maxDistance = 0;
        for (Vertex x : this){
            maxDistance += getLocalNeighborhoodMaxDist(x);
        }
        return maxDistance/this.size();
    }
    public double getMaxNeighborhoodMaxDist(){
        double maxDistance = 0;
        for (Vertex x : this){
            double distance = getLocalNeighborhoodMaxDist(x);
            if (distance > maxDistance) maxDistance = distance;
        }
        return maxDistance;
    }

    public double getMinX(){
        double minX = Double.POSITIVE_INFINITY;
        for (Vertex vertex : this){
            if (vertex.getX() < minX) minX = vertex.getX();
        }
        return minX;
    }
    public double getMaxX(){
        double maxX = Double.NEGATIVE_INFINITY;
        for (Vertex vertex : this){
            if (vertex.getX() > maxX) maxX = vertex.getX();
        }
        return maxX;
    }
    public double getMinY(){
        double minY = Double.POSITIVE_INFINITY;
        for (Vertex vertex : this){
            if (vertex.getY() < minY) minY = vertex.getY();
        }
        return minY;
    }
    public double getMaxY(){
        double maxY = Double.NEGATIVE_INFINITY;
        for (Vertex vertex : this){
            if (vertex.getY() > maxY) maxY = vertex.getY();
        }
        return maxY;
    }

    public int getMaxNeighborsCount() {
        int maxN = 0;
        for (Vertex vertex : this){
            int count = vertex.getNeighbors().size();
            if (count > maxN) maxN = count;
        }
        return maxN;
    }
    public int getMinNeighborsCount() {
        int minN = Integer.MAX_VALUE;
        for (Vertex vertex : this){
            int count = vertex.getNeighbors().size();
            if (count < minN) minN = count;
        }
        return minN;
    }
    public int getInsideMinNeighborsCount() {
        int minN = Integer.MAX_VALUE;
        for (Vertex vertex : this){
            if (partOfBorder(vertex)) continue;
            int count = vertex.getNeighbors().size();
            if (count < minN) minN = count;
        }
        return minN;
    }

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