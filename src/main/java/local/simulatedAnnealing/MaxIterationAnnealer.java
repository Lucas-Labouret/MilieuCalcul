package local.simulatedAnnealing;

import local.simulatedAnnealing.acceptor.Acceptor;
import local.simulatedAnnealing.evaluator.Evaluator;
import local.simulatedAnnealing.neighborGenerator.NeighborGenerator;
import local.simulatedAnnealing.temperatureRegulator.TemperatureRegulator;

/**
 * Runs a set number of iteration before stopping.
 */
public class MaxIterationAnnealer<C> extends Annealer<C> {
    private final int maxIterations;

    public MaxIterationAnnealer(
            int maxIterations,
            TemperatureRegulator temperatureRegulator,
            Evaluator<C> evaluator,
            Acceptor acceptor,
            NeighborGenerator<C> neighborGenerator
    ) {
        this.maxIterations = maxIterations;
        this.temperatureRegulator = temperatureRegulator;
        this.evaluator = evaluator;
        this.acceptor = acceptor;
        this.neighborGenerator = neighborGenerator;
    }

    public C optimize(C seed) {
        System.out.println("Starting optimization...");
        int accepted = 0;
        C candidate = seed;
        double score = evaluator.evaluate(candidate);
        double newScore;
        for (int i = 0; i < maxIterations; i++) {
            System.out.println("Iteration " + (i+1) + " of " + maxIterations);
            double temperature = temperatureRegulator.progress();
            C neighbor = neighborGenerator.generate(candidate);
            newScore = evaluator.evaluate(neighbor);
            if (acceptor.accept(score, newScore, temperature)) {
                System.out.println("    Accepted new candidate with score: " + newScore);
                accepted++;
                candidate = neighbor;
                score = newScore;
            }
            else {
                System.out.println("    Rejected new candidate with score: " + newScore);
            }
            System.out.println("    Acceptance rate: " + (accepted / (double)(i + 1)));
        }
        return candidate;
    }
}
