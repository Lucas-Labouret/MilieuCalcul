package local.computingMedium;

import local.computingMedium.vertexSets.VertexSet;
import local.misc.GenericTriangle;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Face extends GenericTriangle<Vertex> {
    public Face(Vertex a, Vertex b, Vertex c) {
        super(a, b, c);
    }

    @Override
    protected Vertex makePoint(double x, double y) {
        return new Vertex(x, y);
    }

    @Override
    public HashSet<Vertex> getVertices() {
        return new HashSet<>(List.of(a, b, c));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Face face)) return false;
        return Objects.equals(this.getVertices(), face.getVertices());
    }
}
