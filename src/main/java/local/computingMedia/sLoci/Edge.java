package local.computingMedia.sLoci;

import java.util.ArrayList;
import java.util.HashSet;

public class Edge {
    private final Vertex start;
    private final Vertex end;

    public Edge(Vertex start, Vertex end) {
        this.start = start;
        this.end = end;
    }

    private Vertex start() { return start; }
    private Vertex end() { return end; }

    public HashSet<Vertex> getEnds() {
        HashSet<Vertex> ends = new HashSet<>();
        ends.add(start);
        ends.add(end);
        return ends;
    }

    public Vertex getNeighbor(Vertex vertex) {
        if (vertex.equals(start)) return end;
        if (vertex.equals(end)) return start;
        return null;
    }

    public static Vertex getWeightedCenter(double t, Vertex start, Vertex end) {
        assert 0 <= t && t <= 1;
        return new Vertex(
                (1 - t) * start.getX() + t * end.getX(),
                (1 - t) * start.getY() + t * end.getY()
        );
    }

    public Vertex getCenter() {
        return Edge.getWeightedCenter(0.5, start, end);
    }

    public static double length2(Vertex start, Vertex end) {
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        return dx * dx + dy * dy;
    }
    public static double length(Vertex start, Vertex end) {
        return Math.sqrt(length2(start, end));
    }

    public double length2() {
        return length2(start, end);
    }
    public double length() {
        return length(start, end);
    }

    public double distanceFrom(Vertex vertex) {
        return 2*Face.area(vertex, start, end) / length();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Edge edge)) return false;
        return getEnds().equals(edge.getEnds());
    }

    @Override
    public int hashCode() {
        return start.hashCode() + end.hashCode();
    }

    @Override
    public String toString() {
        return '[' + start.toString() + ',' + end.toString() + ']';
    }

    public boolean contains(Vertex point) {
        return Math.abs(length() - (length(start, point) + length(point, end))) < 1e-6;
    }

    public static int sortCWWithReferencePoint(Vertex ref, Edge e1, Edge e2) {
        Vertex centre1 = e1.getCenter();
        Vertex centre2 = e2.getCenter();

        double angle1 = Math.atan2(centre1.getY() - ref.getY(), centre1.getX() - ref.getX());
        double angle2 = Math.atan2(centre2.getY() - ref.getY(), centre2.getX() - ref.getX());

        if (angle1 < 0) angle1 += 2 * Math.PI;
        if (angle2 < 0) angle2 += 2 * Math.PI;

        return Double.compare(angle1, angle2);
    }

    public static int sortCCW(Vertex v1, Vertex v2, Vertex va, Vertex vb) {
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

    public static int sortCCW(Edge e1, Edge e2) {
        return sortCCW(e1.start(), e1.end(), e2.start(), e2.end());
    }

    public static boolean intersect(Edge s1, Edge s2) {
        return Vertex.orientation(s1.start(), s1.end(), s2.start()) != Vertex.orientation(s1.start(), s1.end(), s2.end()) &&
                Vertex.orientation(s2.start(), s2.end(), s1.start()) != Vertex.orientation(s2.start(), s2.end(), s1.end());
    }

    public static boolean insidePolygonOrOnBorder(Edge segment, ArrayList<Vertex> polygon) {
        Vertex start = segment.start();
        Vertex end = segment.end();

        if (!Vertex.insidePolygonOrOnBorder(start, polygon) || !Vertex.insidePolygonOrOnBorder(end, polygon))
            return false;
        boolean result = true;
        for (int i = 0; i < polygon.size(); i++) {
            Vertex a = polygon.get(i);
            Vertex b = polygon.get((i + 1) % polygon.size());
            Edge edge = new Edge(a, b);
            if (segment.equals(edge)) return true;
            if (
                    Edge.intersect(segment, edge) &&
                            !edge.contains(start) &&
                            !edge.contains(end)
            ) result = false;
        }
        return result;
    }
}
