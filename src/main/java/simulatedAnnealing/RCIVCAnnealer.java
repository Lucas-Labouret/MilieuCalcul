package simulatedAnnealing;

import computingMedia.cannings.vertexCannings.RoundedCoordIncrementalVCanning;
import computingMedia.cannings.vertexCannings.VertexCanning;
import simulatedAnnealing.acceptor.StandardAcceptor;
import simulatedAnnealing.evaluator.EverageMaskEvaluator;
import simulatedAnnealing.neighborGenerator.RCIVCGenerator;
import simulatedAnnealing.temperatureRegulator.LinearTemperatureRegulator;

public class RCIVCAnnealer extends MaxIterationAnnealer<VertexCanning> {
    public RCIVCAnnealer(int maxIterations) {
        super(
                maxIterations,
                new LinearTemperatureRegulator(maxIterations),
                new EverageMaskEvaluator(),
                new StandardAcceptor(),
                new RCIVCGenerator()
        );
    }

    @Override
    public VertexCanning optimize(VertexCanning seed) {
        VertexCanning result = super.optimize(seed);
        try {
            System.out.println("Increment : " + ((RoundedCoordIncrementalVCanning) seed).getIncrement() +
                               " -> "         + ((RoundedCoordIncrementalVCanning) result).getIncrement());
        }
        catch (ClassCastException e) {
            System.out.println("Seed is not a RoundedCoordIncrementalVCanning instance.");
        }
        return result;
    }
}
