package local.computingMedium.miseEnBoite;

import local.computingMedium.Vertex;
import local.computingMedium.media.Medium;

import java.util.HashMap;

@FunctionalInterface
public interface VertexMeB {
    HashMap<Vertex, Coord> miseEnBoite(Medium medium);
}
