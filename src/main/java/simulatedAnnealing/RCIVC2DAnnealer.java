package simulatedAnnealing;

import cannings.vertexCannings.RoundedCoordIncrementalVCanning;
import cannings.vertexCannings.VertexCanning;
import simulatedAnnealing.acceptor.StandardAcceptor;
import simulatedAnnealing.evaluator.EverageMaskEvaluator;
import simulatedAnnealing.neighborGenerator.RCIVC2DGenerator;
import simulatedAnnealing.neighborGenerator.RCIVCGenerator;
import simulatedAnnealing.temperatureRegulator.LinearTemperatureRegulator;

/**
 * RCIVCAnnealer is a specialized annealer for RoundedCoordIncrementalVCanning
 * It optimizes the vertex canning by adjusting the increment of the coordinates.
 */
public class RCIVC2DAnnealer extends MaxIterationAnnealer<VertexCanning> {
    public RCIVC2DAnnealer(int maxIterations) {
        super(
                maxIterations,
                new LinearTemperatureRegulator(maxIterations),
                new EverageMaskEvaluator(),
                new StandardAcceptor(),
                new RCIVC2DGenerator()
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
