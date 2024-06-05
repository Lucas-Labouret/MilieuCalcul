package local.furthestpointoptimization.model;

public class Coord {
    int i, j;

    Coord(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public static Coord minus(Coord a, Coord b) {
        return new Coord(a.i - b.i, a.j - b.j);
    }

}