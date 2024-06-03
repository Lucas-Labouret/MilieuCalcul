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

    public static double cross(Point center, Point a, Point b) {
        return (a.x-center.x)*(b.y-center.y) - (a.y-center.y)*(b.x-center.x);
    } 

    public double cross(Point a, Point b) {
        return Point.cross(this, a, b);
    } 

    // colin <=> c.cross(A,B) == 0
    // ClockWise <=> c.cross(A,B) <= 0
    // CounterClockWise <=> c.cross(A,B) >= 0

    // TODO doc
    public static double angleBetweenThreePoints(Point a, Point b, Point c) {
        double ba_x = a.getX() - b.getX();
        double ba_y = a.getY() - b.getY();
        double bc_x = c.getX() - b.getX();
        double bc_y = c.getY() - b.getY();
        double dotProduct = ba_x * bc_x + ba_y * bc_y;
        double det = ba_x * bc_y - ba_y * bc_x;
        double res = Math.toDegrees(Math.atan2(det, dotProduct));
        if (res < 0.0) {
            return res + 360.0;
        } else {
            return res;
        }
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
    static interface AngleCalculator {
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
}
