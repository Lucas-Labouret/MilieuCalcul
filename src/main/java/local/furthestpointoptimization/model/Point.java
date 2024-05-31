package local.furthestpointoptimization.model;

import java.util.Comparator;
import java.util.Objects;

public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof Point point) {
            return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return '(' + String.valueOf(getX()) + ',' + String.valueOf(getY()) + ')';
    }

    public double distance_from(Point other) {
        return Math.sqrt((this.x - other.x)*(this.x - other.x) + (this.y - other.y)*(this.y - other.y));
        
    }

    public static class CompareByXThenY implements Comparator<Point> {
        @Override
        public int compare(Point p1, Point p2) {
            int cmp = Double.compare(p1.getX(), p2.getX());
            if (cmp != 0) {
                return cmp;
            }
            return Double.compare(p1.getY(), p2.getY());
        }
    }

    public static class CompareByDistanceToOrigin implements Comparator<Point> {
        @Override
        public int compare(Point p1, Point p2) {
            double distance1 = Math.sqrt(p1.getX() * p1.getX() + p1.getY() * p1.getY());
            double distance2 = Math.sqrt(p2.getX() * p2.getX() + p2.getY() * p2.getY());
            return Double.compare(distance1, distance2);
        }
    }
}
