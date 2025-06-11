package local.simulatedAnnealing;

import local.simulatedAnnealing.acceptor.Acceptor;
import local.simulatedAnnealing.evaluator.Evaluator;
import local.simulatedAnnealing.neighborGenerator.NeighborGenerator;
import local.simulatedAnnealing.temperatureRegulator.TemperatureRegulator;

/**
 * Annealer that stops when the acceptance rate falls below a certain threshold.
 */
public class AcceptanceBreakpointAnnealer<C> extends Annealer<C> {
    private final double acceptanceBreakpoint;

    public AcceptanceBreakpointAnnealer(
            double acceptanceBreakpoint,
            TemperatureRegulator temperatureRegulator,
            Evaluator<C> evaluator,
            Acceptor acceptor,
            NeighborGenerator<C> neighborGenerator
    ) {
        this.acceptanceBreakpoint = acceptanceBreakpoint;
        this.temperatureRegulator = temperatureRegulator;
        this.evaluator = evaluator;
        this.acceptor = acceptor;
        this.neighborGenerator = neighborGenerator;
    }

    @Override
    public C optimize(C seed) {
        System.out.println("Starting optimization...");

        int accepted = 0;
        int iteration = 1;
        double acceptance;

        C candidate = seed;
        double score = evaluator.evaluate(candidate);
        double newScore;
        do {
            System.out.println("Iteration " + iteration);
            double temperature = temperatureRegulator.progress();
            C neighbor = neighborGenerator.generate(candidate);
            newScore = evaluator.evaluate(neighbor);
            if (acceptor.accept(score, newScore, temperature)) {
                System.out.println("    Accepted new candidate with score: " + newScore);
                accepted++;
                candidate = neighbor;
                score = newScore;
            } else {
                System.out.println("    Rejected new candidate with score: " + newScore);
            }
            acceptance = accepted / (double) iteration++;
            System.out.println("    Acceptance rate: " + acceptance);
        } while (acceptance > acceptanceBreakpoint);
        return candidate;
    }
}
