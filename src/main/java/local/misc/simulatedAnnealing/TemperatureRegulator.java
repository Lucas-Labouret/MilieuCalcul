package local.misc.simulatedAnnealing;

@FunctionalInterface
public interface TemperatureRegulator {
    /**
     * Provides the current temperature in the simulated annealing process.
     * The temperature should decrease over time, simulating the cooling process.
     *
     * @return the current temperature
     */
    double progress();
}
