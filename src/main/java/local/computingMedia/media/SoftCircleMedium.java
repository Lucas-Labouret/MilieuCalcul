package local.computingMedia.media;

import local.misc.geometry.Point;
import local.computingMedia.Vertex;

@SuppressWarnings("serial")
public class SoftCircleMedium extends SoftBorderedMedium {
    public SoftCircleMedium() {}

    public SoftCircleMedium(Vertex... vertices) {
        super(vertices);
    }

    public SoftCircleMedium copy() {
        return new SoftCircleMedium(this.toArray(new Vertex[0]));
    }

    public SoftCircleMedium(int count) {
        for (int i = 0; i < count; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double radius = Math.random()/4;
            double x = 0.5+radius*Math.cos(angle);
            double y = 0.5+radius*Math.sin(angle);
            add(new Vertex(x, y));
        }
    }

    @Override
    public boolean isInBorder(Vertex vertex) {
        return vertex.distanceFrom(new Point(0.5, 0.5)) < 0.5;
    }
}
