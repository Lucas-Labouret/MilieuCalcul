package local.furthestpointoptimization.model;

import java.util.*;
import java.util.function.Function;

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
        for (int i = 0; i < count; i++) {
            this.add(new Vertex(Math.random()*count, Math.random()*count));
        }

        double minX = getMinX();
        double minY = getMinY();
        double maxX = getMaxX();
        double maxY = getMaxY();
        for (Vertex vertex : this){
            double x = vertex.getX();
            double y = vertex.getY();
            vertex.setX((x - minX)/(maxX - minX));
            vertex.setY((y - minY)/(maxY - minY));
        }
    }

    public VertexSet(VertexSet vertices) {
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

    public VertexSet(){}

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
        for (Vertex vertex : this) for (Vertex neighbor : vertex.getNeighbors()){
            ArrayList<Vertex> sortedNeighborNeighbors = new ArrayList<>(neighbor.getNeighbors());
            if (sortedNeighborNeighbors.size() < 2) continue;
            sortedNeighborNeighbors.sort((a, b) -> GeometricPrimitives.sortCCW(vertex, neighbor, a, b));
            if (sortedNeighborNeighbors.getFirst().hasNeighbors(vertex))
                triangles.add(new Triangle(vertex, neighbor, sortedNeighborNeighbors.getFirst()));
        }
        return triangles;
    }

    public double getLocalMinDist(Vertex x){
        double minDistance = Double.POSITIVE_INFINITY;
        for (Vertex y : this) if (x != y){
            double distance = Segment.length(x, y);
            if (distance < minDistance) minDistance = distance;
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

    public double getWidth(){ return width; }
    public double getHeight(){ return height; }

    public ArrayList<Vertex> getBorder(){ return border; }
}

class DelaunayUtils {
    private DelaunayUtils(){}

    private record BucketKey(int x, int y) {
        private BucketKey {
            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("Bucket index must be non-negative");
            }
        }

        public boolean equals(Object o) {
                if (o == this) return true;
                if (!(o instanceof BucketKey)) return false;
                BucketKey b = (BucketKey) o;
                return x == b.x && y == b.y;
            }

        public int hashCode() {
                return x < y ? y * y + x : x * x + x + y;
            }
        }

    public static void buildDT(VertexSet vertexSet){
    /*
        HashMap<BucketKey, VertexSet> buckets = new HashMap<>();
        double n = vertexSet.size();
        int m = (int) Math.ceil(Math.sqrt(n/Math.log(n)));

        for (Vertex v : vertexSet){
            int x = (int) v.getX() * m;
            int y = (int) v.getY() * m;
            BucketKey key = new BucketKey(x, y);
            if (!buckets.containsKey(key)){
                buckets.put(key, new VertexSet());
            }
            buckets.get(key).add(v);
        }

        for (VertexSet bucket : buckets.values()){
            bucketDT(bucket);
        }
    */
        bucketDT(vertexSet);
    }

    private static void bucketDT(VertexSet vertexSet){
        ArrayList<Vertex> vertices = new ArrayList<>(vertexSet);
        vertices.sort((a, b) -> {
            if (a.getX() < b.getX()) return -1;
            if (a.getX() > b.getX()) return 1;
            return Double.compare(a.getY(), b.getY());
        });
        _r_bucketDT(vertices, vertexSet.getBorder(), 0, vertices.size());
    }

    private static ArrayList<Integer> _r_bucketDT(ArrayList<Vertex> vertices, ArrayList<Vertex> border, int left, int right){
        if (right - left == 2){
            vertices.get(left).setId(left);
            vertices.get(left+1).setId(left+1);

            vertices.get(left).addNeighbor(vertices.get(left+1));

            return new ArrayList<>(List.of(left, left+1));
        }

        if (right - left == 3){
            Vertex v1 = vertices.get(left);
            Vertex v2 = vertices.get(left+1);
            Vertex v3 = vertices.get(left+2);

            v1.setId(left);
            v2.setId(left+1);
            v3.setId(left+2);

            v1.addNeighbor(v2);
            v2.addNeighbor(v3);
            if (GeometricPrimitives.orientation(v1, v2, v3) == 0)
                return new ArrayList<>(List.of(left, left+2));

            v1.addNeighbor(v3);
            if (GeometricPrimitives.orientation(v1, v2, v3) == 1)
                return new ArrayList<>(List.of(left, left+2, left+1));

            return new ArrayList<>(List.of(left, left+1, left+2));
        }

        int mid = (left + right) / 2;
        ArrayList<Integer> leftHull = _r_bucketDT(vertices, border, left, mid);
        ArrayList<Integer> rightHull = _r_bucketDT(vertices, border, mid, right);
        ArrayList<Integer> hull = new ArrayList<>();
        int[] tmp = mergeHull(vertices, leftHull, rightHull, hull);
        //System.out.println(left + "-" + (mid-1) + ": " + leftHull);
        //System.out.println(mid + "-" + (right-1) + ": " + rightHull);
        //System.out.println("Merged: " + hull);
        int leftLowerBound = tmp[0], rightLowerBound = tmp[1];
        mergeDT(vertices, border, leftLowerBound, rightLowerBound, left, mid, right);
        return hull;
    }

    private static int[] mergeHull(
            ArrayList<Vertex> vertices,
            ArrayList<Integer> leftHull, ArrayList<Integer> rightHull,
            ArrayList<Integer> mergedHull
    ){
        if (allAligned(vertices, leftHull, rightHull)){
            mergedHull.addAll(leftHull);
            mergedHull.addAll(rightHull);
            return new int[]{leftHull.getFirst(), rightHull.getFirst()};
        }

        int left = 0;
        for (int i = 1; i < leftHull.size(); i++)
            if (vertices.get(leftHull.get(i)).getX() > vertices.get(leftHull.get(left)).getX()) left = i;
        int right = 0;
        for (int i = 1; i < rightHull.size(); i++)
            if (vertices.get(rightHull.get(i)).getX() < vertices.get(rightHull.get(right)).getX()) right = i;

        final int leftStart = left;
        final int rightStart = right;
        boolean leftDone;
        boolean rightDone;
        do {
            int rightNext = getNextCandidate(vertices, rightHull, leftHull, right, left, -1, 2);
            rightDone = rightNext == right;
            right = rightNext;

            int leftNext = getNextCandidate(vertices, leftHull, rightHull, left, right, 1, 1);
            leftDone = leftNext == left;
            left = leftNext;
        } while (!leftDone || !rightDone);
        final int leftUpperBound = left;
        final int rightUpperBound = right;

        left = leftStart;
        right = rightStart;
        do {
            int rightNext = getNextCandidate(vertices, rightHull, leftHull, right, left, 1, 1);
            rightDone = rightNext == right;
            right = rightNext;

            int leftNext = getNextCandidate(vertices, leftHull, rightHull, left, right, -1, 2);
            leftDone = leftNext == left;
            left = leftNext;
        } while (!leftDone || !rightDone);
        final int leftLowerBound = left;
        final int rightLowerBound = right;

        int i = leftUpperBound;
        while (i != leftLowerBound){
            mergedHull.add(leftHull.get(i));
            i = Math.floorMod(i + 1, leftHull.size());
        }
        mergedHull.add(leftHull.get(leftLowerBound));
        i = rightLowerBound;
        while (i != rightUpperBound){
            mergedHull.add(rightHull.get(i));
            i = Math.floorMod(i + 1, rightHull.size());
        }
        mergedHull.add(rightHull.get(rightUpperBound));

        //System.out.println("llb: " + leftHull.get(leftLowerBound) + ", lub: " + leftHull.get(leftUpperBound) + ", rlb: " + rightHull.get(rightLowerBound) + ", rub: " + rightHull.get(rightUpperBound));

        return new int[]{leftHull.get(leftLowerBound), rightHull.get(rightLowerBound)};
    }

    private static int getNextCandidate(
            ArrayList<Vertex> vertices,
            ArrayList<Integer> hull, ArrayList<Integer> otherHull,
            int current, int reference,
            int side, int rotation
    ){
        boolean done = false;
        while (!done) {
            int next = Math.floorMod(current  + side, hull.size());
            int orientation = GeometricPrimitives.orientation(
                vertices.get(otherHull.get(reference)),
                vertices.get(hull.get(current)),
                vertices.get(hull.get(next))
            );
            if (orientation == rotation) {
                current = next;
            } else done = true;
        }
        return current;
    }

    public static void mergeDT(
            ArrayList<Vertex> vertices,
            ArrayList<Vertex> border,
            int leftLowerBound, int rightLowerBound,
            int left, int mid, int right
    ){
        int lCurrent = leftLowerBound;
        int rCurrent = rightLowerBound;
        vertices.get(lCurrent).addNeighbor(vertices.get(rCurrent));

        while (true){
            final int lTemp = lCurrent;
            final int rTemp = rCurrent;

            ArrayList<Integer> lCandidates = new ArrayList<>();
            for (int i = left; i < mid; i++) if (vertices.get(lCurrent).hasNeighbors(vertices.get(i))) lCandidates.add(i);
            lCandidates.sort((a, b) -> {
                Vertex v1 = vertices.get(lTemp);
                Vertex v2 = vertices.get(rTemp);
                Vertex va = vertices.get(a);
                Vertex vb = vertices.get(b);

                return GeometricPrimitives.sortCCW(v1, v2, vb, va);
            });

            ArrayList<Integer> rCandidates = new ArrayList<>();
            for (int i = mid; i < right; i++) if (vertices.get(rCurrent).hasNeighbors(vertices.get(i))) rCandidates.add(i);
            rCandidates.sort((a, b) -> {
                Vertex v1 = vertices.get(rTemp);
                Vertex v2 = vertices.get(lTemp);
                Vertex va = vertices.get(a);
                Vertex vb = vertices.get(b);

                return GeometricPrimitives.sortCCW(v1, v2, va, vb);
            });

            int lCand = 0;
            int rCand = 0;

            Function<Integer, Boolean> valid  =
                    (candidate) -> GeometricPrimitives.ccw(vertices.get(candidate), vertices.get(lTemp), vertices.get(rTemp));

            boolean lFlag = false;
            if ( valid.apply(lCandidates.get(lCand)) ){
                lFlag = true;
                while (GeometricPrimitives.inCircle(
                        vertices.get(lCurrent), vertices.get(lCandidates.get(lCand)), vertices.get(rCurrent),
                        vertices.get(lCandidates.get((lCand + 1) % lCandidates.size()))
                )) {
                    vertices.get(lCurrent).removeNeighbor(vertices.get(lCandidates.get(lCand)));
                    lCand = (lCand + 1) % lCandidates.size();
                }
            }

            boolean rFlag = false;
            if ( valid.apply(rCandidates.get(rCand)) ){
                rFlag = true;
                while (GeometricPrimitives.inCircle(
                        vertices.get(lCurrent), vertices.get(rCandidates.get(rCand)), vertices.get(rCurrent),
                        vertices.get(rCandidates.get((rCand + 1) % rCandidates.size()))
                )) {
                    vertices.get(rCurrent).removeNeighbor(vertices.get(rCandidates.get(rCand)));
                    rCand = (rCand + 1) % rCandidates.size();
                }
            }

            if (!lFlag && !rFlag) return;

            boolean circleCheck = GeometricPrimitives.inCircle(
                    vertices.get(lCurrent), vertices.get(lCandidates.get(lCand)), vertices.get(rCurrent),
                    vertices.get(rCandidates.get(rCand))
            );
            if (!lFlag || (rFlag && circleCheck)){
                rCurrent = rCandidates.get(rCand);
                vertices.get(rCurrent).addNeighbor(vertices.get(lCurrent));
            } else {
                lCurrent = lCandidates.get(lCand);
                vertices.get(lCurrent).addNeighbor(vertices.get(rCurrent));
            }
        }
    }

    private static boolean allAligned(ArrayList<Vertex> vertices, ArrayList<Integer> left, ArrayList<Integer> right){
        for (Integer i : left) for (Integer j : right){
            if (GeometricPrimitives.orientation(vertices.get(left.getFirst()), vertices.get(i), vertices.get(j)) != 0)
                return false;
        }
        return true;
    }
}

