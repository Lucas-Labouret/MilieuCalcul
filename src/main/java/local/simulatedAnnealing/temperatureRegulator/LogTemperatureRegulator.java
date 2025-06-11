package local.simulatedAnnealing.temperatureRegulator;

public class LogTemperatureRegulator implements TemperatureRegulator {
    private int iteration = 0;

    @Override
    public double progress() {
        return 1.0 / Math.pow(Math.log(Math.E + iteration++), 2);
    }
}
