package local.furthestpointoptimization.model;

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

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof Point point)
            return Math.abs(this.getX() - point.getX()) < 1e-6 && Math.abs(this.getY() - point.getY()) < 1e-6;
        return false;
    }

    public int hashCode() {
        return 0;
    }
}