class FPOUtils {
    private FPOUtils(){}

    public static void buildFPO(VertexSet vertexSet, double convergenceTolerance){
        int nbIterations = 0;
        double oldFpo = 0;
        double newFpo = 0;
        do {
            double fpo = fpoIteration(vertexSet);
            oldFpo = newFpo;
            newFpo = fpo;
            nbIterations++;
            System.out.println("-------------------------------------");
            System.out.println("Iteration " + nbIterations + ", " + vertexSet.size() + " vertices left");
            System.out.println("Average min dist: " + vertexSet.getAverageMinDist() + ", max dist: " + vertexSet.getMaxDist());
            System.out.println("FPO: " + fpo + ", Progress: " + (newFpo - oldFpo));
        } while (newFpo < convergenceTolerance && newFpo - oldFpo > 1e-6);
    }

    private static double fpoIteration(VertexSet vertices){
        VertexSet vertexSet = new VertexSet(vertices.toArray(new Vertex[0]));
        for (Vertex vertex : vertexSet){
            if (vertex.isBorder()) continue;
            Vertex f = vertex;
            double rmax = vertices.getLocalMinDist(vertex);
            //System.out.println("Starting with " + vertex + " with rmax = " + rmax);
            delaunayRemove(vertices, vertex);
            HashSet<Triangle> triangles = vertices.getTriangles();
            for (Triangle triangle : triangles){
                Vertex circumcenter = triangle.getOrthocenter();
                double circumradius = triangle.getOrthocenterDist();
                //System.out.println("    Triangle: " + triangle + ", c = (" + circumcenter.getX() + ", " + circumcenter.getY() + "), r = " + circumradius);
                if (
                        circumcenter.getX() < 0 || circumcenter.getX() > 1 ||
                        circumcenter.getY() < 0 || circumcenter.getY() > 1 ||
                        vertices.contains(circumcenter) ||
                        !GeometricPrimitives.insidePolygon(circumcenter, vertices.getBorder())
                ) continue;
                if (circumradius > rmax){
                    rmax = circumradius;
                    f = circumcenter;
                }
            }
            //System.out.println("Found (" + f.getX() + ", " + f.getY() + ") with rmax = " + rmax + " /already contained: " + vertices.contains(f));
            vertex.setX(f.getX());
            vertex.setY(f.getY());
            delaunayInsert(vertices, vertex);
        }
        return vertices.getAverageMinDist()/vertices.getMaxDist();
    }

