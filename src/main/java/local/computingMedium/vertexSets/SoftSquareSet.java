package local.computingMedium.vertexSets;

import java.io.Serial;

import local.computingMedium.Vertex;

public class SoftSquareSet extends SoftSet {
    @Serial private static final long serialVersionUID = 3644071857588825726L;

    public SoftSquareSet(int count) {
        for (int i = 0; i < count; i++) {
            double x = Math.random();
            double y = Math.random();
            this.add(new Vertex(x,y));
        }
    }

    @Override
    public boolean isInBorder(Vertex vertex) {
        return 0 <= vertex.getX() && vertex.getX() <= 1 &&
               0 <= vertex.getY() && vertex.getY() <= 1;
    }
}
