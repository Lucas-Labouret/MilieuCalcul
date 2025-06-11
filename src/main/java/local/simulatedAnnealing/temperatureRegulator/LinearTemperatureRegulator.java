package local.simulatedAnnealing.temperatureRegulator;

/**
 * Standard implementation of the Acceptor interface for simulated annealing.
 * This implementation has temperature that decreases linearly from 1 to 0 over a set number of iterations.
 */
public class LinearTemperatureRegulator implements TemperatureRegulator {
    private final int maxIterations;
    private int currentIteration;

    public LinearTemperatureRegulator(int maxIterations) {
        this.maxIterations = maxIterations;
        this.currentIteration = 0;
    }

    @Override
    public double progress() {
        if (currentIteration >= maxIterations) {
            return 0;
        }
        double progress = (double) currentIteration / maxIterations;
        currentIteration++;
        return 1 - progress; // Decrease heat over time
    }
}
