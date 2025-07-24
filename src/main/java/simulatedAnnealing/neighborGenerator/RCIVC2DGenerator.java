package simulatedAnnealing.neighborGenerator;

import cannings.vertexCannings.RoundedCoordIncremental2DVCanning;
import cannings.vertexCannings.RoundedCoordIncrementalVCanning;
import cannings.vertexCannings.VertexCanning;
import simulatedAnnealing.evaluator.EverageMaskEvaluator;
import simulatedAnnealing.neighborGenerator.neighborSelector.MaximumScoreSelector;
import simulatedAnnealing.neighborGenerator.neighborSelector.NeighborSelector;

public class RCIVC2DGenerator implements NeighborGenerator<VertexCanning> {
    @Override
    public VertexCanning generate(VertexCanning c) {
        RoundedCoordIncremental2DVCanning candidate = (RoundedCoordIncremental2DVCanning) c;
        final NeighborSelector<VertexCanning> selector = new MaximumScoreSelector<>(new EverageMaskEvaluator());
        final double candidateIncrement = candidate.getIncrement();
        double newIncrement = candidateIncrement;
        for (int i = 0; i < 10; i++) {
            newIncrement *= 0.9;
            selector.add(0, new RoundedCoordIncremental2DVCanning(candidate.getMedium(), newIncrement));
        }
        newIncrement = candidateIncrement;
        for (int i = 0; i < 10; i++) {
            newIncrement *= 1.1;
            selector.add(0, new RoundedCoordIncremental2DVCanning(candidate.getMedium(), newIncrement));
        }

        return selector.next();
    }
}
