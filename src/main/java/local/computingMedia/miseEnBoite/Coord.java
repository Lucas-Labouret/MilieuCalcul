package local.computingMedia.miseEnBoite;

public record Coord(int i, int j) {
    public String toString() {
        return "(" + i + "," + j + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Coord(int i1, int j1))) return false;
        return i == i1 && j == j1;
    }

    @Override
    public int hashCode() {
        return j < i ? i * i + j : j * j + i + j;
    }

}