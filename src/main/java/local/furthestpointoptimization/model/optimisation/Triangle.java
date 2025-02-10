package local.furthestpointoptimization.model.optimisation;

import local.furthestpointoptimization.model.vertexSets.Point;
import local.furthestpointoptimization.model.vertexSets.Vertex;
import local.furthestpointoptimization.model.vertexSets.VertexSet;

import java.util.Objects;

public class Triangle {
    // VOIR https://www.geogebra.org/m/gqxh5a8x
    
    private final Vertex a;
    private final Vertex b;
    private final Vertex c;

    public Triangle(Vertex a, Vertex b, Vertex c) {
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

    public Vertex getA() { return a; }
    public Vertex getB() { return b; }
    public Vertex getC() { return c; }

    /** centre du cercle circonscrit | centre du cercle passant par les 3 sommets */
    public Vertex getCircumcenter() {
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
        
        return new Vertex(Ux, Uy);
    }
    public double getCircumRadius() {
        Vertex circumcenter = getCircumcenter();
        return Math.sqrt(Segment.length2(a, circumcenter));
    }

    /** centre de masse ou centroïde | intersection des droite passant par sommet et milieu opposé */
    public Vertex getCentroid() {
        double x = (a.getX() + b.getX() + c.getX()) / 3;
        double y = (a.getY() + b.getY() + c.getY()) / 3;
        return new Vertex(x, y);
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
    public Vertex getOrthocenter() {
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

        return new Vertex(x, y);
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


    public Vertex getBarycenter() {
        return this.getCentroid();
    }
    public double getBarycenterDist() {
        return getCentroidDist();
    }

    /** Centre du cercle incrit */
    public Vertex getIncenter() {
        double xa = a.getX(), ya = a.getY();
        double xb = b.getX(), yb = b.getY();
        double xc = c.getX(), yc = c.getY();

        double a = this.b.distance_from(this.c);
        double b = this.a.distance_from(this.c);
        double c = this.a.distance_from(this.b);

        double perimeter = a+b+c;
        double x = (a * xa + b * xb + c * xc) / perimeter;
        double y = (a * ya + b * yb + c * yc) / perimeter;
        return new Vertex(x, y);
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

    public VertexSet getVertices() {
        return new VertexSet(a, b, c);
    }

    public boolean _contains(Point p) {
        Point center = this.getCircumcenter();
        return Double.compare(p.distance_from(center), this.getCircumRadius()) < 0;
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
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Triangle triangle = (Triangle) obj;
        return Objects.equals(this.getVertices(), triangle.getVertices());
    }

    @Override
    public String toString() {
        // return "(" + a + ", " + b + ", " + c + ")";
        return "(" + a.getId() + ", " + b.getId() + ", " + c.getId() + ")";
    }

    @Override
    public int hashCode() {
        return this.getVertices().hashCode();
    }
}
