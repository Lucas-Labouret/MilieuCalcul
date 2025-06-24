package simulatedAnnealing.evaluator;

import computingMedia.cannings.coords.sCoords.VertexCoord;
import computingMedia.cannings.vertexCannings.VertexCanning;
import computingMedia.sLoci.Vertex;

/**
 * Evaluator that calculates the Manhattan distance between vertices in a VertexCanning.
 * We want to minimize the average distance between vertices and their neighbors,
 * while prioritizing the reduction of the overall maximum distance.
 */
public class ManhattanDistanceEvaluator implements Evaluator<VertexCanning> {
    @Override
    public double evaluate(VertexCanning candidate) {
        double averageDistance = 0.0;
        int maxDistance = 0;
        for (Vertex v: candidate.getMedium()) for (Vertex n: v.getNeighbors()) {
            VertexCoord vPos = candidate.getVertexCanning().get(v);
            VertexCoord nPos = candidate.getVertexCanning().get(n);

            int distance = Math.abs(vPos.Y() - nPos.Y()) + Math.abs(vPos.X() - nPos.X());
            averageDistance += distance;
            if (distance > maxDistance) {
                maxDistance = distance;
            }
        }
        averageDistance /= candidate.getMedium().size();
        return -averageDistance * maxDistance;
    }
}
