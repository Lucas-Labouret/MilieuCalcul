package local.computingMedium;

import local.misc.geometry.Triangle;

import java.util.Objects;

public class Face extends Triangle<Vertex> {
    public Face(Vertex a, Vertex b, Vertex c) {
        super(a, b, c);
    }

    @Override
    protected Vertex makePoint(double x, double y) {
        return new Vertex(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Face face)) return false;
        return Objects.equals(this.getVertices(), face.getVertices());
    }
}
