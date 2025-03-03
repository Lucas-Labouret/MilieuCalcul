package local.misc;

public class Segment extends GenericSegment<Point> {
    public Segment(Point start, Point end) {
        super(start, end);
    }

    public Point getMiddle() {
        return new Point((start.getX() + end.getX()) / 2, (start.getY() + end.getY()) / 2);
    }
}
