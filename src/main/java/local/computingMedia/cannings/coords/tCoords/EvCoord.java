package local.computingMedia.cannings.coords.tCoords;

import local.computingMedia.cannings.coords.sCoords.EdgeCoord;

public record EvCoord(int side, EdgeCoord edge) {
    public EvCoord {
        if (side < 0 || side > 1) {
            throw new IllegalArgumentException("Side must be 0 or 1");
        }
    }

    public EvCoord(int side, int theta, int Y, int X) {
        this(side, new EdgeCoord(theta, Y, X));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof EfCoord(int s1, EdgeCoord e1))) return false;
        return side == s1 && edge.equals(e1);
    }

    @Override
    public int hashCode() {
        return edge.hashCode() + side;
    }

    @Override
    public String toString() {
        return "(" + side + ", " + edge + ")";
    }
}
