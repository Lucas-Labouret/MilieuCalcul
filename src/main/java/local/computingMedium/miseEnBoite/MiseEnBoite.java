package local.computingMedium.miseEnBoite;

import local.computingMedium.Vertex;
import local.computingMedium.vertexSets.VertexSet;
import local.misc.Coord;

import java.util.HashMap;

@FunctionalInterface
public interface MiseEnBoite {
    HashMap<Vertex, Coord> miseEnBoite(VertexSet vs);
}
