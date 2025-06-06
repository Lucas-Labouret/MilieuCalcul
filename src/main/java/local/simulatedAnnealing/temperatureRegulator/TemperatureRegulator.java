package local.simulatedAnnealing.temperatureRegulator;

@FunctionalInterface
public interface TemperatureRegulator {
    /**
     * Provides the current temperature in the simulated annealing process.
     * The temperature should decrease over time, starting from 1 and converging to 0.
     *
     * @return the current temperature
     */
    double progress();
}