    private static void delaunayRemove(VertexSet vertices, Vertex vertex){
        for (Vertex v : vertices) v.getNeighbors().clear();
        vertices.remove(vertex);
        DelaunayUtils.buildDT(vertices);
    }
    private static void delaunayInsert(VertexSet vertices, Vertex vertex){
        vertices.add(vertex);
        for (Vertex v : vertices) v.getNeighbors().clear();
        DelaunayUtils.buildDT(vertices);
    }
}

class GeometricPrimitives {
    private GeometricPrimitives(){}

    private static double inCirclePrimitive(Point a, Point b, Point c, Point d){
        double ax = a.getX(), ay = a.getY();
        double bx = b.getX(), by = b.getY();
        double cx = c.getX(), cy = c.getY();
        double dx = d.getX(), dy = d.getY();

        double ax_ = ax - dx, ay_ = ay - dy;
        double bx_ = bx - dx, by_ = by - dy;
        double cx_ = cx - dx, cy_ = cy - dy;

        return (
                (ax_*ax_ + ay_*ay_) * (bx_*cy_-cx_*by_) -
                (bx_*bx_ + by_*by_) * (ax_*cy_-cx_*ay_) +
                (cx_*cx_ + cy_*cy_) * (ax_*by_-bx_*ay_)
        );
    }
    private static double orientationPrimitive(Point a, Point b, Point c){
        return (b.getY() - a.getY()) * (c.getX() - b.getX()) - (b.getX() - a.getX()) * (c.getY() - b.getY());
    }

