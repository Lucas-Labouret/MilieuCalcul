package local.computingMedia.optimization;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import local.computingMedia.media.Medium;
import local.computingMedia.sLoci.Edge;
import local.computingMedia.sLoci.Face;
import local.computingMedia.Orientation;
import local.computingMedia.sLoci.Vertex;

/**
 * Set of utilities to build the Delaunay triangulation of a medium.
 * This implementation uses the divide-and-conquer algorithm presented <a href="https://doi.org/10.1007/BF01840356">here</a>.
 */
public class Delaunay {
    private Delaunay(){}

    private record BucketKey(int x, int y) {
        private BucketKey {
            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("Bucket index must be non-negative");
            }
        }

        @Override public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof BucketKey(int x1, int y1))) return false;
            return x == x1 && y == y1;
        }

        @Override  public int hashCode() {
            return x < y ? y * y + x : x * x + x + y;
        }
    }

    /**
     * Builds the Delaunay triangulation of the given medium.
     * The medium must contain at least 3 vertices.
     * <p>
     * Dwyer's optimized the algorithm by partitioning the vertices into buckets based on their coordinates,
     * and then merging the buckets together. This optimization is not implemented in this version.
     * </p>
     *
     * @param medium the medium to build the triangulation for
     * @throws IllegalArgumentException if the medium contains less than 3 vertices
     */
    public static void buildDT(Medium medium){
        //TODO: implement Dwyer's optimization
        if (medium.size() <= 2) throw new IllegalArgumentException("Medium must contain at least 3 vertices");
    /*
        HashMap<BucketKey, Medium> buckets = new HashMap<>();
        double n = medium.size();
        int m = (int) Math.ceil(Math.sqrt(n/Math.log(n)));

        for (Vertex v : medium){
            int x = (int) v.getX() * m;
            int y = (int) v.getY() * m;
            BucketKey key = new BucketKey(x, y);
            if (!buckets.containsKey(key)){
                buckets.put(key, new Medium());
            }
            buckets.get(key).add(v);
        }

        for (Medium bucket : buckets.values()){
            bucketDT(bucket);
        }
    */
        bucketDT(medium);
    }

    /**
     * Builds the Delaunay triangulation of each bucket.
     */
    private static void bucketDT(Medium medium){
        ArrayList<Vertex> vertices = new ArrayList<>(medium);
        vertices.sort(new Vertex.CompareByXThenY());
        _r_bucketDT(vertices, 0, vertices.size());
    }

    /**
     * Recursive method to build the Delaunay triangulation of the vertices in the given range.
     * This method assumes that the vertices are sorted by their x-coordinate.
     *
     * @param vertices the list of vertices to triangulate
     * @param left the left index (inclusive)
     * @param right the right index (exclusive)
     * @return a list of indices representing the convex hull of the triangulation of the vertices in the given range, sorted counter-clockwise
     */
    private static ArrayList<Integer> _r_bucketDT(ArrayList<Vertex> vertices, int left, int right){
        // Base case / 2 vertices
        // We just connect the two vertices together
        if (right - left == 2){
            vertices.get(left).addNeighbor(vertices.get(left+1));
            return new ArrayList<>(List.of(left, left+1));
        }

        // Base case / 3 vertices
        // We connect the three vertices together, and return the indices in counter-clockwise order to form the convex hull
        if (right - left == 3){
            Vertex v1 = vertices.get(left);
            Vertex v2 = vertices.get(left+1);
            Vertex v3 = vertices.get(left+2);

            v1.addNeighbor(v2);
            v2.addNeighbor(v3);

            Orientation sens = Vertex.orientation(v1, v2, v3);
            switch (sens) {
                case Collinear -> { return new ArrayList<>(List.of(left, left+2)); }
                case Clockwise -> {
                    v1.addNeighbor(v3);
                    return new ArrayList<>(List.of(left, left+2, left+1));
                }
                case CounterClockwise -> {
                    v1.addNeighbor(v3);
                    return new ArrayList<>(List.of(left, left+1, left+2));
                }
            }
        }

        // Recursive case

        // We split the vertices in two halves, and recursively build the triangulation for each half
        int mid = (left + right) / 2;
        ArrayList<Integer> leftHull = _r_bucketDT(vertices, left, mid);
        ArrayList<Integer> rightHull = _r_bucketDT(vertices, mid, right);

        // We merge the two halves together to form the triangulation
        ArrayList<Integer> hull = new ArrayList<>();
        int[] tmp = mergeHull(vertices, leftHull, rightHull, hull);
        int leftLowerBound = tmp[0], rightLowerBound = tmp[1];
        mergeDT(vertices, leftLowerBound, rightLowerBound, left, mid, right);
        return hull;
    }


    /**
     * Merges the two convex hulls together to form the convex hull of the triangulation
     * using the merge algorithm presented <a href="https://dl.acm.org/doi/pdf/10.1145/282918.282923">here</a>.
     * The two hulls must be sorted counter-clockwise.
     *
     * @param vertices (in) the list of vertices to triangulate
     * @param leftHull (in) the left convex hull
     * @param rightHull (in) the right convex hull
     * @param mergedHull (inout) the list to store the merged convex hull
     * @return the indices of the vertices that form the lower edge connecting the left hull to the right.
     */
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

        // Find the rightmost vertex of the left hull and the leftmost vertex of the right hull
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

        // Find the top edge connecting the left hull to the right hull by having the edge (left, right) "climb up" the left and right hulls
        do {
            int rightNext = getNextCandidate(vertices, rightHull, leftHull, right, left, -1, Orientation.CounterClockwise);
            rightDone = rightNext == right;
            right = rightNext;

            int leftNext = getNextCandidate(vertices, leftHull, rightHull, left, right, 1, Orientation.Clockwise);
            leftDone = leftNext == left;
            left = leftNext;
        } while (!leftDone || !rightDone);
        final int leftUpperBound = left;
        final int rightUpperBound = right;

        left = leftStart;
        right = rightStart;
        // Same process, but now we "climb down" the left and right hulls to find the lower edge connecting the two hulls
        do {
            int rightNext = getNextCandidate(vertices, rightHull, leftHull, right, left, 1, Orientation.Clockwise);
            rightDone = rightNext == right;
            right = rightNext;

            int leftNext = getNextCandidate(vertices, leftHull, rightHull, left, right, -1, Orientation.CounterClockwise);
            leftDone = leftNext == left;
            left = leftNext;
        } while (!leftDone || !rightDone);
        final int leftLowerBound = left;
        final int rightLowerBound = right;

        // Populate the merged hull with the vertices of the left hull from the upper bound to the lower bound, and the vertices of the right hull from the lower bound to the upper bound
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

        // Return the indices of the vertices that form the lower edge connecting the left hull to the right hull
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
            Orientation orientation = Vertex.orientation(
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

                return Edge.sortCCW(v1, v2, vb, va);
            });

            ArrayList<Integer> rCandidates = new ArrayList<>();
            for (int i = mid; i < right; i++) if (vertices.get(rCurrent).hasNeighbors(vertices.get(i))) rCandidates.add(i);
            rCandidates.sort((a, b) -> {
                Vertex v1 = vertices.get(rTemp);
                Vertex v2 = vertices.get(lTemp);
                Vertex va = vertices.get(a);
                Vertex vb = vertices.get(b);

                return Edge.sortCCW(v1, v2, va, vb);
            });

            int lCand = 0;
            int rCand = 0;

            Function<Integer, Boolean> valid  =
                    (candidate) -> Vertex.ccw(vertices.get(candidate), vertices.get(lTemp), vertices.get(rTemp));

            boolean lFlag = false;
            if ( valid.apply(lCandidates.get(lCand)) ){
                lFlag = true;
                while (Face.inCircumscribedCircle(
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
                while (Face.inCircumscribedCircle(
                        vertices.get(lCurrent), vertices.get(rCandidates.get(rCand)), vertices.get(rCurrent),
                        vertices.get(rCandidates.get((rCand + 1) % rCandidates.size()))
                )) {
                    vertices.get(rCurrent).removeNeighbor(vertices.get(rCandidates.get(rCand)));
                    rCand = (rCand + 1) % rCandidates.size();
                }
            }

            if (!lFlag && !rFlag) return;

            boolean circleCheck = Face.inCircumscribedCircle(
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


    /**
     * Checks if all the vertices in the left and right lists are collinear.
     * This is used to optimize the merging of the two hulls.
     *
     * @param vertices the list of vertices to check
     * @param left the indices of the left hull
     * @param right the indices of the right hull
     * @return true if all the vertices in the left and right hulls are collinear, false otherwise
     */
    private static boolean allAligned(ArrayList<Vertex> vertices, ArrayList<Integer> left, ArrayList<Integer> right){
        ArrayList<Integer> candidates = new ArrayList<>();
        candidates.addAll(left);
        candidates.addAll(right);
        for (int i = 1; i < candidates.size()-1; i++) for (int j = i+1; j < candidates.size(); j++){
            if (Vertex.orientation(
                    vertices.get(candidates.getFirst()),
                    vertices.get(i),
                    vertices.get(j)
            ) != Orientation.Collinear)
                return false;
        }
        return true;
    }
}