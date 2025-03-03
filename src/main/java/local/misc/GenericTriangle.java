package local.misc;

import local.computingMedium.vertexSets.VertexSet;

import java.util.Objects;
import java.util.Set;

public abstract class GenericTriangle<T extends Point> {
    // VOIR https://www.geogebra.org/m/gqxh5a8x
    
    protected final T a;
    protected final T b;
    protected final T c;

    protected abstract T makePoint(double x, double y);

    public GenericTriangle(T a, T b, T c) {
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
        T circumcenter = getCircumcenter();
        return Math.sqrt(GenericSegment.length2(a, circumcenter));
    }

    /** centre de masse ou centroïde | intersection des droite passant par sommet et milieu opposé */
    public T getCentroid() {
        double x = (a.getX() + b.getX() + c.getX()) / 3;
        double y = (a.getY() + b.getY() + c.getY()) / 3;
        return makePoint(x, y);
    }
    public double getCentroidDist() {
        return Math.min(
            GenericSegment.length(a, getCentroid()),
            Math.min(
                GenericSegment.length(b, getCentroid()),
                GenericSegment.length(c, getCentroid())
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
            GenericSegment.length(a, getOrthocenter()),
            Math.min(
                GenericSegment.length(b, getOrthocenter()),
                GenericSegment.length(c, getOrthocenter())
            )
        );
    }


    public T getBarycenter() {
        return this.getCentroid();
    }
    public double getBarycenterDist() {
        return getCentroidDist();
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
            GenericSegment.length(a, getIncenter()),
            Math.min(
                GenericSegment.length(b, getIncenter()),
                GenericSegment.length(c, getIncenter())
            )
        );
    }

    public abstract Set<T> getVertices();

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
    public abstract boolean equals(Object obj);

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
