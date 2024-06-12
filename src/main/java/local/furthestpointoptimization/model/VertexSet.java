package local.furthestpointoptimization.model;

import local.furthestpointoptimization.model.diemkeTriangulator.DiemkeInterface;
import local.furthestpointoptimization.model.diemkeTriangulator.NotEnoughPointsException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.random.RandomGenerator;

public class VertexSet extends HashSet<Vertex> {
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
        return rd.nextDouble(1e-3);
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

    public static HashMap<Vertex, Coord> cristalHM(int width, int height) {
        HashMap<Vertex, Coord> vs = new HashMap<>();
        double w = 1/(double)(width+1);
        for (int i = 1; i<=height; ++i) {
            for (int j = 1; j<=width; ++j) {
                double x = w*(j+0.5*(1-i%2));
                double y = (i-0.5)*w*(double)(Math.sqrt(3)/(double)2);
                
                Vertex v = new Vertex((x+randomEps()), (y+randomEps()));
                vs.put(v, new Coord(i, j));
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

    private void addBorder(
            int totalWidth, int totalHeight,
            double hexHeight
    ){
        ArrayList<Vertex> border = new ArrayList<>();

        for (int i = 0; i < totalWidth; i++){
            Vertex vertex = new Vertex(i+0.5, 0, true);
            if (!border.isEmpty())
                border.getLast().addNeighbor(vertex);
            border.add(vertex);
        }
        for (int i = 0; i < totalHeight-1; i++){
            Vertex vertex;
            if (i%2 == 0) vertex = new Vertex(totalWidth, (i+1)*hexHeight/2, true);
            else vertex = new Vertex(totalWidth-0.5, (i+1)*hexHeight/2, true);
            border.getLast().addNeighbor(vertex);
            border.add(vertex);
        }
        for (int i = totalWidth - (totalHeight%2 == 0 ? 1 : 0); i >= 0; i--){
            Vertex vertex;
            if (totalHeight%2 == 1) vertex = new Vertex(i, this.height, true);
            else vertex = new Vertex(i+0.5, this.height, true);
            border.getLast().addNeighbor(vertex);
            border.add(vertex);
        }
        for (int i = totalHeight-1; i > 0; i--){
            Vertex vertex;
            if (i%2 == 0) vertex = new Vertex(0.5, i*hexHeight/2, true);
            else vertex = new Vertex(0, i*hexHeight/2, true);
            border.getLast().addNeighbor(vertex);
            border.add(vertex);
        }
        border.getLast().addNeighbor(border.getFirst());

        this.addAll(border);
        this.border = border;
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
        // DelaunayUtils.buildDT(this);

        try {
            triangles.clear();
            DiemkeInterface.triangulate(this);
        } catch (NotEnoughPointsException e) {
            e.printStackTrace();
        }
    }

    public void optimize(double convergenceTolerance){
        FPOUtils.buildFPO(this, convergenceTolerance);
    }

    public HashSet<Triangle> triangles = new HashSet<>();
    public HashSet<Triangle> getTriangles(){
       triangles.clear();
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