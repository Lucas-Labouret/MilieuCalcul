package local.furthestpointoptimization.model;

import local.furthestpointoptimization.model.miseEnBoite.Coord;
import local.furthestpointoptimization.model.optimisation.diemkeTriangulator.DiemkeInterface;
import local.furthestpointoptimization.model.optimisation.diemkeTriangulator.NotEnoughPointsException;
import local.furthestpointoptimization.model.optimisation.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

public class VertexSet extends HashSet<Vertex> implements Serializable {
    @Serial
    private static final long serialVersionUID = 9168791764777148744L;

    private double width = 1;
    private double height = 1;

    private ArrayList<Vertex> border = null;

    public static VertexSet newHexBorderedSet(int borderWidth, int borderHeight, int count){
        VertexSet vertices = new VertexSet();

        borderHeight++;

        final double largeHeight = 1/Math.tan(Math.PI/6);

        vertices.width = borderWidth;
        vertices.height = (borderHeight/2.)*largeHeight;

        vertices.addBorder(
                borderWidth, borderHeight,
                largeHeight
        );

        for (int i = 0; i < count; i++){
            Vertex v;
            do v = new Vertex(Math.random()*vertices.width, Math.random()*vertices.height);
            while (!GeometricPrimitives.insidePolygon(v, vertices.getBorder()));
            vertices.add(v);
        }
        for (Vertex vertex : vertices){
            vertex.setX(vertex.getX()/vertices.width);
            vertex.setY(vertex.getY()/vertices.width);
        }
        vertices.width = 1;
        vertices.height = vertices.height/vertices.width;

        for (Vertex vertex : vertices){
            for (Vertex other : vertices){
                if (vertex == other) continue;
                if (vertex.almostEquals(other, 1e-10)){
                    throw new RuntimeException("Two vertices are too close");
                }
            }
        }

        return vertices;
    }

