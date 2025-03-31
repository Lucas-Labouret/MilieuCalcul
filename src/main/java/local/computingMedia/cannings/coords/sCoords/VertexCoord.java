package local.computingMedia.cannings.coords.sCoords;

public record VertexCoord(int Y, int X) {
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof VertexCoord(int i1, int j1))) return false;
        return Y == i1 && X == j1;
    }

    @Override
    public int hashCode() {
        return X < Y ? Y * Y + X : X * X + Y + X;
    }

    @Override
    public String toString() {
        return "(" + Y + ", " + X + ")";
    }

    public int compareTo(VertexCoord other){
        if (Y < other.Y) return -1;
        if (Y > other.Y) return 1;

        return Integer.compare(X, other.X);
    }
}