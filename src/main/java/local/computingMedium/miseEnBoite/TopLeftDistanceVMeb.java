package local.computingMedium.miseEnBoite;

import local.computingMedium.Vertex;
import local.computingMedium.media.Medium;

import java.util.HashMap;

@Deprecated //Gave the same coord to multiple vertices
public class TopLeftDistanceVMeb implements VertexMeB {
    @Override
    public HashMap<Vertex, Coord> miseEnBoite(Medium medium) {
        DistanceToSideGetter distanceToSideGetter = new DistanceToSideGetter();
        HashMap<Vertex, Integer> topCoords = distanceToSideGetter.getDistanceToTop(medium);
        HashMap<Vertex, Integer> leftCoords = distanceToSideGetter.getDistanceToLeft(medium);

        HashMap<Vertex, Coord> result = new HashMap<>();
        for (Vertex vertex : topCoords.keySet()) {
            result.put(vertex, new Coord(topCoords.get(vertex), leftCoords.get(vertex)));
        }
        return result;
    }
}
