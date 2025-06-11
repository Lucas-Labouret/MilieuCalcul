package local.simulatedAnnealing;

import local.simulatedAnnealing.neighborGenerator.neighborSelector.MaximumScoreSelector;
import local.simulatedAnnealing.neighborGenerator.NearestNeighborGenerator;
import local.computingMedia.cannings.vertexCannings.VertexCanning;
import local.simulatedAnnealing.acceptor.GreedyAcceptor;
import local.simulatedAnnealing.evaluator.EverageMaskEvaluator;
import local.simulatedAnnealing.temperatureRegulator.LinearTemperatureRegulator;

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
public class VertexCanningNearestNeighborAnnealer extends MaxIterationAnnealer<VertexCanning> {
    public VertexCanningNearestNeighborAnnealer(int maxIterations) {
        super(
                maxIterations,
                new LinearTemperatureRegulator(maxIterations),
                new EverageMaskEvaluator(),
                new GreedyAcceptor(),
                new NearestNeighborGenerator(new MaximumScoreSelector<>(new EverageMaskEvaluator()))
        );
    }
}
