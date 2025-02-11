package local.furthestpointoptimization.model.miseEnBoite;

import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.vertexSets.VertexSet;

import java.util.HashMap;

import static local.furthestpointoptimization.model.vertexSets.VertexSet.randomEps;

public class CristalMeb implements MiseEnBoite{
    int width;
    int height;

    public CristalMeb(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public HashMap<Vertex, Coord> miseEnBoite(VertexSet ignored) {
        HashMap<Vertex, Coord> vs = new HashMap<>();
        double w = 1/(double)(width+1);
        for (int i = 1; i<=height; ++i) {
            for (int j = 1; j<=width; ++j) {
                double x = w*(j+0.5*(1-i%2));
                double y = (i-0.5)*w*(Math.sqrt(3)/(double)2);

                Vertex v = new Vertex((x+randomEps()), (y+randomEps()));
                vs.put(v, new Coord(i, j));
            }
        }
        return vs;
    }
}
