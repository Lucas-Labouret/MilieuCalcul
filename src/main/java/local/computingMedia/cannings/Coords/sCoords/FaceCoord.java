package local.computingMedia.cannings.Coords.sCoords;

public record FaceCoord(int theta, VertexCoord vertex) {
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof EdgeCoord(int t1, VertexCoord a1))) return false;
        return theta == t1 && vertex.equals(a1);
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
