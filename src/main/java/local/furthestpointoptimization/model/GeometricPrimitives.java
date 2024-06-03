package local.furthestpointoptimization.model;

import javafx.geometry.Orientation;

class GeometricPrimitives {
    private GeometricPrimitives(){}

    private static double inCirclePrimitive(Vertex a, Vertex b, Vertex c, Vertex d){
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
    private static double orientationPrimitive(Vertex a, Vertex b, Vertex c){
        return (b.getY() - a.getY()) * (c.getX() - b.getX()) - (b.getX() - a.getX()) * (c.getY() - b.getY());
    }

    public enum Orientation {
        CoLineaire, // 0
        ClockWise, // 1
        CounterClockWise; // 2
    }

    public static Orientation orientation(Vertex a, Vertex b, Vertex c){
        double val = orientationPrimitive(a, b, c);

        if (val == 0) return Orientation.CoLineaire;
        return (val > 0) ? Orientation.CounterClockWise : Orientation.ClockWise;
    }
    public static boolean ccw(Vertex a, Vertex b, Vertex c){
        return orientation(a, b, c) == Orientation.CounterClockWise;
    }
    public static boolean inCircle(Vertex a, Vertex b, Vertex c, Vertex d){
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
}