package local.furthestpointoptimization.model.optimisation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import local.furthestpointoptimization.model.Point;
import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;
import local.furthestpointoptimization.model.optimisation.GeometricPrimitives.Orientation;

/** Lucas */
public class DelaunayUtils {
    private DelaunayUtils(){}

    private record BucketKey(int x, int y) {
        private BucketKey {
            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("Bucket index must be non-negative");
            }
        }

        @Override public boolean equals(Object o) {
                if (o == this) return true;
                if (!(o instanceof BucketKey)) return false;
                BucketKey b = (BucketKey) o;
                return x == b.x && y == b.y;
            }

            @Override  public int hashCode() {
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

    /** Premier appel */
    private static void bucketDT(VertexSet vertexSet){
        ArrayList<Vertex> vertices = new ArrayList<>(vertexSet);
        vertices.sort(new Point.CompareByXThenY());
        _r_bucketDT(vertices, 0, vertices.size());
    }

    private static ArrayList<Integer> _r_bucketDT(ArrayList<Vertex> vertices, int left, int right){
        // cas de base
        if (right - left == 2){
            vertices.get(left).addNeighbor(vertices.get(left+1));
            return new ArrayList<>(List.of(left, left+1));
        }

        // cas de base
        if (right - left == 3){
            Vertex v1 = vertices.get(left);
            Vertex v2 = vertices.get(left+1);
            Vertex v3 = vertices.get(left+2);

            v1.addNeighbor(v2);
            v2.addNeighbor(v3);

            GeometricPrimitives.Orientation sens = GeometricPrimitives.orientation(v1, v2, v3);
            switch (sens) {
                case CoLineaire :
                    return new ArrayList<>(List.of(left, left+2));
                case ClockWise : 
                    v1.addNeighbor(v3);
                    return new ArrayList<>(List.of(left, left+2, left+1));
                case CounterClockWise:
                    v1.addNeighbor(v3);
                    return new ArrayList<>(List.of(left, left+1, left+2));
            };
        }

        // recurence

        int mid = (left + right) / 2;
        ArrayList<Integer> leftHull = _r_bucketDT(vertices, left, mid);
        ArrayList<Integer> rightHull = _r_bucketDT(vertices, mid, right);
        ArrayList<Integer> hull = new ArrayList<>();
        int[] tmp = mergeHull(vertices, leftHull, rightHull, hull);
        //System.out.println(left + "-" + (mid-1) + ": " + leftHull);
        //System.out.println(mid + "-" + (right-1) + ": " + rightHull);
        //System.out.println("Merged: " + hull);
        int leftLowerBound = tmp[0], rightLowerBound = tmp[1];
        mergeDT(vertices, leftLowerBound, rightLowerBound, left, mid, right);
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
            int rightNext = getNextCandidate(vertices, rightHull, leftHull, right, left, -1, Orientation.CounterClockWise);
            rightDone = rightNext == right;
            right = rightNext;

            int leftNext = getNextCandidate(vertices, leftHull, rightHull, left, right, 1, Orientation.ClockWise);
            leftDone = leftNext == left;
            left = leftNext;
        } while (!leftDone || !rightDone);
        final int leftUpperBound = left;
        final int rightUpperBound = right;

        left = leftStart;
        right = rightStart;
        do {
            int rightNext = getNextCandidate(vertices, rightHull, leftHull, right, left, 1, Orientation.ClockWise);
            rightDone = rightNext == right;
            right = rightNext;

            int leftNext = getNextCandidate(vertices, leftHull, rightHull, left, right, -1, Orientation.CounterClockWise);
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
            int side, Orientation rotation
    ){
        boolean done = false;
        while (!done) {
            int next = Math.floorMod(current  + side, hull.size());
            Orientation orientation = GeometricPrimitives.orientation(
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
        }

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
            if (GeometricPrimitives.orientation(vertices.get(left.getFirst()), vertices.get(i), vertices.get(j)) != Orientation.CoLineaire)
                return false;
        }
        return true;
    }
}