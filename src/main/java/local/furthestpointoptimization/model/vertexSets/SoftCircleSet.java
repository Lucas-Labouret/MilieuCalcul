package local.furthestpointoptimization.model.vertexSets;

import local.furthestpointoptimization.model.Point;
import local.furthestpointoptimization.model.Vertex;

import java.io.Serial;

public class SoftCircleSet extends SoftSet {
    @Serial private static final long serialVersionUID = -4971010843396509991L;

    public SoftCircleSet(int count) {
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
