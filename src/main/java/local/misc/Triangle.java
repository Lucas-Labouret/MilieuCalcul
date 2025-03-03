package local.misc;

import local.computingMedium.Face;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Triangle extends GenericTriangle<Point> {
    public Triangle(Point a, Point b, Point c) {
        super(a, b, c);
    }

    @Override
    protected Point makePoint(double x, double y) {
        return new Point(x, y);
    }

    @Override
    public HashSet<Point> getVertices() {
        return new HashSet<>(List.of(a, b, c));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Triangle triangle)) return false;
        return Objects.equals(this.getVertices(), triangle.getVertices());
    }
}
