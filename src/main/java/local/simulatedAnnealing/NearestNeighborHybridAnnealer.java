package local.simulatedAnnealing;

import local.computingMedia.cannings.vertexCannings.VertexCanning;
import local.simulatedAnnealing.acceptor.GreedyAcceptor;
import local.simulatedAnnealing.acceptor.StandardAcceptor;
import local.simulatedAnnealing.evaluator.EverageMaskEvaluator;
import local.simulatedAnnealing.neighborGenerator.NearestNeighborGenerator;
import local.simulatedAnnealing.neighborGenerator.neighborSelector.MaximumScoreSelector;
import local.simulatedAnnealing.neighborGenerator.neighborSelector.WeightedRandomSelector;
import local.simulatedAnnealing.temperatureRegulator.LinearTemperatureRegulator;
import local.simulatedAnnealing.temperatureRegulator.LogTemperatureRegulator;

/**
 * Using only the maximum weight metric yields mediocre results, but max score is terribly slow,
 * so we try a hybrid approach, first optimizing for maximum weight, then refining the solution with maximum score.
 */
public class NearestNeighborHybridAnnealer extends Annealer<VertexCanning> {
    private final MaxIterationAnnealer<VertexCanning> maxIterationAnnealerMaxWeight;
    private final AcceptanceBreakpointAnnealer<VertexCanning> acceptanceBreakpointAnnealer;

    public NearestNeighborHybridAnnealer(int maxIterationsWeight, double acceptanceBreakpointScore) {
        this.maxIterationAnnealerMaxWeight = new MaxIterationAnnealer<>(
                maxIterationsWeight,
                new LinearTemperatureRegulator(maxIterationsWeight),
                new EverageMaskEvaluator(),
                new GreedyAcceptor(),
                new NearestNeighborGenerator(new WeightedRandomSelector<>())
        );

        this.acceptanceBreakpointAnnealer = new AcceptanceBreakpointAnnealer<>(
                acceptanceBreakpointScore,
                new LogTemperatureRegulator(),
                new EverageMaskEvaluator(),
                new StandardAcceptor(),
                new NearestNeighborGenerator(new MaximumScoreSelector<>(new EverageMaskEvaluator()))
        );
    }

    @Override
    public VertexCanning optimize(VertexCanning seed) {
        VertexCanning intermediate = maxIterationAnnealerMaxWeight.optimize(seed);
        return acceptanceBreakpointAnnealer.optimize(intermediate);
    }
}
