package local.furthestpointoptimization.model;

public class Segment {
    private final Point start;
    private final Point end;

    public Segment(Point start, Point end) {
        if (start.equals(end)) {
            throw new IllegalArgumentException("Les points de début et de fin ne peuvent pas être identiques pour creer un segment");
        }
        this.start = start;
        this.end = end;
    }

    /**returns a reference*/
    public Point getStart() { return start; }
    /**returns a reference*/
    public Point getEnd() { return end; }

    public static double length2(Point start, Point end) {
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        return dx * dx + dy * dy;
    }
    public static double length(Point start, Point end) {
        return Math.sqrt(length2(start, end));
    }

    public double length2() {
        return length2(start, end);
    }
    public double length() {
        return length(start, end);
    }

    @Override
    public String toString() {
        return '[' + start.toString() + ',' + end.toString() + ']';
    }
}
