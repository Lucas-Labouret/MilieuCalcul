package local.computingMedia.canning;

public record Coord(int X, int Y) {
    public String toString() {
        return "(" + X + "," + Y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Coord(int i1, int j1))) return false;
        return X == i1 && Y == j1;
    }

    @Override
    public int hashCode() {
        return Y < X ? X * X + Y : Y * Y + X + Y;
    }

}