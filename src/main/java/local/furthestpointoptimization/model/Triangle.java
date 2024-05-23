package local.furthestpointoptimization.model;

public class Triangle {
    private final Vertex a;
    private final Vertex b;
    private final Vertex c;

    public Triangle(Vertex a, Vertex b, Vertex c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Vertex getA() { return a; }
    public Vertex getB() { return b; }
    public Vertex getC() { return c; }

    public Vertex getCircumcenter() {
        double x1 = a.getX(), y1 = a.getY();
        double x2 = b.getX(), y2 = b.getY();
        double x3 = c.getX(), y3 = c.getY();

        double a = Math.sqrt((x2 - x3)*(x2 - x3) + (y2 - y3)*(y2 - y3));
        double b = Math.sqrt((x1 - x3)*(x1 - x3) + (y1 - y3)*(y1 - y3));
        double c = Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));

        double D = 2 * (x1*(y2 - y3) + x2*(y3 - y1) + x3*(y1 - y2));

        double v1 = a * (x1 * x1 + y1 * y1);
        double v2 = b * (x2 * x2 + y2 * y2);
        double v3 = c * (x3 * x3 + y3 * y3);

        double x = ((v1 - v2 + v3) / D);
        double y = ((v1 + v2 - v3) / D);

        return new Vertex(x, y);
    }
    public double getCircumRadius() {Vertex circumcenter = getCircumcenter();
        return Math.sqrt(Segment.length2(a, circumcenter));
    }

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

    public Vertex getOrthocenter() {
        double x1 = a.getX(), y1 = a.getY();
        double x2 = b.getX(), y2 = b.getY();
        double x3 = c.getX(), y3 = c.getY();

        double A = x1 - x2;
        double B = y1 - y2;
        double C = x1 - x3;
        double D = y1 - y3;
        double E = (A * (x1 + x2) + B * (y1 + y2)) / 2;
        double F = (C * (x1 + x3) + D * (y1 + y3)) / 2;

        double x = (E * D - B * F) / (A * D - B * C);
        double y = (A * F - E * C) / (A * D - B * C);

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
        double x = (a.getX() + b.getX() + c.getX()) / 3;
        double y = (a.getY() + b.getY() + c.getY()) / 3;
        return new Vertex(x, y);
    }
    public double getBarycenterDist() {
        return Math.min(
            Segment.length(a, getBarycenter()),
            Math.min(
                Segment.length(b, getBarycenter()),
                Segment.length(c, getBarycenter())
            )
        );
    }

    public Vertex getIncenter() {
        double x1 = a.getX(), y1 = a.getY();
        double x2 = b.getX(), y2 = b.getY();
        double x3 = c.getX(), y3 = c.getY();

        double a = Math.sqrt((x2 - x3)*(x2 - x3) + (y2 - y3)*(y2 - y3));
        double b = Math.sqrt((x1 - x3)*(x1 - x3) + (y1 - y3)*(y1 - y3));
        double c = Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));

        double x = (a * x1 + b * x2 + c * x3) / (a + b + c);
        double y = (a * y1 + b * y2 + c * y3) / (a + b + c);

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

    public boolean contains(Vertex v) {
        double denominator = ((b.getY() - c.getY()) * (a.getX() - c.getX()) + (c.getX() - b.getX()) * (a.getY() - c.getY()));
        if (denominator == 0) return false;
        double alpha = ((b.getY() - c.getY()) * (v.getX() - c.getX()) + (c.getX() - b.getX()) * (v.getY() - c.getY())) / denominator;
        double beta = ((c.getY() - a.getY()) * (v.getX() - c.getX()) + (a.getX() - c.getX()) * (v.getY() - c.getY())) / denominator;
        double gamma = 1.0 - alpha - beta;

        return alpha >= 0 && beta >= 0 && gamma >= 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Triangle triangle = (Triangle) obj;
        return this.getVertices().equals(triangle.getVertices());
    }

    public String toString() {
        return "(" + a + ", " + b + ", " + c + ")";
    }

    public int hashCode() {
        return this.getVertices().hashCode();
    }
}
