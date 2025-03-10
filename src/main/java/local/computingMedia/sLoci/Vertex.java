package local.computingMedia.sLoci;

import local.misc.Orientation;
import local.computingMedia.media.Medium;

import java.util.*;

public class Vertex {
    private double x;
    private double y;

    static final Random rd = new Random();
    public static double randomEps() {
        return rd.nextDouble(1e-5);
    }

    private final HashSet<Vertex> neighbors = new HashSet<>();
    String id = "";

    private final boolean isBorder;
    private final boolean isTopBorder;
    private final boolean isLeftBorder;
    private final boolean isRightBorder;
    private final boolean isBottomBorder;


    public Vertex(double x, double y) {
        this(x, y, false, false, false, false, false);
    }

    public Vertex(double x, double y, boolean isBorder) {
        this(x, y, isBorder, false, false, false, false);
    }

    public Vertex(
            double x, double y,
            boolean isBorder,
            boolean isTopBorder, boolean isLeftBorder, boolean isRightBorder, boolean isBottomBorder
    ) {
        this.x = x + randomEps();
        this.y = y + randomEps();
        this.isBorder = isBorder;
        this.isTopBorder = isTopBorder;
        this.isLeftBorder = isLeftBorder;
        this.isRightBorder = isRightBorder;
        this.isBottomBorder = isBottomBorder;
    }

