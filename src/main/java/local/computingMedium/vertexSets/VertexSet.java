package local.computingMedium.vertexSets;

import local.misc.Point;
import local.computingMedium.Vertex;
import local.furthestpointoptimization.DelaunayUtils;
import local.furthestpointoptimization.FPOUtils;
import local.misc.LinkedList;
import local.misc.Segment;
import local.misc.Triangle;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

public class VertexSet extends HashSet<Vertex> {
    @Serial private static final long serialVersionUID = -7599751605461214830L;

    static Random rd = new Random();
    public static double randomEps() {
        return rd.nextDouble(1e-5);
    }

    protected double width = 1;
    protected double height = 1;

    protected ArrayList<Vertex> hardBorder = null;
    protected LinkedList<Vertex> softBorder = null;

    public static VertexSet fromFile(String fileName) {
        VertexSet vertexSet;
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            vertexSet = (VertexSet) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        vertexSet.delaunayTriangulate();

        return vertexSet;
    }
    public static void toFile(VertexSet vertexSet, String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(vertexSet);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
            Vertex clone = new Vertex(vertex.getX(), vertex.getY(), vertex.isBorder());
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

    public double getWidth() { return width; }
    public double getHeight() { return height; }

    protected ArrayList<Vertex> getHardBorder() { return hardBorder; }
    protected LinkedList<Vertex> getSoftBorder() { return softBorder; }
    public boolean partOfBorder(Vertex vertex) {
        if (hardBorder != null && hardBorder.contains(vertex)) return true;
        if (softBorder != null && softBorder.contains(vertex)) return true;
        return false;
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

    public HashSet<Triangle> getTriangles(){
        HashSet<Triangle> triangles = new HashSet<>();
        for (Vertex vertex : this){
            vertex.getSurroundTriangleIn(triangles);
        }
        return triangles;
    }

    public double getDist(Vertex x, Vertex y){
        return Segment.length(x, y);
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
}