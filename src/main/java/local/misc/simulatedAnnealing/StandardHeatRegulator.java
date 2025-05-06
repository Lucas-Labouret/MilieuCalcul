package local.misc.simulatedAnnealing;

public class StandardHeatRegulator implements HeatRegulator {
    private final int maxIterations;
    private int currentIteration;

    public StandardHeatRegulator(int maxIterations) {
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
