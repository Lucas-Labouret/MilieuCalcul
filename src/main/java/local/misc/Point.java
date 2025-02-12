package local.misc;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class Point implements Serializable {
    @Serial private static final long serialVersionUID = -5217163308210341910L;

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

    public boolean almostEquals(Point other, double epsilon) {
        return Math.abs(this.x - other.x) < epsilon && Math.abs(this.y - other.y) < epsilon;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return '(' + String.valueOf(getX()) + ',' + String.valueOf(getY()) + ')';
    }

    public double distanceFrom(Point other) {
        return Math.sqrt((this.x - other.x)*(this.x - other.x) + (this.y - other.y)*(this.y - other.y));
        
    }

    public static double cross(Point center, Point a, Point b) {
        return (a.x-center.x)*(b.y-center.y) - (a.y-center.y)*(b.x-center.x);
    } 

    public double cross(Point a, Point b) {
        return Point.cross(this, a, b);
    } 

    // colin <=> c.cross(A,B) == 0
    // ClockWise <=> c.cross(A,B) <= 0
    // CounterClockWise <=> c.cross(A,B) >= 0

    public static boolean areAllPointsCollinear(List<Point> points) {
        if (points.size() < 3) {
            return true; // Deux points ou moins sont toujours colinéaires
        }

        Point p1 = points.get(0);
        Point p2 = points.get(1);

        for (int i = 2; i < points.size(); i++) {
            Point p3 = points.get(i);
            if (orientation(p1, p2, p3) != Orientation.CoLineaire) {
                return false;
            }
        }

        return true;
    }

    private static Orientation orientation(Point p1, Point p2, Point p3) {
        double val = (p2.getY() - p1.getY()) * (p3.getX() - p2.getX()) -
                     (p2.getX() - p1.getX()) * (p3.getY() - p2.getY());

        if (val == 0) {
            return Orientation.CoLineaire;
        } else if (val > 0) {
            return Orientation.ClockWise;
        } else {
            return Orientation.CounterClockWise;
        }
    }

    private enum Orientation {
        CoLineaire,
        ClockWise,
        CounterClockWise
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

    @FunctionalInterface
    public interface AngleCalculator {
        double calculate(Point point);
    }

    public static class CompareByAngle implements Comparator<Point> {
        private AngleCalculator calculator;

        CompareByAngle(AngleCalculator calculator) {
            this.calculator = calculator;
        }

        @Override
        public int compare(Point o1, Point o2) {
            double x1 = this.calculator.calculate(o1);
            double x2 = this.calculator.calculate(o2);
            return Double.compare(x1, x2);
        }
    }

    public static class CompareByAngleDistance implements Comparator<Point> {
        private Point center, ref;
        private boolean clockwise;

        public CompareByAngleDistance(Point center, Point ref, boolean clockwise) {
            this.center=center;
            this.ref=ref;
            this.clockwise = clockwise;
        }

        //Moche
        
        @Override
        public int compare(Point o1, Point o2) {
            double d1 = 0;
            double d2 = 0;
            if (clockwise) {
                d1 = GeometricPrimitives.getAngle(ref, center, o1);
                d2 = GeometricPrimitives.getAngle(ref, center, o2);
            } else {
                d1 = GeometricPrimitives.getAngle(o1, center, ref);
                d2 = GeometricPrimitives.getAngle(o2, center, ref);
            }
            if (Double.compare(d1, d2)==0) {
                return Double.compare(o2.distanceFrom(center),o1.distanceFrom(center));
            }
            return Double.compare(d1, d2);
        }
    }
}
