package local.computingMedia.cannings.coords.sCoords;

public record VertexCoord(int X, int Y) {
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof VertexCoord(int i1, int j1))) return false;
        return X == i1 && Y == j1;
    }

    @Override
    public int hashCode() {
        return Y < X ? X * X + Y : Y * Y + X + Y;
    }

    @Override
    public String toString() {
        return "(" + X + ", " + Y + ")";
    }

    public int compareTo(VertexCoord other){
        if (Y < other.Y) return -1;
        if (Y > other.Y) return 1;

        return Integer.compare(X, other.X);
    }
}