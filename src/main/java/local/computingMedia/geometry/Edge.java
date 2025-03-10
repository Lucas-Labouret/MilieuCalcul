package local.computingMedia.geometry;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public record Edge(Vertex start, Vertex end) {
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Edge(Vertex start1, Vertex end1))) return false;
        return Objects.equals(Set.of(start, end), Set.of(start1, end1));
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
