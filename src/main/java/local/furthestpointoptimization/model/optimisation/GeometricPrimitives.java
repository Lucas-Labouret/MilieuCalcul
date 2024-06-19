package local.furthestpointoptimization.model.optimisation;

import local.furthestpointoptimization.model.Point;
import local.furthestpointoptimization.model.Vertex;

import java.util.ArrayList;

public class GeometricPrimitives {
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

    public enum Orientation {
        CoLineaire, // 0
        ClockWise, // 1
        CounterClockWise; // 2
    }

    public static Orientation orientation(Point a, Point b, Point c){
        double val = orientationPrimitive(a, b, c);

        if (val == 0) return Orientation.CoLineaire;
        return (val > 0) ? Orientation.CounterClockWise : Orientation.ClockWise;
    }
    public static boolean ccw(Point a, Point b, Point c){
        return orientation(a, b, c) == Orientation.CounterClockWise;
    }
    public static boolean inCircle(Point a, Point b, Point c, Point d){
        return inCirclePrimitive(a, b, c, d) > 0;
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

    public static boolean intersect(Segment s1, Segment s2){
        return orientation(s1.getStart(), s1.getEnd(), s2.getStart()) != orientation(s1.getStart(), s1.getEnd(), s2.getEnd()) &&
                orientation(s2.getStart(), s2.getEnd(), s1.getStart()) != orientation(s2.getStart(), s2.getEnd(), s1.getEnd());
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
    private static boolean insidePolygonOrOnBorder(Point point, ArrayList<Vertex> polygon) {
        if (GeometricPrimitives.insidePolygon(point, polygon)) {
            return true;
        }
        for (int i = 0; i < polygon.size(); i++) {
            Point a = polygon.get(i);
            Point b = polygon.get((i + 1) % polygon.size());
            Segment edge = new Segment(a, b);
            if (edge.contains(point)) {
                return true;
            }
        }
        return false;
    }
    public static boolean insidePolygonOrOnBorder(Segment segment, ArrayList<Vertex> polygon) {
        Point start = segment.getStart();
        Point end = segment.getEnd();

        if (!insidePolygonOrOnBorder(start, polygon) || !insidePolygonOrOnBorder(end, polygon)) return false;
        boolean result = true;
        for (int i = 0; i < polygon.size(); i++) {
            Point a = polygon.get(i);
            Point b = polygon.get((i + 1) % polygon.size());
            Segment edge = new Segment(a, b);
            if (segment.equals(edge)) return true;
            if (
                GeometricPrimitives.intersect(segment, edge) &&
                !edge.contains(start) &&
                !edge.contains(end)
            ) result = false;
        }
        return result;
    }

}