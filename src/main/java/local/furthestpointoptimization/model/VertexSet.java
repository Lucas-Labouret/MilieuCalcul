package local.furthestpointoptimization.model;

import java.util.*;
import java.util.function.Function;

public class VertexSet extends HashSet<Vertex> {
    public VertexSet(int count) {
        for (int i = 0; i < count; i++) {
            this.add(new Vertex(Math.random(), Math.random()));
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

    public void delaunayTriangulate(){
        for (Vertex vertex : this) vertex.getNeighbors().clear();
        DelaunayUtils.buildDT(this);
    }

    public void optimize(double convergenceTolerance){
        FPOUtils.buildFPO(this, convergenceTolerance);
    }

    public void addBorder(){
        int borderSize = (int) Math.ceil(Math.sqrt(this.size())) - 1;

        double minX = getMinX(), minY = getMinY();
        double maxX = getMaxX(), maxY = getMaxY();

        for (Vertex vertex : this){
            double x = vertex.getX();
            double y = vertex.getY();

            vertex.setX((x - minX)*(1-2*getMaxDist())/(maxX - minX) + getMaxDist());
            vertex.setY((y - minY)*(1-2*getMaxDist())/(maxY - minY) + getMaxDist());
        }

        this.add(new Vertex(0, 0));
        this.add(new Vertex(0, 1));
        this.add(new Vertex(1, 0));
        this.add(new Vertex(1, 1));

        for (int i = 1; i < borderSize; i++){
            this.add(new Vertex(0, i/(double)borderSize));
            this.add(new Vertex(1, i/(double)borderSize));
            this.add(new Vertex(i/(double)borderSize, 0));
            this.add(new Vertex(i/(double)borderSize, 1));
        }

        this.add(new Vertex(getMaxDist()/2, getMaxDist()/2));
        this.add(new Vertex(1-getMaxDist()/2, getMaxDist()/2));
        this.add(new Vertex(getMaxDist()/2, 1-getMaxDist()/2));
        this.add(new Vertex(1-getMaxDist()/2, 1-getMaxDist()/2));

        for (int i = 1; i < borderSize - 1; i++){
            this.add(new Vertex(getMaxDist()/2, (i+0.5)/(double)borderSize));
            this.add(new Vertex(1-getMaxDist()/2, (i+0.5)/(double)borderSize));
            this.add(new Vertex((i+0.5)/(double)borderSize, getMaxDist()/2));
            this.add(new Vertex((i+0.5)/(double)borderSize, 1-getMaxDist()/2));
        }

        delaunayTriangulate();
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
        ArrayList<Integer> hull = _r_bucketDT(vertices, 0, vertices.size());
        //makeToroid(vertices, hull, vertexSet.getWidth(), vertexSet.getHeight());
    }

    private static ArrayList<Integer> _r_bucketDT(ArrayList<Vertex> vertices, int left, int right){
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
                return new ArrayList<>(List.of(left, left+1, left+2));

            v1.addNeighbor(v3);
            if (GeometricPrimitives.orientation(v1, v2, v3) == 1)
                return new ArrayList<>(List.of(left, left+2, left+1));

            return new ArrayList<>(List.of(left, left+1, left+2));
        }

        int mid = (left + right) / 2;
        ArrayList<Integer> leftHull = _r_bucketDT(vertices, left, mid);
        ArrayList<Integer> rightHull = _r_bucketDT(vertices, mid, right);
        ArrayList<Integer> hull = new ArrayList<>();
        int[] tmp = bucketDTMergeHull(vertices, leftHull, rightHull, hull);
        int leftLowerBound = tmp[0], rightLowerBound = tmp[1];
        bucketDTMerge(vertices, leftLowerBound, rightLowerBound, left, mid, right);
        return hull;
    }

    private static int[] bucketDTMergeHull(
            ArrayList<Vertex> vertices,
            ArrayList<Integer> leftHull, ArrayList<Integer> rightHull,
            ArrayList<Integer> mergedHull
    ){
        if (allAligned(vertices, leftHull, rightHull)){
            mergedHull.addAll(leftHull);
            mergedHull.addAll(rightHull);
            return new int[]{leftHull.getFirst(), rightHull.getFirst()};
        }

        int p = 0;
        for (int i = 1; i < leftHull.size(); i++)
            if (vertices.get(leftHull.get(i)).getX() > vertices.get(leftHull.get(p)).getX()) p = i;
        int q = 0;
        for (int i = 1; i < rightHull.size(); i++)
            if (vertices.get(rightHull.get(i)).getX() < vertices.get(rightHull.get(q)).getX() ) q = i;

        final int p0 = p;
        final int q0 = q;
        boolean pDone = false;
        boolean qDone = false;
        do {
            while (!qDone) {
                int q1 = Math.floorMod(q - 1, rightHull.size());
                if (GeometricPrimitives.orientation(
                        vertices.get(leftHull.get(p)),
                        vertices.get(rightHull.get(q)),
                        vertices.get(rightHull.get(q1))
                ) == 2) {
                    q = q1;
                    pDone = false;
                } else qDone = true;
            }
            while (!pDone) {
                int p1 = Math.floorMod(p + 1, leftHull.size());
                if (GeometricPrimitives.orientation(
                        vertices.get(rightHull.get(q)),
                        vertices.get(leftHull.get(p)),
                        vertices.get(leftHull.get(p1))
                ) == 1) {
                    p = p1;
                    qDone = false;
                } else pDone = true;
            }
        } while (!pDone || !qDone);
        final int leftUpperBound = p;
        final int rightUpperBound = q;

        p = p0;
        q = q0;
        pDone = false;
        qDone = false;
        do {
            while (!qDone) {
                int q1 = Math.floorMod(q + 1, rightHull.size());
                if (GeometricPrimitives.orientation(
                        vertices.get(leftHull.get(p)),
                        vertices.get(rightHull.get(q)),
                        vertices.get(rightHull.get(q1))
                ) == 1) {
                    q = q1;
                    pDone = false;
                } else qDone = true;
            }
            while (!pDone) {
                int p1 = Math.floorMod(p - 1, leftHull.size());
                if (GeometricPrimitives.orientation(
                        vertices.get(rightHull.get(q)),
                        vertices.get(leftHull.get(p)),
                        vertices.get(leftHull.get(p1))
                ) == 2) {
                    p = p1;
                    qDone = false;
                } else pDone = true;
            }
        } while (!pDone || !qDone);
        final int leftLowerBound = p;
        final int rightLowerBound = q;

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

        return new int[]{leftHull.get(leftLowerBound), rightHull.get(rightLowerBound)};
    }

    private static void bucketDTMerge(
            ArrayList<Vertex> vertices,
            int leftLowerBound, int rightLowerBound,
            int left, int mid, int right
    ){
        ArrayList<Integer> leftArray = new ArrayList<>();
        ArrayList<Integer> rightArray = new ArrayList<>();
        for (int i = left; i < mid; i++) leftArray.add(i);
        for (int i = mid; i < right; i++) rightArray.add(i);
        if (allAligned(vertices, leftArray, rightArray)) {
            vertices.get(mid-1).addNeighbor(vertices.get(mid));
            return;
        };

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
            System.out.println("Iteration " + nbIterations);
            System.out.println("Average min dist: " + vertexSet.getAverageMinDist() + ", max dist: " + vertexSet.getMaxDist());
            System.out.println("FPO: " + fpo + ", Progress: " + (newFpo - oldFpo));
        } while (newFpo < convergenceTolerance && newFpo - oldFpo > 1e-6);
    }

