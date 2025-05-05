package local.computingMedia.cannings.evaluation;

public record MaskIndex(int side, int theta, int Y, int X) {
    @Override
    public int hashCode() {
        int left = side < theta ? theta * theta + side : side * side + theta + side;
        int right = X < Y ? Y * Y + X : X * X + Y + X;
        return left < right ? right * right + left : left * left + right + left;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof MaskIndex(int s, int t, int y, int x))) return false;
        return side == s && theta == t && Y == y && X == x;
    }

    @Override
    public String toString() {
        return "(" + side + "," + theta + "," + Y + "," + X + ")";
    }
}
