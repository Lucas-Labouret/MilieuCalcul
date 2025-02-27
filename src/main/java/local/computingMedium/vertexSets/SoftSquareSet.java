package local.computingMedium.vertexSets;

import local.computingMedium.Vertex;

@SuppressWarnings("serial")
public class SoftSquareSet extends SoftSet {
    public SoftSquareSet() {}

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
