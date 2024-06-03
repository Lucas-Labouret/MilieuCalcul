package local.furthestpointoptimization.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

public class VertexSet extends HashSet<Vertex> {
    private double width = 1;
    private double height = 1;

    private ArrayList<Vertex> border = null;

    public static VertexSet newHexBorderedSet(int borderWidth, int borderHeight, int count){
        VertexSet vertices = new VertexSet();

        final double smallHeight = 0.5*Math.tan(Math.PI/6);
        final double mediumHeight = 1/Math.sqrt(2);
        final double largeHeight = mediumHeight + 2*smallHeight;

        vertices.width = borderWidth;
        if (borderHeight%2 == 0) vertices.height = (borderHeight/2)*(largeHeight+mediumHeight)+smallHeight;
        else vertices.height = (borderHeight / 2 + 1) * largeHeight + (borderHeight / 2) * mediumHeight;

        vertices.addBorder(borderWidth, borderHeight);

        for (int i = 0; i < count; i++){
            Vertex v;
            do v = new Vertex(Math.random()*vertices.width, Math.random()*vertices.height);
            while (!GeometricPrimitives.insidePolygon(v, vertices.getBorder()));
            vertices.add(v);
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

    public VertexSet(VertexSet vertices) {
        super();

        this.width = vertices.width;
        this.height = vertices.height;
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

    private void addBorder(int totalWidth, int totalHeight){
        final double smallHeight = 0.5*Math.tan(Math.PI/6);
        final double mediumHeight = 1/Math.sqrt(2);
        final double largeHeight = mediumHeight + 2*smallHeight;

        ArrayList<Vertex> border = new ArrayList<>();

        ArrayList<Vertex> top = new ArrayList<>();
        ArrayList<Vertex> bottom = new ArrayList<>();
        for (int i = 0; i < totalWidth; i++){
            Vertex topIn = new Vertex(i, smallHeight, true);
            Vertex topOut = new Vertex(i+0.5, 0, true);

            if (!top.isEmpty())
                top.getLast().addNeighbor(topIn);
            topOut.addNeighbor(topIn);

            top.add(topIn);
            top.add(topOut);
            this.add(topIn);
            this.add(topOut);
        }
        Vertex lastTopIn = new Vertex(totalWidth, smallHeight, true);
        lastTopIn.addNeighbor(top.getLast());
        top.add(lastTopIn);

        if (totalHeight%2 == 1){
            for (int i = 0; i < totalWidth; i++) {
                Vertex botIn = new Vertex(i, this.height - smallHeight, true);
                Vertex botOut = new Vertex(i + 0.5, this.height, true);

                if (!bottom.isEmpty())
                    bottom.getLast().addNeighbor(botIn);
                botOut.addNeighbor(botIn);

                bottom.add(botIn);
                bottom.add(botOut);
                this.add(botIn);
                this.add(botOut);
            }
            Vertex lastBotIn = new Vertex(totalWidth, this.height - smallHeight, true);
            lastBotIn.addNeighbor(bottom.getLast());
            bottom.add(lastBotIn);
        } else {
            for (int i = 0; i < totalWidth-1; i++) {
                Vertex botIn = new Vertex(i+0.5, this.height - smallHeight, true);
                Vertex botOut = new Vertex(i+1, this.height, true);

                if (!bottom.isEmpty())
                    bottom.getLast().addNeighbor(botIn);
                botOut.addNeighbor(botIn);

                bottom.add(botIn);
                bottom.add(botOut);
                this.add(botIn);
                this.add(botOut);
            }
            Vertex lastBotIn = new Vertex(totalWidth-0.5, this.height - smallHeight, true);
            lastBotIn.addNeighbor(bottom.getLast());
            bottom.add(lastBotIn);
        }

        ArrayList<Vertex> left = new ArrayList<>();
        ArrayList<Vertex> right = new ArrayList<>();
        for (int i = 0; i < totalHeight; i++){
            Vertex left1;
            Vertex left2;
            Vertex right1;
            Vertex right2;

            if (i%2 == 0){
                double y1 = (i/2)*(largeHeight+mediumHeight)+smallHeight;
                double y2 = (i/2)*(largeHeight+mediumHeight)+smallHeight+mediumHeight;
                left1 = new Vertex(0, y1, true);
                left2 = new Vertex(0, y2, true);
                right1 = new Vertex(totalWidth, y1, true);
                right2 = new Vertex(totalWidth, y2, true);
            } else {
                double y1 = (i / 2 + 1) * largeHeight + (i / 2) * mediumHeight;
                double y2 = (i / 2 + 1) * largeHeight + (i / 2 + 1) * mediumHeight;
                left1 = new Vertex(0.5, y1, true);
                left2 = new Vertex(0.5, y2, true);
                right1 = new Vertex(totalWidth - 0.5, y1, true);
                right2 = new Vertex(totalWidth - 0.5, y2, true);
            }

            if (!left.isEmpty())
                left.getLast().addNeighbor(left1);
            left2.addNeighbor(left1);

            if (!right.isEmpty())
                right.getLast().addNeighbor(right1);
            right2.addNeighbor(right1);

            left.add(left1);
            left.add(left2);
            right.add(right1);
            right.add(right2);
        }

        left.getFirst().removeNeighbor(left.get(1));
        left.getLast().removeNeighbor(left.get(left.size()-2));
        right.getFirst().removeNeighbor(right.get(1));
        right.getLast().removeNeighbor(right.get(right.size()-2));

        left.get(1).addNeighbor(top.getFirst());
        left.get(left.size()-2).addNeighbor(bottom.getFirst());
        right.get(1).addNeighbor(top.getLast());
        right.get(right.size()-2).addNeighbor(bottom.getLast());

        border.addAll(bottom);
        for (int i = right.size()-2; i >= 1; i--){
            border.add(right.get(i));
        }
        for (int i = top.size()-1; i >= 0; i--){
            border.add(top.get(i));
        }
        for (int i = 1; i < left.size()-1; i++){
            border.add(left.get(i));
        }

        this.addAll(border);
        this.border = border;
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
            // Créer un nouvel HashSet et y ajouter tous les triangles
            HashSet<Triangle> triangleHashSet = new HashSet<>(triangles);
            return triangleHashSet;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashSet<>();
        }
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