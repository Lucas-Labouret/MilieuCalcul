package local.computingMedia.miseEnBoite;

import local.computingMedia.geometry.Vertex;
import local.computingMedia.media.Medium;

import java.util.HashMap;

@FunctionalInterface
public interface VertexMeB {
    HashMap<Vertex, Coord> miseEnBoite(Medium medium);
}
