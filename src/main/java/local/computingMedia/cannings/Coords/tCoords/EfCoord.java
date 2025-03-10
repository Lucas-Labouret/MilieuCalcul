package local.computingMedia.cannings.Coords.tCoords;

import local.computingMedia.cannings.Coords.sCoords.EdgeCoord;

public record EfCoord(int side, EdgeCoord edge) {
    public EfCoord{
        if (side < 0 || side > 1) {
            throw new IllegalArgumentException("Side must be 0 or 1");
        }
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
