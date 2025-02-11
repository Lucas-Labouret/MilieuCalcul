package local.furthestpointoptimization.model.miseEnBoite;

import local.furthestpointoptimization.model.Vertex;
import local.furthestpointoptimization.model.vertexSets.VertexSet;

import java.util.HashMap;

@Deprecated
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