    public static boolean intersect(Segment s1, Segment s2){
        return orientation(s1.getStart(), s1.getEnd(), s2.getStart()) != orientation(s1.getStart(), s1.getEnd(), s2.getEnd()) &&
               orientation(s2.getStart(), s2.getEnd(), s1.getStart()) != orientation(s2.getStart(), s2.getEnd(), s1.getEnd());
    }
    public static int orientation(Point a, Point b, Point c){
        double val = orientationPrimitive(a, b, c);

        if (val == 0) return 0;
        return (val > 0) ? 2 : 1;
    }
    public static boolean ccw(Point a, Point b, Point c){
        return orientation(a, b, c) == 2;
    }
    public static boolean inCircle(Point a, Point b, Point c, Point d){
        return inCirclePrimitive(a, b, c, d) > 0;
    }
    public static boolean insidePolygon(Point vertex, ArrayList<Vertex> polygon){
        if (polygon == null) return true;

        int intersections = 0;
        for (int i = 0; i < polygon.size(); i++){
            Point a = polygon.get(i);
            Point b = polygon.get((i+1)%polygon.size());
            if (a.getY() == b.getY()) continue;
            if (vertex.getY() < Math.min(a.getY(), b.getY())) continue;
            if (vertex.getY() >= Math.max(a.getY(), b.getY())) continue;
            double x = (vertex.getY() - a.getY()) * (b.getX() - a.getX()) / (b.getY() - a.getY()) + a.getX();
            if (x > vertex.getX()) intersections++;
        }
        return intersections % 2 == 1;
    }
    public static boolean insidePolygon(Segment s, ArrayList<Vertex> polygon){
        if (polygon == null) return true;

        if(!insidePolygon(s.getStart(), polygon) || !insidePolygon(s.getEnd(), polygon)) return false;
        for (int i = 0; i < polygon.size(); i++){
            Point a = polygon.get(i);
            Point b = polygon.get((i+1)%polygon.size());
            if (intersect(s, new Segment(a, b))) return false;
        }
        return true;
    }

    public static int sortCCW(Vertex v1, Vertex v2, Vertex va, Vertex vb){
        double angle2 = Math.atan2(v2.getY() - v1.getY(), v2.getX() - v1.getX());
        double angleA = Math.atan2(va.getY() - v1.getY(), va.getX() - v1.getX());
        double angleB = Math.atan2(vb.getY() - v1.getY(), vb.getX() - v1.getX());

        if (angle2 < 0) angle2 += 2 * Math.PI;
        if (angleA < 0) angleA += 2 * Math.PI;
        if (angleB < 0) angleB += 2 * Math.PI;

        angleA -= angle2;
        angleB -= angle2;

        if (angleA < 0) angleA += 2 * Math.PI;
        if (angleB < 0) angleB += 2 * Math.PI;

        return Double.compare(angleA, angleB);
    }
}