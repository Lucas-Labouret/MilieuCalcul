package local.furthestpointoptimization.model.optimisation;

import local.furthestpointoptimization.model.Point;
import local.furthestpointoptimization.model.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.OptionalInt;
import java.util.concurrent.*;

/** Arthur */
@Deprecated
public class DelaunayTriangulation {
    private Vertex[] vertices;
    public DelaunayTriangulation(Vertex[] vertices) {
        this.vertices = vertices;
    }

    public void triangulate() {
        // Sorting
        Arrays.sort(this.vertices, new Point.CompareByXThenY());
        // recursion
        // The Parralelisation is useless 

        DelaunayTriangulationAction action = new DelaunayTriangulationAction(this.vertices, 0, this.vertices.length-1);
        // ForkJoinPool yo = new ForkJoinPool();
        // yo.invoke(action);
        // yo.close();
        System.out.println("lancé");
        action.triangulate_rec(new SliceBound(0, this.vertices.length-1));
        // triangulate_rec(new SliceBound(0, vertices.length - 1));
    }
}

class SliceBound {
    int start, end;

    SliceBound(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int len() {
        return end - start + 1;
    }

    public int lowBound() {
        return start;
    }

    public int highBound() {
        return end;
    }

    @Override
    public String toString() {

        return lowBound() + " -- " + highBound();
    }
}


class DelaunayTriangulationAction extends RecursiveAction {
    private Vertex[] vertices;
    private int start, end;

    DelaunayTriangulationAction(Vertex[] vertices, int start, int end) {
        this.vertices = vertices;
        this.start = start;
        this.end = end;
    }


    @Override
    protected void compute() {
        // if (true) return;
        if (this.end - this.start < 100) {
            triangulate_rec(new SliceBound(start, end));
        } else {
            int pivot = (this.end + this.start) / 2;
            DelaunayTriangulationAction dleft = new DelaunayTriangulationAction(this.vertices, this.start, pivot);
            DelaunayTriangulationAction dright = new DelaunayTriangulationAction(this.vertices, pivot+1, this.end);
            SliceBound l = new SliceBound(this.start, pivot);
            SliceBound r = new SliceBound(pivot + 1, this.end);

            // invokeAll(dleft, dright);
            // System.out.println("fork "+ this.start+ "--"+pivot);
            dleft.fork();
            // System.out.println("fork "+ (pivot+1)+ "--"+end);
            // dright.fork();
            dright.compute();
            dleft.join();
            fusion(l, r);
        }
    }



    /** Inclusive bound */
    void triangulate_rec(SliceBound bound) {
        int sliceLength = bound.len();
        int leftIndex = bound.lowBound(), rightIndex = bound.highBound();
        switch (sliceLength) {
            case 1:
            case 0:
            System.exit(1);
            // base case
            case 2:
                Vertex.link(vertices[leftIndex], vertices[rightIndex]);
                break;
            // base case
            case 3:
                ArrayList<Point> vs = new ArrayList<>();
                vs.add(vertices[leftIndex]);
                vs.add(vertices[leftIndex+1]);
                vs.add(vertices[leftIndex+2]);
                Vertex.link(vertices[leftIndex], vertices[leftIndex+1]);
                Vertex.link(vertices[leftIndex+1], vertices[leftIndex+2]);

                if (!Point.areAllPointsCollinear(vs)) {
                    Vertex.link(vertices[leftIndex], vertices[leftIndex+2]);
                }
                break;
            // recursion
            default:
                int pivot = (leftIndex + rightIndex) / 2;
                SliceBound l = new SliceBound(leftIndex, pivot);
                SliceBound r = new SliceBound(pivot + 1, rightIndex);
                System.out.println(l+ " \033[91m<\033[0m|\033[91m>\033[0m "+r);
                triangulate_rec(l);
                triangulate_rec(r);
                System.out.println(l+ " \033[92m>\033[0m|\033[92m<\033[0m "+r);
                fusion(l, r);
                break;
        }
    }



