package local.computingMedia.cannings.simulatedAnnealing;

import local.computingMedia.cannings.vertexCannings.VertexCanning;
import local.computingMedia.media.Medium;
import local.misc.simulatedAnnealing.StandardAnnealer;
import local.misc.simulatedAnnealing.StandardAcceptor;
import local.misc.simulatedAnnealing.StandardTemperatureRegulator;

/**
 * VertexCanningNearestNeighborAnnealer is a specialized annealer for vertex cannings.
 * It attempts to minimize the average number of masks used by every communication type.
 * <p>
 * Exploration of the solution space is done in two ways:
 * 1. By repositioning a vertex to an adjacent empty cell in the canning.
 * 2. By merging two adjacent lines or columns that don't overlap.
 * In both cases, the selection is biased toward modifications of elements that are "close" to each other.
 * </p>
 */
public class VertexCanningNearestNeighborAnnealer extends StandardAnnealer<VertexCanning, Medium> {
    public VertexCanningNearestNeighborAnnealer(int maxIterations) {
        super(
                maxIterations,
                new StandardTemperatureRegulator(maxIterations),
                new EverageMaskEvaluator(),
                new StandardAcceptor(),
                new NearestNeighborGenerator()
        );
    }
}
