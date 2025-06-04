package local.misc.simulatedAnnealing;

/**
 * Standard implementation of the Acceptor interface for simulated annealing.
 * This implementation runs a set number of iteration before stopping.
 */
public class StandardAnnealer<C> extends Annealer<C> {
    int maxIterations;

    public StandardAnnealer(
            int maxIterations,
            TemperatureRegulator temperatureRegulator,
            Evaluator<C> evaluator,
            Acceptor acceptor,
            RandomNeighborGenerator<C> randomNeighborGenerator
    ) {
        this.maxIterations = maxIterations;
        this.temperatureRegulator = temperatureRegulator;
        this.evaluator = evaluator;
        this.acceptor = acceptor;
        this.randomNeighborGenerator = randomNeighborGenerator;
    }

    public C optimize(C seed) {
        System.out.println("Starting optimization...");
        C candidate = seed;
        double oldScore;
        double newScore = evaluator.evaluate(candidate);
        for (int i = 0; i < maxIterations; i++) {
            System.out.println("Iteration " + (i+1) + " of " + maxIterations);
            double heat = temperatureRegulator.progress();
            C neighbor = randomNeighborGenerator.generate(candidate);
            oldScore = newScore;
            newScore = evaluator.evaluate(neighbor);
            if (acceptor.accept(oldScore, newScore, heat)) candidate = neighbor;
        }
        return candidate;
    }
}
