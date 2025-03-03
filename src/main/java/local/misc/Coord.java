package local.misc;

public class Coord {
    int i, j;

    public Coord(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public int getI() { return i; }
    public int getJ() { return j; }

    public static Coord minus(Coord a, Coord b) {
        return new Coord(a.i - b.i, a.j - b.j);
    }

    public String toString() {
        return "(" + i + "," + j + ")";
    }

    @Override public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Coord)) return false;
        Coord b = (Coord) o;
        return i == b.i && j == b.j;
    }

    @Override public int hashCode() {
        return j < i ? i * i + j : j * j + i + j;
    }

}