    void fusion(SliceBound leftTrig, SliceBound rightTrig) {
        int[] base = get_base_for_fusion(leftTrig.start, rightTrig.end);
        
        int leftPtIdx = base[0];
        int rightPtIdx = base[1];
        Vertex.link(vertices[leftPtIdx], vertices[rightPtIdx]);
        // if (true) return;
        int i = 1;
        while (true) {

            OptionalInt leftCandidate = getCandidate(leftPtIdx, rightPtIdx, Side.LEFT);
            OptionalInt rightCandidate = getCandidate(leftPtIdx, rightPtIdx, Side.RIGHT);

            if (leftCandidate.isPresent() && rightCandidate.isPresent()) {
                Vertex basel = vertices[leftPtIdx];
                Vertex baser = vertices[rightPtIdx];

                System.out.println(" - - - ");
                System.out.println("("+leftPtIdx+","+rightPtIdx+")");
                System.out.println("("+leftCandidate.getAsInt()+","+rightCandidate.getAsInt()+")");
                
                Vertex l = vertices[leftCandidate.getAsInt()];
                Vertex r = vertices[rightCandidate.getAsInt()];
                
                // ArrayList<Vertex> arr = new ArrayList<>();
                // arr.add(basel);
                // arr.add(baser);
                // arr.add(l);
                // arr.add(r);
                // if (Point.areAllPointsCollinear()) {

                // }

                // if (leftCandidate.getAsInt() == rightCandidate.getAsInt()) {
                    // Vertex.link(basel, vertices[leftCandidate.getAsInt()]);
                    // Vertex.link(baser, vertices[leftCandidate.getAsInt()]);
                    // return;
                // }

                Triangle leftTriangle = new Triangle(basel, baser, l);
                Triangle rightTriangle = new Triangle(basel, baser, r);
                // boolean leftTest = test_in_circle(leftTriangle, (Point)r);
                // boolean leftTest = test_in_circle(basel, baser, l, r);
                boolean leftTest = leftTriangle._contains(r);

                // boolean rightTest = test_in_circle(rightTriangle, (Point)l);
                // boolean rightTest = test_in_circle(basel, baser, r, l);
                boolean rightTest = rightTriangle._contains(l);

                if (leftTest && rightTest) {
                    System.out.println("Two true");
                    // System.exit(1);
                    return;
                } else if (!leftTest && !rightTest) {
                    System.out.println("Two false");
                    // System.out.println("("+leftPtIdx+","+rightPtIdx+")");
                    // System.out.println("("+leftCandidate.getAsInt()+","+rightCandidate.getAsInt()+")");
                    leftPtIdx = leftCandidate.getAsInt();
                    rightPtIdx = rightCandidate.getAsInt();

                    // return;
                    // Vertex.link(vertices[rightPtIdx], vertices[leftCandidate.getAsInt()]);
                    // leftPtIdx = leftCandidate.getAsInt();
                    // return;

                } else if (!leftTest) {
                    Vertex.link(vertices[rightPtIdx], vertices[leftCandidate.getAsInt()]);
                    leftPtIdx = leftCandidate.getAsInt();
                } else if (!rightTest) {
                    Vertex.link(vertices[leftPtIdx], vertices[rightCandidate.getAsInt()]);
                    rightPtIdx = rightCandidate.getAsInt();
                }

            } else if (leftCandidate.isPresent()) {
                int candidID = leftCandidate.getAsInt();
                Vertex.link(vertices[rightPtIdx], vertices[candidID]);
                leftPtIdx = candidID;

            } else if (rightCandidate.isPresent()) {
                int candidID = rightCandidate.getAsInt();
                Vertex.link(vertices[leftPtIdx], vertices[candidID]);
                rightPtIdx = candidID;
            } else if (leftCandidate.isEmpty() && rightCandidate.isEmpty()) {
                break;
            }

            if (i++ == 1000) {
                System.out.println("boucle inf");
                return;}
            // sniff java :'(

            // leftCandidate.ifPresent(new IntConsumer() {
            // @Override
            // public void accept(int value) {
            // Vertex.link(vertices[rightPtIdx], vertices[value]);
            // leftPtIdx = value;
            // }
            // });

            // rightCandidate.ifPresent(new IntConsumer() {
            // @Override
            // public void accept(int value) {
            // Vertex.link(vertices[leftPtIdx], vertices[value]);
            // rightPtIdx = value;
            // }
            // });
            // return;
        }
    }