    private static double fpoIteration(VertexSet vertices){
        VertexSet vertexSet = new VertexSet(vertices.toArray(new Vertex[0]));
        for (Vertex vertex : vertexSet){
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
                        vertices.contains(circumcenter)
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

    private static void retriangulate(VertexSet originals){
        VertexSet clones = new VertexSet();
        HashMap<Vertex, Vertex> cloneToOriginals = new HashMap<>();
        for (Vertex original : originals){
            Vertex clone = new Vertex(original.getX(), original.getY());
            clones.add(clone);
            cloneToOriginals.put(clone, original);
        }
        System.out.println("Retriangulating " + originals);
        DelaunayUtils.buildDT(clones);
        for (Vertex clone : clones){
            Vertex original = cloneToOriginals.get(clone);
            for (Vertex neighbor : clone.getNeighbors()){
                original.addNeighbor(cloneToOriginals.get(neighbor));
            }
        }
    }
    private static void delaunayRemove(VertexSet vertices, Vertex vertex){
        /*VertexSet originals = vertex.getNeighbors();
        vertices.remove(vertex);
        retriangulate(originals);*/
        for (Vertex v : vertices) v.getNeighbors().clear();
        vertices.remove(vertex);
        DelaunayUtils.buildDT(vertices);
    }
    private static void delaunayInsert(VertexSet vertices, Vertex vertex){
/*
        vertices.add(vertex);
        VertexSet originals = new VertexSet();
        originals.add(vertex);
        for (Triangle triangle : getTriangles(vertices)) {
            Vertex a = triangle.getA();
            Vertex b = triangle.getB();
            Vertex c = triangle.getC();

            if (GeometricPrimitives.orientation(a, b, c) == 0) continue;
            if (GeometricPrimitives.orientation(a, b, c) == 2 && !GeometricPrimitives.inCircle(a, b, c, vertex)) continue;
            if (GeometricPrimitives.orientation(a, b, c) == 1 && !GeometricPrimitives.inCircle(a, c, b, vertex)) continue;

            a.removeNeighbor(b);
            b.removeNeighbor(c);
            c.removeNeighbor(a);

            originals.add(a);
            originals.add(b);
            originals.add(c);
        }
        if (originals.size() == 1) retriangulate(vertices);
        else retriangulate(originals);
 */
        vertices.add(vertex);
        for (Vertex v : vertices) v.getNeighbors().clear();
        DelaunayUtils.buildDT(vertices);
    }
}

class GeometricPrimitives {
    private GeometricPrimitives(){}

    private static double inCirclePrimitive(Vertex a, Vertex b, Vertex c, Vertex d){
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
    private static double orientationPrimitive(Vertex a, Vertex b, Vertex c){
        return (b.getY() - a.getY()) * (c.getX() - b.getX()) - (b.getX() - a.getX()) * (c.getY() - b.getY());
    }

    public static int orientation(Vertex a, Vertex b, Vertex c){
        double val = orientationPrimitive(a, b, c);

        if (Math.abs(val) < 1e-6) return 0;
        return (val > 0) ? 2 : 1;
    }
    public static boolean ccw(Vertex a, Vertex b, Vertex c){
        return orientation(a, b, c) == 2;
    }
    public static boolean inCircle(Vertex a, Vertex b, Vertex c, Vertex d){
        return inCirclePrimitive(a, b, c, d) > 1e-6;
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