    public Vertex(Vertex vertex) {
        this(
                vertex.getX(), vertex.getY(),
                vertex.isBorder(),
                vertex.isTopBorder(), vertex.isLeftBorder(), vertex.isRightBorder(), vertex.isBottomBorder()
        );
        this.id = vertex.id;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    public void setId(String id) { this.id = id; }
    public String getId() { return id; }

    public boolean isBorder() { return isBorder; }
    public boolean isTopBorder() { return isTopBorder; }
    public boolean isLeftBorder() { return isLeftBorder; }
    public boolean isRightBorder() { return isRightBorder; }
    public boolean isBottomBorder() { return isBottomBorder; }

    public boolean hasNeighbors(Vertex v) { return neighbors.contains(v); }
    public void addNeighbor(Vertex v) { if (neighbors.add(v)) v.addNeighbor(this); }
    public void removeNeighbor(Vertex v) { if (neighbors.remove(v)) v.removeNeighbor(this); }

    public HashSet<Vertex> getNeighbors() { return neighbors; }

    public HashSet<Face> getSurroundingFaces() {
        HashSet<Face> t = new HashSet<>();
        getSurroundingFacesIn(t);
        return t;
    }

    public void getSurroundingFacesIn(Set<Face> faces) {
        ArrayList<Vertex> sortedNeighbors = new ArrayList<>(this.getNeighbors());
        sortedNeighbors.sort(new Medium.ClockWise(this));
        int nbNeighbours = sortedNeighbors.size();
        for (int i = 0; i < nbNeighbours; i++) {
            Vertex neighbor1 = sortedNeighbors.get(i % nbNeighbours);
            Vertex neighbor2 = sortedNeighbors.get((i + 1) % nbNeighbours);
            if (neighbor1.hasNeighbors(neighbor2)) {
                faces.add(new Face(this, neighbor1, neighbor2));
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof Vertex vertex) {
            return Double.compare(vertex.x, x) == 0 && Double.compare(vertex.y, y) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        Long _x = Double.doubleToLongBits(x);
        Long _y = Double.doubleToLongBits(y);
        return (int) ((_x < _y ? _y * _y + _x : _x * _x + _x + _y)%Integer.MAX_VALUE);
    }

    @Override
    public String toString() {
        return id;
    }

    public double distanceFrom(Vertex other) {
        return Math.sqrt((this.x - other.x)*(this.x - other.x) + (this.y - other.y)*(this.y - other.y));
    }

    public static double cross(Vertex center, Vertex a, Vertex b) {
        return (a.x-center.x)*(b.y-center.y) - (a.y-center.y)*(b.x-center.x);
    }

    public double cross(Vertex a, Vertex b) {
        return Vertex.cross(this, a, b);
    }

    public static Orientation orientation(Vertex a, Vertex b, Vertex c) {
        double rawOrientation = (b.getY() - a.getY()) * (c.getX() - b.getX()) - (b.getX() - a.getX()) * (c.getY() - b.getY());

        if (rawOrientation == 0) return Orientation.Collinear;
        else if (rawOrientation > 0) return Orientation.CounterClockwise;
        else return Orientation.Clockwise;
    }

    public static boolean allCollinear(List<Vertex> vertices) {
        if (vertices.size() < 3) {
            return true;
        }

        Vertex p1 = vertices.get(0);
        Vertex p2 = vertices.get(1);

        for (int i = 2; i < vertices.size(); i++) {
            Vertex p3 = vertices.get(i);
            if (orientation(p1, p2, p3) != Orientation.Collinear) {
                return false;
            }
        }

        return true;
    }

    public static boolean ccw(Vertex a, Vertex b, Vertex c){
        return orientation(a, b, c) == Orientation.CounterClockwise;
    }

    public static boolean insidePolygon(Vertex vertex, ArrayList<Vertex> polygon){
        if (polygon == null) return true;

        int intersections = 0;
        for (int i = 0; i < polygon.size(); i++){
            Vertex a = polygon.get(i);
            Vertex b = polygon.get((i+1)%polygon.size());
            if (a.getY() == b.getY()) continue;
            if (vertex.getY() < Math.min(a.getY(), b.getY())) continue;
            if (vertex.getY() >= Math.max(a.getY(), b.getY())) continue;
            double x = (vertex.getY() - a.getY()) * (b.getX() - a.getX()) / (b.getY() - a.getY()) + a.getX();
            if (x > vertex.getX()) intersections++;
        }
        return intersections % 2 == 1;
    }

    public static boolean insidePolygonOrOnBorder(Vertex vertex, ArrayList<Vertex> polygon) {
        if (insidePolygon(vertex, polygon)) {
            return true;
        }
        for (int i = 0; i < polygon.size(); i++) {
            Vertex a = polygon.get(i);
            Vertex b = polygon.get((i + 1) % polygon.size());
            Edge edge = new Edge(a, b);
            if (edge.contains(vertex)) {
                return true;
            }
        }
        return false;
    }

    public static double getAngle(Vertex a, Vertex b, Vertex c) {
        double angle = Math.atan2(c.getY() - b.getY(), c.getX() - b.getX()) - Math.atan2(a.getY() - b.getY(), a.getX() - b.getX());
        if (angle < 0) angle += 2 * Math.PI;
        return angle;
    }

    public static class CompareByXThenY implements Comparator<Vertex> {
        @Override
        public int compare(Vertex p1, Vertex p2) {
            int cmp = Double.compare(p1.getX(), p2.getX());
            if (cmp != 0) {
                return cmp;
            }
            return Double.compare(p1.getY(), p2.getY());
        }
    }

    public static class CompareByDistanceToOrigin implements Comparator<Vertex> {
        @Override
        public int compare(Vertex p1, Vertex p2) {
            double distance1 = Math.sqrt(p1.getX() * p1.getX() + p1.getY() * p1.getY());
            double distance2 = Math.sqrt(p2.getX() * p2.getX() + p2.getY() * p2.getY());
            return Double.compare(distance1, distance2);
        }
    }

    @FunctionalInterface
    public interface AngleCalculator {
        double calculate(Vertex vertex);
    }

    public static class CompareByAngle implements Comparator<Vertex> {
        private final AngleCalculator calculator;

        CompareByAngle(AngleCalculator calculator) {
            this.calculator = calculator;
        }

        @Override
        public int compare(Vertex o1, Vertex o2) {
            double x1 = this.calculator.calculate(o1);
            double x2 = this.calculator.calculate(o2);
            return Double.compare(x1, x2);
        }
    }

    public static class CompareByAngleDistance implements Comparator<Vertex> {
        private final Vertex center, ref;
        private final boolean clockwise;

        public CompareByAngleDistance(Vertex center, Vertex ref, boolean clockwise) {
            this.center=center;
            this.ref=ref;
            this.clockwise = clockwise;
        }

        @Override
        public int compare(Vertex o1, Vertex o2) {
            double d1, d2;
            if (clockwise) {
                d1 = getAngle(ref, center, o1);
                d2 = getAngle(ref, center, o2);
            } else {
                d1 = getAngle(o1, center, ref);
                d2 = getAngle(o2, center, ref);
            }
            if (Double.compare(d1, d2)==0) {
                return Double.compare(o2.distanceFrom(center),o1.distanceFrom(center));
            }
            return Double.compare(d1, d2);
        }
    }
}
