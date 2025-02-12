package local.computingMedium.miseEnBoite;

import local.computingMedium.Vertex;
import local.computingMedium.vertexSets.VertexSet;
import local.misc.Coord;

import java.util.HashMap;

@Deprecated //Gave the same coord to multiple vertices
public class TopLeftDistanceMeb implements MiseEnBoite {
    @Override
    public HashMap<Vertex, Coord> miseEnBoite(VertexSet vs) {
        DistanceToSideGetter distanceToSideGetter = new DistanceToSideGetter();
        HashMap<Vertex, Integer> topCoords = distanceToSideGetter.getDistanceToTop(vs);
        HashMap<Vertex, Integer> leftCoords = distanceToSideGetter.getDistanceToLeft(vs);

        HashMap<Vertex, Coord> result = new HashMap<>();
        for (Vertex vertex : topCoords.keySet()) {
            result.put(vertex, new Coord(topCoords.get(vertex), leftCoords.get(vertex)));
        }
        return result;
    }
}
