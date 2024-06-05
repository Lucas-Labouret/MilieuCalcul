package local.furthestpointoptimization.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

public class VertexSet extends HashSet<Vertex> {
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

    public static VertexSet cristal(int width, int height) {
        VertexSet vs = new VertexSet();
        double w = 1/(double)(width+1);
        System.out.println(w);
        for (int i = 1; i<=height; ++i) {
            for (int j = 1; j<=width; ++j) {
                double x = w*(j+0.5*(1-i%2));
                double y = (i-0.5)*w*(double)(Math.sqrt(3)/(double)2);
                Vertex v = new Vertex((x), (y));
                vs.add(v);
            }
        }
        return vs;
    }

    public VertexSet(VertexSet vertices) {
        super();
        HashMap<Vertex, Vertex> originalToClone = new HashMap<>();
        for (Vertex vertex : vertices){
            Vertex clone = new Vertex(vertex.getX(), vertex.getY());
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

    public void delaunayTriangulate(){
        for (Vertex vertex : this) vertex.getNeighbors().clear();
        DelaunayUtils.buildDT(this);
    }

    public void optimize(double convergenceTolerance){
        FPOUtils.buildFPO(this, convergenceTolerance);
    }

    public void addBorder(){
        int borderSize = (int) Math.floor(Math.sqrt(this.size()));

        double minX = getMinX(), minY = getMinY();
        double maxX = getMaxX(), maxY = getMaxY();

        for (Vertex vertex : this){
            double x = vertex.getX();
            double y = vertex.getY();

            vertex.setX((x - minX)*(1-2/(double)borderSize)/(maxX - minX) + 1/(double)borderSize);
            vertex.setY((y - minY)*(1-2/(double)borderSize)/(maxY - minY) + 1/(double)borderSize);
        }

        ArrayList<Vertex> innerTop = new ArrayList<>();
        ArrayList<Vertex> innerLeft = new ArrayList<>();
        ArrayList<Vertex> innerRight = new ArrayList<>();
        ArrayList<Vertex> innerBot = new ArrayList<>();

        Vertex innerTopLeft = new Vertex(0.5/(double)borderSize, 0.5/(double)borderSize);
        Vertex innerTopRight = new Vertex(1-0.5/(double)borderSize, 0.5/(double)borderSize);
        Vertex innerBotLeft = new Vertex(0.5/(double)borderSize, 1-0.5/(double)borderSize);
        Vertex innerBotRight = new Vertex(1-0.5/(double)borderSize, 1-0.5/(double)borderSize);

        innerTop.add(innerTopLeft);
        innerLeft.add(innerTopLeft);
        innerRight.add(innerTopRight);
        innerBot.addFirst(innerBotLeft);

        for (int i = 1; i <= borderSize - 2; i++){
            innerTop.add(i, new Vertex((i+0.5)/(double)borderSize, 0.5/(double)borderSize));
            this.add(innerTop.get(i));

            innerBot.add(i, new Vertex((i+0.5)/(double)borderSize, 1-0.5/(double)borderSize));
            this.add(innerBot.get(i));
        }

        delaunayTriangulate();

        ArrayList<Vertex> rightVertices = new ArrayList<>(this);
        rightVertices.sort((a, b) -> {
            if (a.getX() < b.getX()) return -1;
            if (a.getX() > b.getX()) return 1;
            return Double.compare(a.getY(), b.getY());
        });

        this.add(innerTopLeft);
        this.add(innerTopRight);
        this.add(innerBotLeft);
        this.add(innerBotRight);

        for (int i = 1; i <= borderSize - 2; i++){
            innerLeft.add(new Vertex(0.5/(double)borderSize, (i+0.5)/(double)borderSize));
            this.add(innerLeft.get(i));
            innerLeft.get(i).addNeighbor(innerLeft.get(i-1));
            innerLeft.get(i).setId(i);

            innerRight.add(new Vertex(1-0.5/(double)borderSize, (i+0.5)/(double)borderSize));
            this.add(innerRight.get(i));
            innerRight.get(i).addNeighbor(innerRight.get(i-1));
        }

        innerTop.add(innerTopRight);
        innerLeft.add(innerBotLeft);
        innerBot.add(innerBotRight);
        innerRight.add(innerBotRight);

        innerLeft.getLast().addNeighbor(innerLeft.get(innerLeft.size()-2));
        innerRight.getLast().addNeighbor(innerRight.get(innerRight.size()-2));

        ArrayList<Vertex> vertices;

        vertices = new ArrayList<>(innerLeft);
        vertices.addAll(rightVertices);

        int rightLowerBound = 0;
        for (int i = 0; i < vertices.size(); i++){
            if (vertices.get(i).equals(innerBot.get(1))){
                rightLowerBound = i;
                break;
            }
        }
        DelaunayUtils.mergeDT(vertices, borderSize-1, rightLowerBound, 0, innerLeft.size(), vertices.size());

        vertices = new ArrayList<>(this);
        vertices.sort((a, b) -> {
            if (a.getX() < b.getX()) return -1;
            if (a.getX() > b.getX()) return 1;
            return Double.compare(a.getY(), b.getY());
        });
        vertices.addAll(innerRight);

        int leftLowerBound = 0;
        for (int i = 0; i < vertices.size(); i++){
            if (vertices.get(i).equals(innerBot.get(innerBot.size()-2))){
                leftLowerBound = i;
                break;
            }
        }
        System.out.println(vertices.get(vertices.size()-borderSize+1));
        DelaunayUtils.mergeDT(vertices, leftLowerBound, vertices.size()-1, 0, vertices.size()-innerRight.size(), vertices.size());

        ArrayList<Vertex> outerTop = new ArrayList<>(borderSize);
        ArrayList<Vertex> outerBot = new ArrayList<>(borderSize);
        ArrayList<Vertex> outerLeft = new ArrayList<>(borderSize);
        ArrayList<Vertex> outerRight = new ArrayList<>(borderSize);

        Vertex outerTopLeft = new Vertex(0, 0);
        outerTop.addFirst(outerTopLeft);
        outerLeft.addFirst(outerTopLeft);
        this.add(outerTopLeft);

        Vertex outerTopRight = new Vertex(1, 0);
        outerTop.addLast(outerTopRight);
        outerRight.addFirst(outerTopRight);
        this.add(outerTopRight);

        Vertex outerBotLeft = new Vertex(0, 1);
        outerBot.addFirst(outerBotLeft);
        outerLeft.addLast(outerBotLeft);
        this.add(outerBotLeft);

        Vertex outerBotRight = new Vertex(1, 1);
        outerBot.addLast(outerBotRight);
        outerRight.addLast(outerBotRight);
        this.add(outerBotRight);

        for (int i = 1; i <= borderSize-1; i++){
            outerTop.add(i, new Vertex(i/(double)borderSize, 0));
            this.add(outerTop.get(i));

            outerLeft.add(i, new Vertex(0, i/(double)borderSize));
            this.add(outerLeft.get(i));

            outerRight.add(i, new Vertex(1, i/(double)borderSize));
            this.add(outerRight.get(i));

            outerBot.add(i, new Vertex(i/(double)borderSize, 1));
            this.add(outerBot.get(i));
        }

        for (int i = 0; i < borderSize; i++){
            outerTop.get(i).addNeighbor(outerTop.get(i+1));
            outerBot.get(i).addNeighbor(outerBot.get(i+1));
            outerLeft.get(i).addNeighbor(outerLeft.get(i+1));
            outerRight.get(i).addNeighbor(outerRight.get(i+1));
        }

        for (int i = 0; i <= borderSize-1; i++){
            innerTop.get(i).addNeighbor(outerTop.get(i));
            innerTop.get(i).addNeighbor(outerTop.get(i+1));

            innerLeft.get(i).addNeighbor(outerLeft.get(i));
            innerLeft.get(i).addNeighbor(outerLeft.get(i+1));

            innerRight.get(i).addNeighbor(outerRight.get(i));
            innerRight.get(i).addNeighbor(outerRight.get(i+1));

            innerBot.get(i).addNeighbor(outerBot.get(i));
            innerBot.get(i).addNeighbor(outerBot.get(i+1));
        }
    }

    /** Donne tout les triangles (version unithread) */
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