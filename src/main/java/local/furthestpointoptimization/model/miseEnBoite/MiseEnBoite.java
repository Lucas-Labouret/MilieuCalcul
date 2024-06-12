package local.furthestpointoptimization.model.miseEnBoite;

import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.VertexSet;

import java.util.HashMap;

@FunctionalInterface
public interface MiseEnBoite {
    HashMap<Vertex, Coord> miseEnBoite(VertexSet vs);
}
