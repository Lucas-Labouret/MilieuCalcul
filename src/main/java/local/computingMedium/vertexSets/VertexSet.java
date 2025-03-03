package local.computingMedium.vertexSets;

import local.computingMedium.Face;
import local.misc.Point;
import local.computingMedium.Vertex;
import local.furthestpointoptimization.DelaunayUtils;
import local.furthestpointoptimization.FPOUtils;
import local.misc.LinkedList;
import local.misc.GenericSegment;

import java.io.IOException;
import java.io.Serial;
import java.util.*;

@SuppressWarnings("serial")
public class VertexSet extends HashSet<Vertex> {
    static Random rd = new Random();
    public static double randomEps() {
        return rd.nextDouble(1e-5);
    }

    protected double width = 1;
    protected double height = 1;

    protected ArrayList<Vertex> hardBorder = null;
    protected LinkedList<Vertex> softBorder = null;

    public VertexSet(Set<Vertex> sv) {
        this.addAll(sv);
    }

    public VertexSet(VertexSet vertices) {
        this.width = vertices.width;
        this.height = vertices.height;
        this.hardBorder = vertices.hardBorder;
        this.softBorder = vertices.softBorder;

        HashMap<Vertex, Vertex> originalToClone = new HashMap<>();
        for (Vertex vertex : vertices){
            Vertex clone = new Vertex(vertex);
            originalToClone.put(vertex, clone);
            this.add(clone);
        }
        for (Vertex vertex : vertices){
            Vertex clone = originalToClone.get(vertex);
            for (Vertex neighbor : vertex.getNeighbors()){
                clone.addNeighbor(originalToClone.get(neighbor));
            }
        }
    }

    public VertexSet(Vertex... vertices) {
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

    public Vertex getVertex(Point p) {
        for (Vertex v : this) {
            if (Double.compare(v.getX(),p.getX())==0 && Double.compare(v.getY(),p.getY())==0) {
                return v;
            }
        }
        return null;
    }

    public void delaunayTriangulate(){
        for (Vertex vertex : this) vertex.getNeighbors().clear();
        DelaunayUtils.buildDT(this);
    }

    public void optimize(double convergenceTolerance){
        FPOUtils.buildFPO(this, convergenceTolerance);
    }

    public HashSet<Face> getFaces(){
        HashSet<Face> faces = new HashSet<>();
        for (Vertex vertex : this){
            vertex.getSurroundingFacesIn(faces);
        }
        return faces;
    }

    public double getDist(Vertex x, Vertex y){
        return GenericSegment.length(x, y);
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

    @Serial
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        throw new IOException("Use a SavefileManager instead");
    }
}