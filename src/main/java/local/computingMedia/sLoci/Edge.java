package local.computingMedia.sLoci;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Represents an edge in a medium, defined by two vertices start and end.
 * The edge is undirected, and the start and end vertices can be swapped without anything.
 * Provides methods to calculate properties such as length, center, and distance from a vertex.
 */
public class Edge {
    private final Vertex start;
    private final Vertex end;

    public Edge(Vertex start, Vertex end) {
        this.start = start;
        this.end = end;
    }

    private Vertex start() { return start; }
    private Vertex end() { return end; }

    /**
     * The start and end vertices are given in an arbitrary order, as the edge is undirected.
     * @return a set containing the extremities of the edge.
     */
    public HashSet<Vertex> getEnds() {
        HashSet<Vertex> ends = new HashSet<>();
        ends.add(start);
        ends.add(end);
        return ends;
    }

    /**
     * @return Given one end of the edge, returns the other end. If the given vertex is not an end of the edge, returns null.
     */
    public Vertex getNeighbor(Vertex vertex) {
        if (vertex.equals(start)) return end;
        if (vertex.equals(end)) return start;
        return null;
    }

    /**
     * @param t A value between 0 and 1, where 0 corresponds to the start vertex and 1 corresponds to the end vertex.
     * @param start The starting vertex of the edge.
     * @param end The ending vertex of the edge.
     * @return A new vertex that is the weighted center between start and end.
     */
    public static Vertex getWeightedCenter(double t, Vertex start, Vertex end) {
        assert 0 <= t && t <= 1;
        return new Vertex(
                (1 - t) * start.getX() + t * end.getX(),
                (1 - t) * start.getY() + t * end.getY()
        );
    }

    /** @return a new vertex corresponding to the center of the edge. */
    public Vertex getCenter() {
        return Edge.getWeightedCenter(0.5, start, end);
    }

    /**
     * Calculates the squared length of the edge between two vertices.
     * @param start The starting vertex of the edge.
     * @param end The ending vertex of the edge.
     * @return The squared length of the edge.
     */
    public static double length2(Vertex start, Vertex end) {
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        return dx * dx + dy * dy;
    }
    /**
     * Calculates the length of the edge between two vertices.
     * @param start The starting vertex of the edge.
     * @param end The ending vertex of the edge.
     * @return The length of the edge.
     */
    public static double length(Vertex start, Vertex end) {
        return Math.sqrt(length2(start, end));
    }

    /** @return The squared length of this edge. */
    public double length2() {
        return length2(start, end);
    }
    /** @return The length of this edge. */
    public double length() {
        return length(start, end);
    }

    /**
     * Calculates the distance from a vertex to the edge.
     * The distance is defined as the length of the perpendicular from the vertex to the edge.
     * @param vertex The vertex from which to calculate the distance.
     * @return The distance from the vertex to the edge.
     */
    public double distanceFrom(Vertex vertex) {
        return 2*Face.area(vertex, start, end) / length();
    }

    /**
     * Calculates the angle of the edge from the x-axis.
     * @return The angle in radians ranging from -π to π, where 0 corresponds to the positive x-axis.
     */
    public double angleFromXAxis() {
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        return Math.atan2(dy, dx);
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

    /**
     * Checks if a point is on the edge.
     * A point is considered on the edge if it lies on the line segment defined by the start and end vertices.
     * @param point The point to check.
     * @return true if the point is on the edge, false otherwise.
     */
    public boolean contains(Vertex point) {
        return Math.abs(length() - (length(start, point) + length(point, end))) < 1e-6;
    }

    /**
     * Sorts two edges in clockwise order with respect to a reference point.
     * @param ref The reference vertex.
     * @param e1 The first edge.
     * @param e2 The second edge.
     * @return A negative integer, zero, or a positive integer as e1 is placed clockwise, colinear, or counterclockwise to e2 with respect to the reference point.
     */
    public static int sortCWWithReferencePoint(Vertex ref, Edge e1, Edge e2) {
        Vertex centre1 = e1.getCenter();
        Vertex centre2 = e2.getCenter();

        double angle1 = Math.atan2(centre1.getY() - ref.getY(), centre1.getX() - ref.getX());
        double angle2 = Math.atan2(centre2.getY() - ref.getY(), centre2.getX() - ref.getX());

        if (angle1 < 0) angle1 += 2 * Math.PI;
        if (angle2 < 0) angle2 += 2 * Math.PI;

        return Double.compare(angle1, angle2);
    }

    /**
     * Sorts two edges in counterclockwise order relative to the positive x-axis.
     * @param v1 The first vertex of the first edge.
     * @param v2 The second vertex of the first edge.
     * @param va The first vertex of the second edge.
     * @param vb The second vertex of the second edge.
     * @return A negative integer, zero, or a positive integer as the first edge is placed counterclockwise, colinear, or clockwise to the second with respect to positive x-axis.
     */
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

    /**
     * Sorts two edges in counterclockwise order relative to the positive x-axis.
     * @param e1 the first edge.
     * @param e2 the second edge.
     * @return A negative integer, zero, or a positive integer as the first edge is placed counterclockwise, colinear, or clockwise to the second with respect to positive x-axis.
     */
    public static int sortCCW(Edge e1, Edge e2) {
        return sortCCW(e1.start(), e1.end(), e2.start(), e2.end());
    }

    /** Checks if two edges intersect. */
    public static boolean intersect(Edge s1, Edge s2) {
        return Vertex.orientation(s1.start(), s1.end(), s2.start()) != Vertex.orientation(s1.start(), s1.end(), s2.end()) &&
                Vertex.orientation(s2.start(), s2.end(), s1.start()) != Vertex.orientation(s2.start(), s2.end(), s1.end());
    }

    /**
     * Checks if a segment is inside a polygon or on its border.
     * A segment is considered inside the polygon if both its endpoints are inside the polygon or on its border,
     * and it does not intersect any edge of the polygon except for the edges it coincides with.
     * @param segment The segment to check.
     * @param polygon The polygon represented as a list of vertices.
     * @return true if the segment is inside the polygon or on its border, false otherwise.
     */
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
