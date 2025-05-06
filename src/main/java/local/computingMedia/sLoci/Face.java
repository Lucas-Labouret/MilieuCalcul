package local.computingMedia.sLoci;

import java.util.Objects;
import java.util.Set;

public class Face {
    protected final Vertex a;
    protected final Vertex b;
    protected final Vertex c;
    
    public Face(Vertex a, Vertex b, Vertex c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
    public Set<Vertex> getVertices(){
        return Set.of(a, b, c);
    }

    public boolean contains(Vertex v) {
        double denominator = ((b.getY() - c.getY()) * (a.getX() - c.getX()) + (c.getX() - b.getX()) * (a.getY() - c.getY()));
        if (denominator == 0) return false;
        double alpha = ((b.getY() - c.getY()) * (v.getX() - c.getX()) + (c.getX() - b.getX()) * (v.getY() - c.getY())) / denominator;
        double beta = ((c.getY() - a.getY()) * (v.getX() - c.getX()) + (a.getX() - c.getX()) * (v.getY() - c.getY())) / denominator;
        double gamma = 1.0 - alpha - beta;

        return alpha >= 0 && beta >= 0 && gamma >= 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Face face)) return false;
        return Objects.equals(this.getVertices(), face.getVertices());
    }

    @Override
    public int hashCode() {
        return this.getVertices().hashCode();
    }

    @Override
    public String toString() {
        // return "(" + a + ", " + b + ", " + c + ")";
        return "(" + a.toString() + ", " + b.toString() + ", " + c.toString() + ")";
    }

    public static double area(Vertex a , Vertex b, Vertex c) {
        return Math.abs(
                ((a.getX() - c.getX()) * (b.getY() - a.getY()) -
                 (a.getX() - b.getX()) * (c.getY() - a.getY())) / 2
        );
    }
    public double area() {
        return area(a, b, c);
    }

    public Vertex getCircumcenter() {
        double xa = a.getX(), ya = a.getY();
        double xb = b.getX(), yb = b.getY();
        double xc = c.getX(), yc = c.getY();

        double a = (xa*xa+ya*ya);
        double b = (xb*xb+yb*yb);
        double c = (xc*xc+yc*yc);

        double D = 2*(xa*(yb - yc)+xb*(yc - ya)+xc*(ya - yb));
        double Ux = (a*(yb-yc)+b*(yc-ya)+c*(ya-yb))/D;
        double Uy = (a*(xc-xb)+b*(xa-xc)+c*(xb-xa))/D;

        return new Vertex(Ux, Uy);
    }
    public double getCircumradius() {
        Vertex circumcenter = getCircumcenter();
        return Math.sqrt(Edge.length2(a, circumcenter));
    }

    public Vertex getCentroid() {
        double x = (a.getX() + b.getX() + c.getX()) / 3;
        double y = (a.getY() + b.getY() + c.getY()) / 3;
        return new Vertex(x, y);
    }
    public double getCentroidDist() {
        return Math.min(
                Edge.length(a, getCentroid()),
                Math.min(
                        Edge.length(b, getCentroid()),
                        Edge.length(c, getCentroid())
                )
        );
    }

    public Vertex getOrthocenter() {
        double xa = a.getX(), ya = a.getY();
        double xb = b.getX(), yb = b.getY();
        double xc = c.getX(), yc = c.getY();

        double A = xc - xb;
        double B = yc - yb;
        double C = xc - xa;
        double D = yc - ya;
        double E = xa*A+ya*B;
        double F = xb*C+yb*D;

        double denominator = (A * D - B * C);
        double x = (E * D - B * F) / denominator;
        double y = (A * F - E * C) / denominator;

        return new Vertex(x, y);
    }
    public double getOrthocenterDist() {
        return Math.min(
                Edge.length(a, getOrthocenter()),
                Math.min(
                        Edge.length(b, getOrthocenter()),
                        Edge.length(c, getOrthocenter())
                )
        );
    }

    public Vertex getIncenter() {
        double xa = a.getX(), ya = a.getY();
        double xb = b.getX(), yb = b.getY();
        double xc = c.getX(), yc = c.getY();

        double a = this.b.distanceFrom(this.c);
        double b = this.a.distanceFrom(this.c);
        double c = this.a.distanceFrom(this.b);

        double perimeter = a+b+c;
        double x = (a * xa + b * xb + c * xc) / perimeter;
        double y = (a * ya + b * yb + c * yc) / perimeter;
        return new Vertex(x, y);
    }
    public double getIncenterDist() {
        return Math.min(
                Edge.length(a, getIncenter()),
                Math.min(
                        Edge.length(b, getIncenter()),
                        Edge.length(c, getIncenter())
                )
        );
    }

    public static boolean inCircumscribedCircle(Vertex a, Vertex b, Vertex c, Vertex d){
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
        ) > 0;
    }

    public boolean inCircumscribedCircle(Vertex d){
        return inCircumscribedCircle(a, b, c, d);
    }

    public static int sortCWWithReferencePoint(Vertex ref, Face f1, Face f2) {
        Vertex centroid1 = f1.getCentroid();
        Vertex centroid2 = f2.getCentroid();

        double angle1 = Math.atan2(centroid1.getY() - ref.getY(), centroid1.getX() - ref.getX());
        double angle2 = Math.atan2(centroid2.getY() - ref.getY(), centroid2.getX() - ref.getX());

        if (angle1 < 0) angle1 += 2 * Math.PI;
        if (angle2 < 0) angle2 += 2 * Math.PI;

        return Double.compare(angle1, angle2);
    }
}
