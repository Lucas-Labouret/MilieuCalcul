package local.furthestpointoptimization.model.miseEnBoite;

import local.furthestpointoptimization.model.vertexSets.Vertex;
import local.furthestpointoptimization.model.vertexSets.VertexSet;

import java.util.HashMap;

@FunctionalInterface
public interface MiseEnBoite {
    HashMap<Vertex, Coord> miseEnBoite(VertexSet vs);
}
