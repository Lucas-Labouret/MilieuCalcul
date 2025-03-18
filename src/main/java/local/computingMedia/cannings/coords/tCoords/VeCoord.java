package local.computingMedia.cannings.coords.tCoords;

import local.computingMedia.cannings.coords.sCoords.VertexCoord;

public record VeCoord(int theta, VertexCoord vertex) {
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof VeCoord(int t1, VertexCoord v1))) return false;
        return theta == t1 && vertex.equals(v1);
    }

    @Override
    public int hashCode() {
        return vertex.hashCode() + theta;
    }

    @Override
    public String toString() {
        return "(" + theta + ", " + vertex + ")";
    }
}
