package local.computingMedium.vertexSets;

import local.computingMedium.Vertex;

public class CristalSet extends VertexSet{
    public CristalSet(int width, int height) {
        double w = 1/(double)(width+1);
        for (int i = 1; i<=height; ++i) {
            for (int j = 1; j<=width; ++j) {
                double x = w*(j+0.5*(1-i%2));
                double y = (i-0.5)*w*(Math.sqrt(3)/2);

                Vertex v = new Vertex((x+randomEps()), (y+randomEps()));
                add(v);
            }
        }
    }

    public boolean isInBorder(Vertex v) {
        return false;
    }
}
