package local.computingMedia.cannings.coords.tCoords;

import local.computingMedia.cannings.coords.sCoords.FaceCoord;

public record FvCoord(int side, FaceCoord face) {
    public FvCoord {
        if (side < 0 || side > 2) {
            throw new IllegalArgumentException("Side must be 0, 1 or 2");
        }
    }

    public FvCoord(int side, int theta, int Y, int X){
        this(side, new FaceCoord(theta, Y, X));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof FvCoord(int s1, FaceCoord f1))) return false;
        return side == s1 && face.equals(f1);
    }

    @Override
    public int hashCode() {
        return face.hashCode() + side;
    }

    @Override
    public String toString() {
        return "(" + side + ", " + face + ")";
    }
}
