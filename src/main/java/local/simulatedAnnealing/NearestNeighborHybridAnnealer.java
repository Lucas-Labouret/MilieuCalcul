package local.simulatedAnnealing;

import local.computingMedia.cannings.vertexCannings.VertexCanning;
import local.simulatedAnnealing.acceptor.GreedyAcceptor;
import local.simulatedAnnealing.acceptor.StandardAcceptor;
import local.simulatedAnnealing.evaluator.EverageMaskEvaluator;
import local.simulatedAnnealing.neighborGenerator.NearestNeighborGenerator;
import local.simulatedAnnealing.neighborGenerator.neighborSelector.MaximumScoreSelector;
import local.simulatedAnnealing.neighborGenerator.neighborSelector.MaximumWeightSelector;
import local.simulatedAnnealing.neighborGenerator.neighborSelector.WeightedRandomSelector;
import local.simulatedAnnealing.temperatureRegulator.StandardTemperatureRegulator;

/**
 * Using only the maximum weight metric yields mediocre results, but max score is terribly slow,
 * so we try a hybrid approach, first optimizing for maximum weight, then refining the solution with maximum score.
 */
public class NearestNeighborHybridAnnealer extends Annealer<VertexCanning> {
    private final StandardAnnealer<VertexCanning> standardAnnealerMaxWeight;
    private final StandardAnnealer<VertexCanning> standardAnnealerMaxScore;

    public NearestNeighborHybridAnnealer(int maxIterationsWeight, int maxIterationsScore) {
        this.standardAnnealerMaxWeight = new StandardAnnealer<>(
                maxIterationsWeight,
                new StandardTemperatureRegulator(maxIterationsWeight),
                new EverageMaskEvaluator(),
                new GreedyAcceptor(),
                new NearestNeighborGenerator(new WeightedRandomSelector<>())
        );

        this.standardAnnealerMaxScore = new StandardAnnealer<>(
                maxIterationsScore,
                new StandardTemperatureRegulator(maxIterationsScore),
                new EverageMaskEvaluator(),
                new StandardAcceptor(),
                new NearestNeighborGenerator(new MaximumScoreSelector<>(new EverageMaskEvaluator()))
        );
    }

    @Override
    public VertexCanning optimize(VertexCanning seed) {
        VertexCanning intermediate = standardAnnealerMaxWeight.optimize(seed);
        return standardAnnealerMaxScore.optimize(intermediate);
    }
}
