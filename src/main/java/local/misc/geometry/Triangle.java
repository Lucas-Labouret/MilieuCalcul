package local.misc.geometry;

import java.util.HashSet;
import java.util.Set;

public class Triangle<T extends Point> {
    // VOIR https://www.geogebra.org/m/gqxh5a8x
    
    protected final T a;
    protected final T b;
    protected final T c;

    protected T makePoint(double x, double y) {
        return (T) new Point(x, y);
    }

    public Triangle(T a, T b, T c) {
        // if (a.equals(b) || b.equals(c) || c.equals(a)) {
            // throw new IllegalArgumentException("Les points d'un triangle ne peuvent pas etre identiques");
        // }

        // condition sur l'aire ?
        // double area2 = Math.abs(a.getX() * (b.getY() - c.getY()) + b.getX() * (c.getY() - a.getY()) + c.getX() * (a.getY() - b.getY()));
        // if (area2 == 0) {
            // throw new IllegalArgumentException("Les points d'un triangle ne peuvent pas être alignés.");
        // }

        this.a = a;
        this.b = b;
        this.c = c;
    }

    public T getA() { return a; }
    public T getB() { return b; }
    public T getC() { return c; }

    /** centre du cercle circonscrit | centre du cercle passant par les 3 sommets */
    public T getCircumcenter() {
        double xa = a.getX(), ya = a.getY();
        double xb = b.getX(), yb = b.getY();
        double xc = c.getX(), yc = c.getY();
    
        double a = (xa*xa+ya*ya);
        double b = (xb*xb+yb*yb);
        double c = (xc*xc+yc*yc);
        
        // Formule de cramer
        //     det(A_x)         a(yb-yc)+b(yc-ya)+c(ya-yb)
        //x = ---------- = -------------------------------------
        //      det(A)       2xa(yb-yc)+2xb(yc-ya)+2xc(ya-yb)
        // où det(A) est l'equation catésienne de cercle circonscrit
        // 

        // Denominator
        double D = 2*(xa*(yb - yc)+xb*(yc - ya)+xc*(ya - yb));
        double Ux = (a*(yb-yc)+b*(yc-ya)+c*(ya-yb))/D;
        double Uy = (a*(xc-xb)+b*(xa-xc)+c*(xb-xa))/D;
        
        return makePoint(Ux, Uy);
    }
    public double getCircumRadius() {
        Point circumcenter = getCircumcenter();
        return Math.sqrt(Segment.length2(a, circumcenter));
    }

    /** centre de masse ou centroïde | intersection des droite passant par sommet et milieu opposé */
    public T getCentroid() {
        double x = (a.getX() + b.getX() + c.getX()) / 3;
        double y = (a.getY() + b.getY() + c.getY()) / 3;
        return makePoint(x, y);
    }
    public double getCentroidDist() {
        return Math.min(
            Segment.length(a, getCentroid()),
            Math.min(
                Segment.length(b, getCentroid()),
                Segment.length(c, getCentroid())
            )
        );
    }

    /** orthocentre | Intersection des hauteurs */
    public T getOrthocenter() {
        double xa = a.getX(), ya = a.getY();
        double xb = b.getX(), yb = b.getY();
        double xc = c.getX(), yc = c.getY();

        // Systeme de produit scalaire (deux hauteurs suffisent)
        // { Ax + By = E
        // { Cx + Dy = F
        double A = xc - xb;
        double B = yc - yb;
        double C = xc - xa;
        double D = yc - ya;
        double E = xa*A+ya*B;
        double F = xb*C+yb*D;
        // Regle de cramer
        double denom = (A * D - B * C);
        double x = (E * D - B * F) / denom;
        double y = (A * F - E * C) / denom;

        return makePoint(x, y);
    }
    public double getOrthocenterDist() {
        return Math.min(
            Segment.length(a, getOrthocenter()),
            Math.min(
                Segment.length(b, getOrthocenter()),
                Segment.length(c, getOrthocenter())
            )
        );
    }

    /** Centre du cercle incrit */
    public T getIncenter() {
        double xa = a.getX(), ya = a.getY();
        double xb = b.getX(), yb = b.getY();
        double xc = c.getX(), yc = c.getY();

        double a = this.b.distanceFrom(this.c);
        double b = this.a.distanceFrom(this.c);
        double c = this.a.distanceFrom(this.b);

        double perimeter = a+b+c;
        double x = (a * xa + b * xb + c * xc) / perimeter;
        double y = (a * ya + b * yb + c * yc) / perimeter;
        return makePoint(x, y);
    }
    public double getIncenterDist() {
        return Math.min(
            Segment.length(a, getIncenter()),
            Math.min(
                Segment.length(b, getIncenter()),
                Segment.length(c, getIncenter())
            )
        );
    }

    public Set<T> getVertices(){
        return new HashSet<>(Set.of(a, b, c));
    }

    public boolean _contains(Point p) {
        Point center = this.getCircumcenter();
        return Double.compare(p.distanceFrom(center), this.getCircumRadius()) < 0;
    }
    public boolean contains(T v) {
        double denominator = ((b.getY() - c.getY()) * (a.getX() - c.getX()) + (c.getX() - b.getX()) * (a.getY() - c.getY()));
        if (denominator == 0) return false;
        double alpha = ((b.getY() - c.getY()) * (v.getX() - c.getX()) + (c.getX() - b.getX()) * (v.getY() - c.getY())) / denominator;
        double beta = ((c.getY() - a.getY()) * (v.getX() - c.getX()) + (a.getX() - c.getX()) * (v.getY() - c.getY())) / denominator;
        double gamma = 1.0 - alpha - beta;

        return alpha >= 0 && beta >= 0 && gamma >= 0;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj instanceof Triangle<?> triangle){
            return this.getVertices().equals(triangle.getVertices());
        }
        return false;
    }

    @Override
    public String toString() {
        // return "(" + a + ", " + b + ", " + c + ")";
        return "(" + a.toString() + ", " + b.toString() + ", " + c.toString() + ")";
    }

    @Override
    public int hashCode() {
        return this.getVertices().hashCode();
    }
}
