package simulatedAnnealing.neighborGenerator;

import computingMedia.cannings.vertexCannings.RoundedCoordIncrementalVCanning;
import computingMedia.cannings.vertexCannings.VertexCanning;
import simulatedAnnealing.evaluator.EverageMaskEvaluator;
import simulatedAnnealing.neighborGenerator.neighborSelector.MaximumScoreSelector;
import simulatedAnnealing.neighborGenerator.neighborSelector.NeighborSelector;

public class RCIVCGenerator implements NeighborGenerator<VertexCanning> {
    @Override
    public VertexCanning generate(VertexCanning c) {
        RoundedCoordIncrementalVCanning candidate = (RoundedCoordIncrementalVCanning) c;
        final NeighborSelector<VertexCanning> selector = new MaximumScoreSelector<>(new EverageMaskEvaluator());
        final double candidateIncrement = candidate.getIncrement();
        double newIncrement = candidateIncrement;
        for (int i = 0; i < 10; i++) {
            newIncrement *= 0.9;
            selector.add(0, new RoundedCoordIncrementalVCanning(candidate.getMedium(), newIncrement));
        }
        newIncrement = candidateIncrement;
        for (int i = 0; i < 10; i++) {
            newIncrement *= 1.1;
            selector.add(0, new RoundedCoordIncrementalVCanning(candidate.getMedium(), newIncrement));
        }

        return selector.next();
    }
}