    public static VertexSet fromFile(String fileName) {
        VertexSet vertexSet = null;
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            vertexSet = (VertexSet) ois.readObject();
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

    public VertexSet(int count) {
        super();
        for (int i = 0; i < count; i++) {
            double x = Math.random();//*count;
            double y = Math.random();//*count;
            this.add(new Vertex(x,y));
        }

        // double minX = getMinX(), maxX = getMaxX();
        // double minY = getMinY(), maxY = getMaxY();
        // for (Vertex vertex : this){
        //     double x = vertex.getX(), y = vertex.getY();
        //     vertex.setX((x - minX)/(maxX - minX));
        //     vertex.setY((y - minY)/(maxY - minY));
        // }
    }

    public VertexSet(Set<Vertex> sv) {
        super();
        this.addAll(sv);
    }

    static  Random rd = new Random();
    
    public static double randomEps() {
        return rd.nextDouble(1e-5);
    }

    public static VertexSet cristal(int width, int height) {
        VertexSet vs = new VertexSet();
        double w = 1/(double)(width+1);
        for (int i = 1; i<=height; ++i) {
            for (int j = 1; j<=width; ++j) {
                double x = w*(j+0.5*(1-i%2));
                double y = (i-0.5)*w*(double)(Math.sqrt(3)/(double)2);
                
                Vertex v = new Vertex((x+randomEps()), (y+randomEps()));
                vs.add(v);
            }
        }
        return vs;
    }

    public VertexSet(VertexSet vertices) {
        super();

        this.width = vertices.width;
        this.height = vertices.height;
        this.border = vertices.border;

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

    public VertexSet(){
        super();
    }

    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public ArrayList<Vertex> getBorder() { return border; }

    private void addBorder(int totalWidth, int totalHeight, double hexHeight){
        ArrayList<Vertex> border = new ArrayList<>();

        for (int i = 0; i < totalWidth; i++){
            Vertex vertex = new Vertex(i+0.5, 0, true, true, i == 0, i == totalWidth-1, false);
            if (!border.isEmpty())
                border.getLast().addNeighbor(vertex);
            border.add(vertex);
        }
        for (int i = 0; i < totalHeight-1; i++){
            Vertex vertex;
            if (i%2 == 0) vertex = new Vertex(totalWidth, (i+1)*hexHeight/2, true, false, false, true, i == totalHeight-2);
            else vertex = new Vertex(totalWidth-0.5, (i+1)*hexHeight/2, true, false, false, true, i == totalHeight-2);
            border.getLast().addNeighbor(vertex);
            border.add(vertex);
        }
        for (int i = totalWidth - (totalHeight%2 == 0 ? 1 : 0); i >= 0; i--){
            Vertex vertex;
            if (totalHeight%2 == 1) vertex = new Vertex(i, this.height, true, false, i == 0, i == totalWidth, true);
            else vertex = new Vertex(i+0.5, this.height, true, false, i == 0, i == totalWidth, true);
            border.getLast().addNeighbor(vertex);
            border.add(vertex);
        }
        for (int i = totalHeight-1; i > 0; i--){
            Vertex vertex;
            if (i%2 == 0) vertex = new Vertex(0.5, i*hexHeight/2, true, false, true, false, false);
            else vertex = new Vertex(0, i*hexHeight/2, true, false, true, false, false);
            border.getLast().addNeighbor(vertex);
            border.add(vertex);
        }
        border.getLast().addNeighbor(border.getFirst());

        this.addAll(border);
        this.border = border;
    }
    public void bandageBorderFix(){
        for (Vertex v1 : border) for (Vertex v2 : border){
            if (v1 == v2) continue;
            if (Math.abs(v1.getX() - v2.getX()) < 1e-4){
                v1.removeNeighbor(v2);
            }
        }
        border.getFirst().addNeighbor(border.getLast());

        /*for (int i = 0; i < border.size(); i+=1){
            Vertex v0 = border.get(i);
            Vertex v1 = border.get((i+1)%border.size());
            Vertex v2 = border.get((i+2)%border.size());

            System.out.println("1");

            if (!(v0.isLeftBorder() && v2.isLeftBorder()) && !(v0.isRightBorder() && v2.isRightBorder()))
                continue;

            System.out.println("2");
            if (v0.isLeftBorder() && v0.getX() < v1.getX()) continue;
            if (v0.isRightBorder() && v0.getX() > v1.getX()) continue;

            boolean hasTriangle = false;
            for (Vertex neighbor : v0.getNeighbors()){
                if (neighbor.isBorder()) continue;
                if (neighbor.getNeighbors().contains(v1)){
                    hasTriangle = true;
                    break;
                }
            }
            for (Vertex neighbor : v2.getNeighbors()){
                if (neighbor.isBorder()) continue;
                if (neighbor.getNeighbors().contains(v1)){
                    hasTriangle = true;
                    break;
                }
            }
            System.out.println(v0 + " " + v1 + " " + v2 + " " + hasTriangle);
            if (!hasTriangle) v0.addNeighbor(v2);
        }*/
    }

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
        if (this.border != null) bandageBorderFix();
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

    public HashSet<Triangle> getTrianglesParallel(){
        int nb_threads = Runtime.getRuntime().availableProcessors();
        return getTriangles(nb_threads);
    }

    /** Version parallele de getTriangles() */
    public HashSet<Triangle> getTriangles(int nb_threads) {
        try (ForkJoinPool forkJoinPool = new ForkJoinPool(nb_threads)) {
            Set<Triangle> triangles = ConcurrentHashMap.newKeySet();

            forkJoinPool.submit(() ->
                this.parallelStream().forEach(vertex ->
                    vertex.getSurroundTriangleIn(triangles)
                )
            ).get();
            // Créer un nouvel HashSet et y ajouter tous les triangles (conversion)
            HashSet<Triangle> triangleHashSet = new HashSet<>(triangles);
            return triangleHashSet;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    /** @deprecated use delaunayTriangulate, Its faster */
    @Deprecated
    public void triangulate() {
        DelaunayTriangulation triangulator = new DelaunayTriangulation(this.toArray(new Vertex[0]));
        triangulator.triangulate();
    }
    

    public double getLocalMinDist(Vertex x){
        double minDistance = Double.POSITIVE_INFINITY;
        for (Vertex y : this)
            if (x != y){
                double distance = Segment.length(x, y);
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
    public double getMaxDist(){
        return Math.sqrt(2/(Math.sqrt(3)*this.size()));
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
        private Vertex center;
        ClockWise(Vertex center) {
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