    int[] get_base_for_fusion(int left, int right) {
        int[] convex_env = midandrew(left, right);
        int[] res = new int[2];
        for (int i = 0; i < convex_env.length - 1; i++) {
            int v1 = convex_env[i];
            int v2 = convex_env[i + 1];
            if (!vertices[v1].hasNeighbors(vertices[v2])) {
                res[0] = v1; // left
                res[1] = v2; // right
                break;
            }
        }
        return res;
    }

    /** return idxes of the low hull */
    int[] midandrew(int left, int right) {
        // Vertex are already sorted
        int n = right - left + 1;
        int[] res = new int[n];
        int k = 0;
        // bottom hull
        for (int i = left; i < right + 1; ++i) {
            while (k >= 2 && vertices[res[k - 2]].cross(vertices[res[k - 1]], vertices[i]) > 0.0) {
                k--;
            }
            res[k++] = i;
        }
        return Arrays.copyOfRange(res, 0, k);
    }

    enum Side {
        LEFT, RIGHT;
    }

    OptionalInt getCandidate(int leftBase, int rightBase, Side s) {
        Vertex leftVertex = this.vertices[leftBase];
        Vertex rightVertex = this.vertices[rightBase];

        int center = -1;
        int other = -1;
        switch (s) {
            case Side.LEFT:
                center = leftBase;
                other = rightBase;
                break;
            case Side.RIGHT:
                center = rightBase;
                other = leftBase;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + s);
        }

        Point.AngleCalculator angleWithBase;
        switch (s) {
            case RIGHT:
                angleWithBase = (Point point) -> Point.angleBetweenThreePoints(leftVertex, rightVertex, point);
                break;

            case LEFT:
                angleWithBase = (Point point) -> Point.angleBetweenThreePoints(point, leftVertex, rightVertex);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + s);
        }

        ArrayList<Vertex> neighbors = new ArrayList<>();
        neighbors.addAll(this.vertices[center].getNeighbors());
        neighbors.remove(vertices[other]);

        // trier
        // neighbors.sort(new Point.CompareByAngle(angleWithBase));
        neighbors.sort(new Point.CompareByAngleDistance(vertices[center], vertices[other], s==Side.RIGHT));
        ArrayList<Integer> neighborsID = new ArrayList<>();

        // Not opti
        for (Vertex n : neighbors) {
            for (int i = 0; i < vertices.length; i++) {
                if (n == vertices[i]) {
                    neighborsID.add(i);
                }
            }
        }

        for (int i = 0; i < neighborsID.size(); i++) {
            int candidarID = neighborsID.get(i);
            Vertex candidat = vertices[candidarID];

            // first critera
            double angle = angleWithBase.calculate(candidat);
            if (angle >= 180) {
                return OptionalInt.empty();
            }

            // 2nd critera
            try {
                int next_candidateID = neighborsID.get(i + 1);
                Vertex next_candidate = vertices[next_candidateID];

                Triangle tri = new Triangle(leftVertex, rightVertex, candidat);
                // boolean is_inside_circle = test_in_circle(tri, (Point)next_candidate);
                // boolean is_inside_circle = test_in_circle(leftVertex, rightVertex, candidat, next_candidate);
                boolean is_inside_circle = tri._contains(next_candidate);
                if (is_inside_circle) {
                    Vertex.unlink(this.vertices[center], candidat);
                    continue;
                } else {
                    return OptionalInt.of(candidarID);
                }

            } catch (IndexOutOfBoundsException e) {
                return OptionalInt.of(candidarID);
            }
        }

        // unreachable
        return null;
    }

}
